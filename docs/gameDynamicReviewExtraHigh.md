No files were modified. The working tree is clean.

## Correctness

### 1. Medium — Puzzle actions do not enforce destination or unlocking

The ViewModel’s puzzle methods can mutate either exhibit regardless of the current destination:

- [MuseumGameViewModel.kt:104](C:/Users/nmale/Documents/games/code/MuseumGame/app/src/main/java/com/example/museumgame/viewmodel/MuseumGameViewModel.kt:104)
- [MuseumGameViewModel.kt:116](C:/Users/nmale/Documents/games/code/MuseumGame/app/src/main/java/com/example/museumgame/viewmodel/MuseumGameViewModel.kt:116)

Likewise, `MuseumGame.answerSlightlyWrong()` does not check whether Slightly Wrong is unlocked at [MuseumGame.kt:77](C:/Users/nmale/Documents/games/code/MuseumGame/app/src/main/java/com/example/museumgame/game/MuseumGame.kt:77).

The current Compose wiring only exposes the appropriate callback on the appropriate screen, so this is not presently user-triggerable. However, a direct call can solve or advance Slightly Wrong while the Pen is unfinished, creating:

- Slightly Wrong completed;
- Slightly Wrong still reported as locked;
- Pen still considered current.

Recommendation: guard ViewModel actions by both current destination and unlocking. Because progression is a domain rule, `MuseumGame` should also reject attempts to play a locked exhibit. A small no-op or explicit locked result is sufficient; a generalized action framework is not justified.

### 2. Medium — Exhibit-specific restart permits non-prefix progress

A valid user flow can produce:

1. Solve Pen.
2. Make progress in or complete Slightly Wrong.
3. Revisit Pen.
4. Restart Pen.

`restartCurrentExhibit()` resets only the selected puzzle at [MuseumGameViewModel.kt:128](C:/Users/nmale/Documents/games/code/MuseumGame/app/src/main/java/com/example/museumgame/viewmodel/MuseumGameViewModel.kt:128). Slightly Wrong may then remain partially progressed or completed while its prerequisite is unsolved.

The UI handles this deterministically:

- Partial Slightly Wrong becomes locked but retains hidden progress.
- Completed Slightly Wrong remains revisitable because completed status overrides unlocking at [MuseumEntranceContent.kt:154](C:/Users/nmale/Documents/games/code/MuseumGame/app/src/main/java/com/example/museumgame/ui/MuseumEntranceContent.kt:154).

This is not a crash, but the semantics are currently undefined.

Recommendation: explicitly choose and test one policy. The cleanest ordered-journey policy is for restarting an earlier exhibit to reset that exhibit and all later exhibits. If preserving downstream completion is intentional, document that completed exhibits remain revisitable even after a prerequisite is restarted.

### What is otherwise consistent

- Completion has one authoritative source: each puzzle state’s `solved`.
- Resume selects the first unfinished exhibit.
- Continue cannot run until the current destination is solved.
- Return to entrance changes navigation only.
- Restart museum resets both puzzles, attempts, feedback, statuses, and destination.
- Completed exhibits can be revisited.
- Locked unfinished exhibits cannot be opened through the entrance.
- Final Continue returns to the completed entrance.

## Architecture

### 3. Medium — Exhibit order and exhibit content are separate catalogs

`MuseumGame` receives `exhibits`, but separately hard-codes `orderedExhibitIds` at [MuseumGame.kt:6](C:/Users/nmale/Documents/games/code/MuseumGame/app/src/main/java/com/example/museumgame/game/MuseumGame.kt:6). The ViewModel separately constructs the exhibit list at [MuseumGameViewModel.kt:46](C:/Users/nmale/Documents/games/code/MuseumGame/app/src/main/java/com/example/museumgame/viewmodel/MuseumGameViewModel.kt:46).

If one list is updated without the other, the entrance can crash at [MuseumEntranceContent.kt:152](C:/Users/nmale/Documents/games/code/MuseumGame/app/src/main/java/com/example/museumgame/ui/MuseumEntranceContent.kt:152).

This becomes increasingly likely as the remaining exhibits are added.

Recommendation: establish one ordered ID catalog now. Either:

- derive `MuseumGame.orderedExhibitIds` from its ordered `exhibits` argument; or
- define the ordered IDs once in the model layer and use that list to construct ViewModel content.

This does not require a puzzle registry.

### 4. Low — Domain state is copied into several UI snapshots

`MuseumGame` owns puzzle states and attempts, while `MuseumUiState` copies:

- both puzzle states;
- both progress objects;
- derived visit statuses.

Every current mutation refreshes the relevant copies, so there is no observed inconsistency. As more exhibits are added, forgetting one assignment becomes more likely.

Recommendation: introduce one private ViewModel synchronization helper that rebuilds domain-derived UI state after game mutations. Avoid a public generic state map for now.

### 5. Scaling is acceptable for two exhibits

The explicit puzzle fields, actions, and `isCompleted()` branches are appropriate at the current size. A generic `Puzzle` interface or registry is still premature.

Revisit that decision when implementing the third exhibit. At that point, repeated coordination code—not the number of planned exhibits—should determine whether an abstraction is warranted.

## Usability and accessibility

### 6. Medium — Whole-museum restart is destructive and visually adjacent to exhibit restart

Both exhibit screens show “Restart” and “Restart museum” as neighboring buttons:

- [ReappearingPenContent.kt:73](C:/Users/nmale/Documents/games/code/MuseumGame/app/src/main/java/com/example/museumgame/ui/ReappearingPenContent.kt:73)
- [SlightlyWrongContent.kt:76](C:/Users/nmale/Documents/games/code/MuseumGame/app/src/main/java/com/example/museumgame/ui/SlightlyWrongContent.kt:76)

One tap on Restart museum permanently clears all current progress.

Recommendation:

- Rename “Restart” to “Restart exhibit.”
- Require confirmation before Restart museum.
- Keep the confirmation text explicit that all exhibits and attempts will reset.

This benefits touch users, switch users, and screen-reader users alike.

### 7. Low — Completion/status accessibility is mostly sound

Positive points:

- Screen titles are headings.
- Dynamic puzzle feedback uses polite live regions.
- Pen controls expose checked state.
- Locked exhibit labels explicitly include “Locked.”
- Disabled buttons retain meaningful text.
- Images have descriptions.
- All action areas remain scrollable.

A confirmation dialog for museum restart is the only currently justified accessibility change.

## Test coverage

### 8. Medium — Progression invariants are not tested against invalid action paths

Missing tests:

- Slightly Wrong cannot advance before Pen completion.
- A puzzle action for a non-current destination is ignored.
- Restarting an earlier exhibit after downstream progress follows the chosen policy.
- A completed-but-currently-locked status is either prohibited or intentionally supported.

These tests should be added with the correctness changes above.

### 9. Medium — Android system back is not exercised

`BackHandler` correctly returns any exhibit to the entrance at [MuseumScreen.kt:18](C:/Users/nmale/Documents/games/code/MuseumGame/app/src/main/java/com/example/museumgame/ui/MuseumScreen.kt:18), but instrumentation tests only verify the explicit back button.

Recommendation: add one activity-level test using Android back after making puzzle progress, then Resume and confirm that the progress remains.

### 10. Low — Entrance states receive limited UI-level coverage

Unit tests cover unlocking, Resume, Continue, completion, revisit, and museum restart. Instrumentation does not directly verify:

- Slightly Wrong’s locked button is disabled initially.
- Pen changes from Current to Revisit after completion.
- Complete Visit returns to the completed entrance.
- Resume is absent after all exhibits are complete.

One compact entrance/progression instrumentation test would cover these without creating a broad UI suite.

## Low-priority cleanup

### 11. `Exhibit` contains legacy fields that are no longer consumed

The playable UI uses exhibit IDs and string-resource mappings. `name`, `description`, and `isAnomaly` appear to be remnants of the original generic anomaly game.

Recommendation: leave them until the progression semantics are settled, then either reduce `Exhibit` to meaningful platform-neutral metadata or remove it in favor of the ordered ID catalog. This is cleanup, not a current defect.

### 12. No obsolete hall navigation remains

No old `Hall` destination, `returnToHall`, hall title resource, or free-selection callback remains. The `museum_hall` drawable filename is simply the reused entrance artwork and does not require renaming.

## Lifecycle assessment

Ordinary configuration changes are sound:

- The activity-scoped ViewModel survives recreation.
- Destination, puzzle progress, attempts, completion, and feedback live in the ViewModel.
- An instrumentation test already verifies exhibit destination and Pen progress across recreation.

Process death still resets the museum because there is no `SavedStateHandle` or persistence. That matches the explicit policy and should not be treated as a defect yet.