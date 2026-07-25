No files were changed. The repository has five remaining exhibit images, so the honest batching is `2 + 2 + 1`; inventing an eighth exhibit merely to complete a third pair would be premature.

## Recommended museum order

| Order | Exhibit | Core skill | Batch |
|---:|---|---|---|
| 1 | The Reappearing Pen | Rechecking assumptions | Existing |
| 2 | Slightly Wrong | Matching clues to anomalies | Existing |
| 3 | Work Apparent | Recognizing and breaking a loop | 1 |
| 4 | Simulated Progress | Distinguishing activity, output, and impact | 1 |
| 5 | Near Occurrence | Controlling a moment at its threshold | 2 |
| 6 | Kubernetes City | Ordering system dependencies | 2 |
| 7 | Creative Chaos | Combining fragments into something new | 3/finale |

This produces a progression from observation, through process and systems reasoning, to constructive synthesis.

## Architecture preparation before Batch 1

These changes are justified by repetition already present or inevitable at seven exhibits. They should be a small behavior-preserving preparation task.

1. Centralize UI resource metadata.

   Add a UI-only `ExhibitUiResources` mapping containing `nameRes`, `drawableRes`, and `imageDescriptionRes`. Keep [ExhibitCatalog.kt](C:/Users/nmale/Documents/games/code/MuseumGame/app/src/main/java/com/example/museumgame/model/ExhibitCatalog.kt) platform-neutral.

2. Extract shared exhibit action chrome.

   A small `ExhibitNavigationActions` composable can own Continue/Complete, Restart exhibit, Restart museum, and Back to entrance. Puzzle controls remain exhibit-specific.

3. Replace separate attempt properties with private catalog-keyed bookkeeping.

   `MuseumGame` can have private `attemptsByExhibitId`, plus `progressFor(id)` and `recordAttempt(id)`. Each typed puzzle action still decides whether an attempt occurred.

4. Make feedback synchronization safe.

   Let `MuseumGame` retain each puzzle’s last typed feedback enum/result, clearing it when that puzzle is reset. Then the ViewModel’s existing refresh helper can become parameterless and rebuild all domain-derived UI state without seven feedback arguments.

5. Keep explicit puzzle dispatch.

   Retain typed methods such as `preserveNearOccurrence()` and explicit `isCompleted`/reset branches. Do not add:

   - A `Puzzle` interface.
   - `Map<String, Any>` puzzle state.
   - A generic action dispatcher.
   - A puzzle or composable registry.

6. Add fail-fast catalog coverage tests.

   A test should prove that every catalog ID has completion handling, restart handling, UI resources, and a screen route. Add catalog entries only when their entire vertical slice lands, avoiding an unlocked destination with no screen.

7. Add test-only journey helpers.

   Helpers such as `solveThrough(exhibitId)` will keep progression tests manageable. They belong under test sources, not production code.

## Batch 1: Work Apparent + Simulated Progress

These belong together because both examine work that looks productive. Their mechanics deliberately differ: the first makes the player experience and interrupt a loop; the second audits evidence.

### 3. Work Apparent

Asset: `work_apparent.png`  
Stable ID: `work_apparent`  
Display title: “Work Apparent”

#### Narrative purpose

The player moves from noticing visual anomalies to recognizing a process anomaly: paperwork circulates continuously while the underlying task remains untouched.

#### Puzzle mechanic and player flow

The player must trace one complete bureaucratic loop before the real exit becomes meaningful.

1. The document starts at `RECEIVED`.
2. `Organize papers` moves it to `ORGANIZED`.
3. `Rearrange papers` moves it to `REARRANGED`.
4. `Return to inbox` moves it back to `RECEIVED` and records that the loop was observed.
5. `Do the actual task` then solves the exhibit.
6. Actions used at the wrong stage leave state unchanged and explain the mismatch.
7. Choosing `Do the actual task` before observing the loop returns “Trace the paperwork loop first.”

All four controls remain explicit; there are no image hotspots.

#### Feedback

- Organize: “The papers are tidier. The task itself is unchanged.”
- Rearrange: “The pile moved. The task itself is unchanged.”
- Return: “The same task is back where it began. The loop is exposed.”
- Wrong stage: “That station cannot receive the document yet.”
- Actual work too early: “First trace why the task never leaves the process.”
- Solved: “The task—not its paperwork—is finally complete.”
- Already solved: the standard explicit already-solved result.

#### Platform-neutral state

```kotlin
enum class WorkStage {
    RECEIVED,
    ORGANIZED,
    REARRANGED
}

enum class WorkAction {
    ORGANIZE,
    REARRANGE,
    RETURN_TO_INBOX,
    DO_ACTUAL_TASK
}

data class WorkApparentState(
    val stage: WorkStage = WorkStage.RECEIVED,
    val loopObserved: Boolean = false,
    val solved: Boolean = false
)
```

Feedback and result types remain specific to this puzzle.

#### Attempts and restart

- Every actionable process choice counts, including wrong-stage choices.
- Navigation, disabled controls, locked calls, and already-solved calls do not count.
- Minimum successful run: four attempts.
- Restart resets stage, loop flag, solved state, feedback, and attempts.
- Cascading restart also resets all later exhibits while preserving Pen and Slightly Wrong.
- Returning to the entrance preserves the current station and loop flag.

#### UI

- Portrait: full-width illustration, current-station card, feedback, attempts, and a 2×2 action grid inside the existing full-screen scroll.
- Landscape: illustration left; current station and controls in the independently scrollable right pane.
- `Do the actual task` should remain available so an early choice can produce meaningful feedback, rather than silently hiding the deduction.

#### Accessibility

- Announce station and feedback changes with a polite live region.
- Give each action a state description such as “Available at current station” or “Not the next station.”
- Describe the illustration as a paper-filled office whose four trays form a closed loop.
- Do not require reading the small labels embedded in the image.

#### Changes

- Domain: new `WorkApparentPuzzle.kt`; add ID/catalog entry; typed state/action/result in `MuseumGame`; completion and restart branches.
- ViewModel: `WorkApparentUiState`, one typed action method, synchronization and destination guards.
- UI: copy asset to `drawable-nodpi`; add `WorkApparentContent.kt`, strings, previews, resource mapping, and explicit `MuseumScreen` case.
- Tests: new puzzle tests plus progression, ViewModel, Compose, and one concise Batch 1 activity flow.

#### Risks

- The sequence can feel deterministic. Wrong-stage feedback and the requirement to expose the loop before breaking it provide the deduction.
- It overlaps thematically with Simulated Progress; screen titles and supporting copy must emphasize “experience the loop” versus “audit the metrics.”

#### Acceptance criteria

- The exact three-station loop sets `loopObserved`.
- Actual work cannot solve before the loop is observed.
- Actual work solves after the loop.
- Attempt rules match the specification.
- Hall return preserves state.
- Restart clears this exhibit and all later exhibits.
- Controls and feedback remain reachable in portrait and landscape.

### 4. Simulated Progress

Asset: `simulate_progress.png`  
Stable ID: `simulated_progress`  
Display title: “Simulated Progress”

#### Narrative purpose

After breaking one visible process loop, the player learns why organizations mistake motion for improvement.

#### Puzzle mechanic and player flow

Classify three records using three definitions shown on-screen:

- Activity: work was performed.
- Output: something was produced.
- Impact: someone’s outcome changed.

Records:

- “Attend another alignment meeting.” → `ACTIVITY`
- “Publish a reviewed prototype.” → `OUTPUT`
- “A visitor completes the task faster.” → `IMPACT`

Flow:

1. View one record at a time.
2. Choose Activity, Output, or Impact.
3. Use Previous/Next to review assignments.
4. Press `Check the metrics`.
5. An incomplete audit gives guidance without counting an attempt.
6. An incorrect complete audit reports the number correct and retains selections.
7. All three correct classifications solve the exhibit.

#### Feedback

- Incomplete: “Classify all three signs of progress.”
- Incorrect: “Two of three measures fit. Activity, output, and impact are not interchangeable.”
- Correct: “The board finally distinguishes motion, production, and meaningful change.”
- Already solved and locked results remain explicit.

#### Platform-neutral state

```kotlin
enum class ProgressSignal {
    ALIGNMENT_MEETING,
    REVIEWED_PROTOTYPE,
    FASTER_VISITOR
}

enum class ProgressKind {
    ACTIVITY,
    OUTPUT,
    IMPACT
}

data class SimulatedProgressState(
    val assignments: Map<ProgressSignal, ProgressKind> = emptyMap(),
    val focusedSignal: ProgressSignal = ProgressSignal.ALIGNMENT_MEETING,
    val solved: Boolean = false
)
```

An incorrect feedback result may carry `correctCount`; localized copy remains in UI resources.

#### Attempts and restart

- Selecting or changing a classification does not count.
- Previous/Next does not count.
- Checking an incomplete audit does not count.
- Checking a complete audit counts once.
- Restart clears assignments, focused record, solved state, feedback, and attempts.
- Hall return preserves all assignments.

#### UI

- Portrait: image, concise definitions, one record card, three radio choices, Previous/Next, and Check.
- Landscape: image left and scrollable audit form right.
- Avoid rendering nine simultaneous controls; the single-record presentation is more usable with TalkBack and on small screens.

#### Accessibility

- Use radio/selectable-group semantics.
- Read the record before its three choices.
- State descriptions should include the saved classification.
- Feedback is a polite live region.
- Definitions must be visible text; the player must not read the illustration’s whiteboard.

#### Changes

- Domain: new `SimulatedProgressPuzzle.kt`; catalog/ID; typed MuseumGame methods for assignment, record navigation, and checking.
- ViewModel: typed UI state and callbacks with current-destination guards.
- UI: asset copy, `SimulatedProgressContent.kt`, resource strings/descriptions, previews, screen route.
- Tests: assignment/reassignment, incomplete audit, correct-count feedback, solving, attempts, restart, navigation preservation, and Compose radio semantics.

#### Risks

- “Output” versus “impact” can sound like workplace jargon. Definitions and examples must be plain language.
- Record navigation is puzzle progress and should live in platform-neutral state, not `remember` inside Compose.

#### Acceptance criteria

- All classifications persist through record navigation and entrance round-trips.
- Only a complete audit increases attempts.
- Incorrect audits preserve assignments.
- The exact mapping solves.
- Restart clears the entire audit.
- Solving unlocks Near Occurrence and no later exhibit.

## Batch 2: Near Occurrence + Kubernetes City

These form a cause-and-effect chapter. Near Occurrence is a compact turn-based timing state machine; Kubernetes City scales ordered reasoning into a longer dependency route. Implement Near Occurrence first so it becomes the low-risk proving ground for the batch.

### 5. Near Occurrence

Asset: `near_occurrence.png`  
Stable ID: `near_occurrence`  
Display title: “Near Occurrence”

#### Narrative purpose

This exhibit slows the journey down. The player learns that the museum preserves thresholds—events that nearly happened—not only mistakes and contradictions.

#### Puzzle mechanic and player flow

A turn-based timing puzzle with no clock or animation dependency.

1. The cup begins `SETTLED`.
2. `Advance the moment` moves it to `SHIFTING`.
3. Advance again moves it to `AT_THRESHOLD`: the cup is tipping, but the table remains dry.
4. `Preserve this moment` at the threshold solves.
5. Preserving earlier reports “Too soon.”
6. Advancing from the threshold causes the spill and resets the sequence.

#### Feedback

- First advance: “The cup edges toward the table’s rim.”
- Threshold: “The cup has tipped, but the table is still dry.”
- Too soon: “Nothing is close enough to becoming.”
- Too late/reset: “The spill happened. The room quietly resets the moment.”
- Solved: “The moment is preserved exactly before consequence.”
- Explicit locked/already-solved results.

#### Platform-neutral state

```kotlin
enum class NearOccurrenceStage {
    SETTLED,
    SHIFTING,
    AT_THRESHOLD
}

data class NearOccurrenceState(
    val stage: NearOccurrenceStage = NearOccurrenceStage.SETTLED,
    val solved: Boolean = false
)
```

Actions are `advance()` and `preserve()`.

#### Attempts and restart

- Every accepted Advance or Preserve action counts.
- Minimum successful run: three attempts.
- Advancing past the threshold counts and resets the stage.
- Locked, non-current, and already-solved actions do not count.
- Restart returns to `SETTLED` and clears feedback/attempts.
- Returning to the entrance preserves the stage.

#### UI

- Portrait: illustration, stage card, feedback, attempts, and two large controls.
- Landscape: illustration left; compact controls right.
- No timer, animation, gesture, or coordinate input is needed.

#### Accessibility

- Stage and feedback are announced politely.
- The current stage has a text label and state description.
- Image description mentions the tipped cup over a dry table, open door, coat, boots, and suspended room.
- Do not communicate timing only through animation.

#### Changes

- Domain: `NearOccurrencePuzzle.kt`, ID/catalog entry, MuseumGame completion/action/restart branches.
- ViewModel: typed state and two guarded action methods.
- UI: image copy, content composable, strings, resource mapping, screen route, portrait/landscape previews.
- Tests: early preservation, threshold solve, over-advance reset, attempt counts, restart, hall preservation, locked/non-current actions, and feedback UI.

#### Risks

- The optimal path is short and can be brute-forced. That is acceptable for a quiet transitional exhibit; the value is its deterministic no-timer mechanic.
- Animation would introduce lifecycle and testing complexity and should not be added.

#### Acceptance criteria

- Two advances reach the threshold.
- Preservation only solves at the threshold.
- Over-advancing resets without solving.
- Attempts count every time-control action exactly once.
- State survives entrance return and configuration recreation.
- Restart resets it and all later exhibits.

### 6. The City of Strange Kubernetes Clusters

Asset: `kubernetes_city.png`  
Stable ID: `kubernetes_city`  
Display title: “The City of Strange Kubernetes Clusters”

#### Narrative purpose

The museum expands from a room-sized mystery to a city-sized system. The player must respect dependencies rather than merely identify an odd object.

#### Puzzle mechanic and player flow

Build a five-stop route for safely launching a service.

Available districts:

- Registry Bazaar — obtain the artifact.
- Governance Citadel — admit it under policy.
- Networking Labyrinth — establish a route.
- Canary Canyon — release it to a small audience.
- Observability Overlook — verify traffic and health.
- Chaos Garden — failure experiments; a decoy for this incident.

Correct route:

1. Registry Bazaar
2. Governance Citadel
3. Networking Labyrinth
4. Canary Canyon
5. Observability Overlook

Flow:

1. Select districts to append them to numbered route slots.
2. A selected district cannot be added twice.
3. `Undo last stop` removes one.
4. `Clear route` removes all.
5. `Test route` with fewer than five stops reports incomplete without counting.
6. Testing a full incorrect route counts and reports the length of the correct prefix.
7. Testing the exact route solves.

#### Feedback

- Incomplete: “The service still has an unmet dependency.”
- Prefix 0: “The city has nothing to deploy. Begin where artifacts are kept.”
- Prefix 1: “The artifact arrived, but policy has not admitted it.”
- Prefix 2: “Policy is clear, but the service has no path.”
- Prefix 3: “The path exists; release it cautiously before watching the whole city.”
- Prefix 4: “The release is live. Verify that traffic is healthy.”
- Solved: “Artifact, policy, path, release, and observation align. The city stabilizes.”

#### Platform-neutral state

```kotlin
enum class KubernetesDistrict {
    REGISTRY,
    GOVERNANCE,
    NETWORKING,
    CANARY,
    OBSERVABILITY,
    CHAOS_GARDEN
}

data class KubernetesCityState(
    val route: List<KubernetesDistrict> = emptyList(),
    val solved: Boolean = false
)
```

The result for a wrong route can carry `correctPrefixLength`.

#### Attempts and restart

- Adding, undoing, and clearing stops do not count.
- Testing an incomplete route does not count.
- Testing a complete route counts once.
- Duplicate additions are no-ops and do not count.
- Restart clears the route, feedback, attempts, and solved state.
- Entrance return preserves the draft route.

#### UI

- Portrait: full illustration, self-contained incident explanation, numbered route, district grid, Undo/Clear, and Test Route in the existing vertical scroll.
- Landscape: city map left; itinerary and controls right with independent scrolling.
- Every district’s role must be restated in the controls. Embedded map text is supplementary only.

#### Accessibility

- Number route slots and give each a readable state description.
- Selected district controls announce their assigned position.
- Never rely on neon district colors.
- Use concise plain-language definitions for artifact, policy, route, staged release, and observation.
- Announce itinerary and validation feedback politely.

#### Changes

- Domain: `KubernetesCityPuzzle.kt`, ID/catalog, typed route methods/results, completion/restart integration.
- ViewModel: typed route state plus add/undo/clear/test methods, all destination-guarded.
- UI: asset, `KubernetesCityContent.kt`, strings, resource mapping, screen route, responsive previews.
- Tests: duplicate prevention, route editing, incomplete validation, every prefix result, correct solution, attempts, restart, navigation preservation, and a focused Compose itinerary test.

#### Risks

- The image is exceptionally dense and its labels will be unreadable on many phones.
- Kubernetes knowledge cannot be assumed; the text panel must contain every required clue.
- The long exhibit title and six district labels need testing under increased font scale.
- Full end-to-end UI solving is long; favor domain/ViewModel coverage and one compact UI interaction test.

#### Acceptance criteria

- Route entries remain unique and ordered.
- Undo and Clear never increase attempts.
- Only complete validation increases attempts.
- Incorrect validation reports the correct prefix without modifying the route.
- The exact five-stop route solves.
- Restart and hall navigation obey museum-wide policies.
- Solving unlocks Creative Chaos.

## Batch 3: Creative Chaos + finale hardening

There are only five remaining exhibits, so this is one exhibit plus a second deliverable: full-journey completion, selector, accessibility, and instrumentation hardening.

### 7. Creative Chaos

Asset: `creative_chaos.png`  
Stable ID: `creative_chaos`  
Display title: “Creative Chaos”

#### Narrative purpose

The finale changes the player’s role. Previous exhibits were about finding, timing, classifying, or correcting anomalies; here the player deliberately creates order from fragments.

#### Puzzle mechanic and player flow

A three-step crafting puzzle using explicit selectable pieces.

Pieces:

- Raw: Grid, Sketch, Code, Note.
- Generated: Pattern, Motion.

Recipes:

1. `GRID + SKETCH` → `PATTERN`
2. `PATTERN + CODE` → `MOTION`
3. `MOTION + NOTE` → solved

Flow:

1. Select up to two available pieces.
2. Selected pieces use selected/state semantics.
3. Press `Combine fragments`.
4. Fewer than two selected returns incomplete feedback without counting.
5. A full incorrect pair counts, clears the selection, and leaves the current step unchanged.
6. A correct pair advances, consumes its inputs, exposes the generated piece, and clears selection.
7. The third correct collision solves.

#### Feedback

- Incomplete: “Select two fragments to combine.”
- Third selection: “Only two fragments can collide at once.”
- Wrong pair: “These fragments overlap, but no useful order emerges.”
- Pattern created: “A loose sketch finds structure in the grid.”
- Motion created: “Code teaches the new pattern how to move.”
- Solved: “The final note gives the moving pattern meaning. Chaos resolves into possibility.”

#### Platform-neutral state

```kotlin
enum class ChaosPiece {
    GRID,
    SKETCH,
    CODE,
    NOTE,
    PATTERN,
    MOTION
}

enum class CreativeChaosStep {
    FORM_PATTERN,
    ADD_MOTION,
    ADD_MEANING,
    COMPLETE
}

data class CreativeChaosState(
    val step: CreativeChaosStep = CreativeChaosStep.FORM_PATTERN,
    val selectedPieces: Set<ChaosPiece> = emptySet(),
    val solved: Boolean = false
)
```

Available pieces are derived from `step`; they do not need separate persisted state.

#### Attempts and restart

- Selection, deselection, and incomplete Combine do not count.
- Each complete two-piece combination counts once.
- Minimum successful run: three attempts.
- Restart clears step, selection, feedback, attempts, and solved state.
- As the final exhibit, there is no downstream puzzle to clear.
- Restarting an earlier exhibit still clears Creative Chaos through catalog-order cascading.

#### UI

- Portrait: illustration, current creation prompt, generated-piece summary, 2×2 or adaptive choice grid, Combine, feedback, and shared exhibit actions.
- Landscape: illustration left, crafting panel right.
- Keep the central illustration fully visible; no drag-and-drop or hotspots.

#### Accessibility

- Pieces announce Available, Selected, Consumed, or Generated.
- The current creative step is a heading.
- Feedback uses a polite live region.
- Pairing and generated pieces are expressed in text, not only visual transformations.
- Image description mentions code, grids, sketches, notes, and the fragmented central form.

#### Changes

- Domain: `CreativeChaosPuzzle.kt`, final ID/catalog entry, MuseumGame methods and final completion/reset branches.
- ViewModel: typed crafting UI state and toggle/combine methods.
- UI: image, content composable, strings, resource mapping, final screen route, previews.
- Tests: selection limit, incomplete combination, incorrect combination, order-insensitive recipes, each generated step, solving, restart, final completion, and full museum restart.

#### Risks

- Recipes may feel arbitrary unless each step prompt communicates its intent without directly giving the answer.
- Generated-piece availability should be derived, preventing contradictory persisted state.
- A full seven-exhibit activity test would be slow and brittle; most journey coverage should remain at domain/ViewModel level.

#### Acceptance criteria

- At most two pieces can be selected.
- Incorrect complete pairs count once and do not advance.
- Each correct recipe advances exactly one step.
- The third recipe solves and enables Complete visit.
- Completing returns to the entrance with Resume absent and every exhibit listed as completed.
- Restart museum clears all seven exhibits and returns to the entrance.

## Batch-level implementation and testing policy

Each two-exhibit batch can span multiple normal sessions:

1. Run the baseline tests.
2. Implement the first exhibit as a complete vertical slice.
3. Run unit and focused UI tests.
4. Implement the second exhibit.
5. Update batch progression/integration coverage.
6. Run all unit and available instrumentation tests.

For every batch:

- Add catalog entries only alongside a working domain state machine, UI route, resources, and tests.
- Add pure puzzle tests before UI implementation.
- Test locked domain calls, non-current ViewModel calls, already-solved calls, and exact attempt semantics.
- Test cascading restart in both directions: restarting the earlier batch exhibit clears both; restarting the later one preserves the earlier.
- Add one focused Compose test per exhibit, not a large matrix.
- Add one concise activity happy path per batch.
- Provide 412×915 and 915×412 previews.
- Test at increased font scale where controls or titles are dense.
- Keep all visible and accessibility text in resources.
- Preserve `ContentScale.Fit`, scrolling, Android back behavior, entrance Resume/selector behavior, and configuration-change state.

The main design risk is the strong thematic overlap between Work Apparent and Simulated Progress. Keeping their mechanics as “trace and interrupt a loop” versus “classify metrics” is essential; if playtesting still finds them repetitive, changing or combining one exhibit would be more justified than adding abstraction.