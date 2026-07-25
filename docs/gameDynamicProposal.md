No files changed.

## Proposed progression model

The museum order should be domain-owned and fixed:

```kotlin
val exhibitOrder = listOf(
    ExhibitIds.REAPPEARING_PEN,
    ExhibitIds.SLIGHTLY_WRONG
)
```

Future exhibits append to this list. Unlocking should always be derived from this order and exhibit completion rather than stored as separate mutable flags.

### Completion as one source of truth

The current model duplicates completion:

```text
ExhibitProgress.solved
ReappearingPenState.solved / SlightlyWrongState.solved
```

That duplication should be removed. Each puzzle state machine should remain the authoritative source for whether its exhibit is solved.

`ExhibitProgress` would then hold only attempts:

```kotlin
data class ExhibitProgress(
    val attempts: Int = 0
)
```

`MuseumGame` derives completion:

```kotlin
fun isCompleted(exhibitId: String): Boolean = when (exhibitId) {
    ExhibitIds.REAPPEARING_PEN -> reappearingPenState.solved
    ExhibitIds.SLIGHTLY_WRONG -> slightlyWrongState.solved
    else -> false
}
```

This guarantees progression, disabled controls, Continue availability, and entrance status all use the same completion fact.

## Domain progression rules

`MuseumGame` should add read-only progression queries:

```kotlin
val orderedExhibitIds: List<String>

fun isCompleted(exhibitId: String): Boolean

fun firstUnfinishedExhibitId(): String?

fun isUnlocked(exhibitId: String): Boolean

fun nextExhibitId(after: String): String?
```

Definitions:

- `firstUnfinishedExhibitId()` returns the first ordered exhibit whose puzzle is not solved.
- An exhibit is unlocked when every preceding exhibit is solved.
- Completed exhibits remain accessible regardless of the current point.
- `nextExhibitId()` returns the following exhibit in museum order.
- The first exhibit is always unlocked.
- When all exhibits are complete, `firstUnfinishedExhibitId()` returns `null`.

Unlock status should never be persisted independently:

```kotlin
isUnlocked(id) =
    every exhibit before id is completed
```

No puzzle interface or registry is required. `MuseumGame` can use explicit `when` branches for the two current puzzle types.

## Navigation model

Replace the current `Hall` destination and full `Exhibit` payload with ID-based destinations:

```kotlin
sealed interface MuseumDestination {
    data object Entrance : MuseumDestination
    data class ExhibitDetail(val exhibitId: String) : MuseumDestination
}
```

Using the stable ID avoids storing a second copy of the exhibit inside navigation state.

ViewModel navigation actions:

```kotlin
fun resumeVisit()
fun openExhibit(exhibitId: String)
fun continueVisit()
fun returnToEntrance()
fun restartMuseum()
```

Behavior:

- `resumeVisit()` opens `firstUnfinishedExhibitId()`.
- `openExhibit(id)` succeeds when the exhibit is completed or unlocked.
- Locked future exhibits ignore the request.
- `continueVisit()` only works when the current exhibit is solved.
- If another exhibit follows, Continue opens it.
- If the current exhibit is the final one, Continue returns to the entrance, where the museum is shown as complete.
- `returnToEntrance()` only changes navigation.
- `restartMuseum()` resets both puzzle state machines, attempts, feedback, and destination.

The current system back handler should return from any exhibit to `Entrance`.

## Entrance presentation

The entrance replaces the current free-selection hall screen.

It would show:

- Museum entrance illustration.
- A primary “Resume visit” button when an unfinished exhibit exists.
- Ordered exhibit entries with derived status:
  - Completed — enabled with “Revisit.”
  - First unfinished — enabled and marked “Current exhibit.”
  - Future — disabled and marked “Locked.”
- “Restart museum” available at all times.

When all exhibits are complete:

- “Resume visit” is hidden or disabled.
- A “Museum visit complete” message appears.
- Every exhibit remains available for revisiting.
- Restart museum remains available.

The image and responsive portrait/landscape structure can remain unchanged.

## Continue behavior inside exhibits

Each exhibit screen receives a derived `canContinue` and an `onContinue` callback.

- Before solving, Continue is absent or disabled.
- After solving, Continue appears.
- Restart remains exhibit-specific if retained.
- Back to museum entrance remains available at all times.
- On the final exhibit, the action could read “Complete visit” while using the same `continueVisit()` behavior.

Puzzle composables should not calculate ordering or unlocking.

## Restart behavior

`MuseumGame` gains:

```kotlin
fun restartMuseum() {
    restartReappearingPen()
    restartSlightlyWrong()
}
```

`MuseumGameViewModel.restartMuseum()` then:

1. Calls `game.restartMuseum()`.
2. Clears both feedback values.
3. Rebuilds both exhibit UI-state snapshots.
4. Sets destination to `Entrance`.

Current exhibit-specific Restart buttons can continue resetting only that exhibit. “Restart museum” at the entrance is the destructive whole-visit reset.

## State that becomes redundant

Remove or replace:

- `ExhibitProgress.solved`
  - Completion comes from the puzzle state.

- `MuseumDestination.ExhibitDetail(val exhibit: Exhibit)`
  - Store only `exhibitId`.

- Independently stored unlocked/current flags
  - Derive them from order and completion.

- Any global museum `solved` flag
  - Museum completion is `firstUnfinishedExhibitId() == null`.

- A separately mutable “current exhibit” field
  - The first unfinished exhibit is derived.

A small immutable entrance model may still be useful:

```kotlin
data class ExhibitVisitStatus(
    val exhibitId: String,
    val completed: Boolean,
    val unlocked: Boolean,
    val current: Boolean
)
```

These values should be regenerated from `MuseumGame`, not mutated independently.

## Minimal justified changes

Likely affected files:

- `ExhibitProgress.kt`
  - Remove `solved`.

- `MuseumGame.kt`
  - Add order, derived completion/unlocking queries, and whole-museum restart.

- `MuseumGameViewModel.kt`
  - Use ID-based navigation.
  - Add resume, continue, entrance, and museum-restart actions.
  - Build derived entrance status.

- `MuseumScreen.kt`
  - Route `Entrance` and exhibit IDs.
  - Pass Continue state/actions.

- `MuseumHallContent.kt`
  - Evolve into `MuseumEntranceContent`.
  - Render Resume, ordered status entries, revisits, locks, and Restart museum.

- `ReappearingPenContent.kt` and `SlightlyWrongContent.kt`
  - Add Continue/Complete Visit action after solving.

- `strings.xml`
  - Entrance, Resume, Continue, Complete Visit, Current, Completed, Locked, Revisit, and Restart Museum labels.

Tests should cover:

- Initial current exhibit is the pen.
- Slightly Wrong starts locked.
- Solving the pen unlocks Slightly Wrong.
- Resume opens the first unfinished exhibit.
- Continue advances only after completion.
- Returning to the entrance preserves progress.
- Completed exhibits can be revisited.
- Locked exhibits cannot be opened.
- Completing both exhibits produces no unfinished exhibit.
- Restart museum resets both puzzles, attempts, feedback, unlocking, and navigation.

This adds progression rules without coupling the two puzzle state machines or creating a generalized puzzle framework.