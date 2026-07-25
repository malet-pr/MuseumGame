The revised structure is cleaner: six playable exhibits followed by a non-playable epilogue. Creative Chaos becomes the emotional and mechanical climax; Kubernetes City becomes a glimpse beyond the museum.

No files were changed.

## Final progression

| Order | Destination | Role |
|---:|---|---|
| 1 | The Reappearing Pen | Recheck assumptions |
| 2 | Slightly Wrong | Interpret visual clues |
| 3 | Work Apparent | Trace and interrupt a process loop |
| 4 | Simulated Progress | Classify activity, output, and impact |
| 5 | Near Occurrence | Preserve an event at its threshold |
| 6 | Creative Chaos | Final playable puzzle; synthesize fragments |
| 7 | Kubernetes City | Non-playable completion epilogue and teaser |

Only the first six belong in the ordered `ExhibitCatalog`. Kubernetes City must not have exhibit progress, attempts, unlocking state, puzzle state, or an entrance-selector option.

## Revised implementation batches

### Architecture preparation

A small behavior-preserving preparation task:

- Add the UI-only exhibit resource mapping.
- Extract shared exhibit navigation/action chrome.
- Replace separate attempt counters with private catalog-keyed bookkeeping.
- Make typed feedback reset safely without a growing ViewModel parameter list.
- Add catalog coverage tests and test-only `solveThrough()` helpers.
- Add no future catalog entries during this preparation.

### Batch 1: Work Apparent + Simulated Progress

These remain paired because they form a coherent chapter about apparent productivity, while their moment-to-moment interactions remain deliberately different.

Implementation order:

1. Work Apparent vertical slice.
2. Simulated Progress vertical slice.
3. Batch progression and cascading-restart coverage.

### Batch 2: Near Occurrence + Creative Chaos + ending integration

The playable pair is Near Occurrence and Creative Chaos. Kubernetes City is completion work attached to the batch, not a third exhibit.

Implementation order:

1. Near Occurrence vertical slice.
2. Creative Chaos vertical slice.
3. Add the Finale destination and Kubernetes City epilogue.
4. Complete six-exhibit journey, accessibility, selector, and restart coverage.

This is the final implementation batch, and Creative Chaos is the final real puzzle within it. Each vertical slice can still be completed and tested in a separate Codex session.

## Progression and navigation model

Extend the existing destination type:

```kotlin
sealed interface MuseumDestination {
    data object Entrance : MuseumDestination
    data class ExhibitDetail(val exhibitId: String) : MuseumDestination
    data object Finale : MuseumDestination
}
```

Important rules:

- `ExhibitCatalog` ends with `CREATIVE_CHAOS`.
- Completing Creative Chaos makes every playable visit status completed.
- Activating Creative Chaos’s `Complete visit` action navigates to `Finale`, not directly to the entrance.
- The Finale has no puzzle state.
- Returning from the Finale goes to the completed entrance.
- Resume remains absent because `firstUnfinishedExhibitId()` is `null`.
- Completed playable exhibits remain revisitable through the entrance selector.
- Restart museum from the Finale uses the existing confirmation and resets all six puzzles.
- Android back from the Finale behaves like Return to entrance.
- Configuration recreation preserves the Finale destination through the ViewModel.
- No separate `museumCompleted` Boolean is needed; completion remains derived from all catalog puzzles being solved.

An optional later enhancement is a `View finale again` action at the completed entrance. It is useful but not required for the first ending slice.

# Playable exhibits

## 3. Work Apparent

Asset: `work_apparent.png`  
ID: `work_apparent`

### Narrative purpose

The player experiences a process that continuously reorganizes work without completing it, then interrupts that cycle.

### Exact mechanic

State begins at `RECEIVED`.

1. `Organize papers` → `ORGANIZED`.
2. `Rearrange papers` → `REARRANGED`.
3. `Return to inbox` → `RECEIVED`, with `loopObserved = true`.
4. `Do the actual task` becomes available and solves.

Wrong-stage process actions leave the state unchanged and immediately explain why the document cannot move there yet.

### Interaction character

Work Apparent should feel active and sequential:

- One action changes the station immediately.
- Feedback occurs after every action.
- The current station is always prominent.
- The player physically experiences the loop.
- There is no final multi-answer form or aggregate score.

`Do the actual task` should be visibly present but disabled until the loop is observed, with nearby explanatory text such as “Trace the paperwork loop first.”

### State

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

### Feedback

- Organize: “The papers are tidier. The task itself is unchanged.”
- Rearrange: “The pile moved. The task itself is unchanged.”
- Return: “The same task is back where it began. The loop is exposed.”
- Wrong station: “That station cannot receive the document yet.”
- Solved: “The task—not its paperwork—is finally complete.”

### Attempts and restart

- Every enabled process choice counts.
- Wrong-stage choices count.
- Disabled Actual Task, navigation, locked calls, and already-solved calls do not.
- Minimum successful run: four attempts.
- Restart resets stage, loop flag, feedback, attempts, and downstream exhibits.
- Entrance return preserves the current station.

### UI and accessibility

- Portrait: image, station card, feedback, attempts, process controls, shared room actions in one scroll.
- Landscape: image left, process panel right.
- Announce station and feedback changes politely.
- Give controls meaningful next-station state descriptions.
- Image description should explain the paper trays and closed cycle.
- Do not require reading labels embedded in the artwork.

### Tests

- Exact full loop.
- Wrong-stage no transition.
- Actual Task unavailable before the loop.
- Solving after the loop.
- Attempt counting.
- Locked and non-current guards.
- Entrance preservation.
- Restart resets Work and every later exhibit.

## 4. Simulated Progress

Asset: `simulate_progress.png`  
ID: `simulated_progress`

### Narrative purpose

The player learns to distinguish work being performed, something being produced, and an outcome genuinely changing.

### Exact mechanic

Classify three records:

- “Attend another alignment meeting.” → Activity
- “Publish a reviewed prototype.” → Output
- “A visitor completes the task faster.” → Impact

Player flow:

1. View one record at a time.
2. Choose Activity, Output, or Impact.
3. Move between records freely.
4. Press `Check the metrics`.
5. Incomplete classification gives guidance without counting.
6. A complete incorrect audit reports how many answers are correct and preserves all selections.
7. All three correct solves.

### Interaction character

Simulated Progress should feel reflective and form-like:

- Assignments do not cause immediate progression.
- Choices can be revised freely.
- Validation is deferred until Check.
- Feedback is aggregate rather than tied to a physical station.
- No process loop is enacted.

This keeps it functionally distinct from Work Apparent even though the themes overlap.

### State

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

### Feedback

- Incomplete: “Classify all three signs of progress.”
- Incorrect: “Two of three measures fit. Activity, output, and impact are not interchangeable.”
- Solved: “The board finally distinguishes motion, production, and meaningful change.”

### Attempts and restart

- Assignment and record navigation do not count.
- Incomplete validation does not count.
- Each complete validation counts once.
- Restart clears assignments, focus, feedback, attempts, and downstream exhibits.
- Entrance return preserves classifications.

### UI and accessibility

- Portrait: image, concise category definitions, one record card, radio choices, Previous/Next, Check.
- Landscape: image left, audit panel right.
- Use selectable-group and radio semantics.
- Announce the selected classification for each record.
- Keep all necessary definitions outside the illustration.
- Feedback is a polite live region.

### Tests

- Partial assignments.
- Reassignment.
- Incomplete validation.
- Correct-count feedback.
- Exact solve mapping.
- Attempt behavior.
- Restart directionality: restarting Simulated Progress preserves Work Apparent.
- Entrance/configuration preservation.

### Accepted overlap policy

Work Apparent and Simulated Progress remain separate unless implementation makes their actual interaction models functionally identical.

Thematic similarity alone is not a reason to merge them. The intended distinction is:

- Work Apparent: immediate state transitions, station-by-station feedback, expose and interrupt a loop.
- Simulated Progress: reversible classification, deferred submission, aggregate correctness feedback.

## 5. Near Occurrence

Asset: `near_occurrence.png`  
ID: `near_occurrence`

### Narrative purpose

A quiet transition before the finale. The player learns that some mysteries concern the exact boundary before an event becomes real.

### Exact mechanic

Turn-based timing with no real-time clock:

1. Start `SETTLED`.
2. Advance → `SHIFTING`.
3. Advance → `AT_THRESHOLD`.
4. Preserve at the threshold → solved.
5. Preserve earlier → too soon.
6. Advance from the threshold → the cup spills and the sequence resets.

### State

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

### Feedback

- Shifting: “The cup edges toward the table’s rim.”
- Threshold: “The cup has tipped, but the table is still dry.”
- Too soon: “Nothing is close enough to becoming.”
- Too late: “The spill happened. The room quietly resets the moment.”
- Solved: “The moment is preserved exactly before consequence.”

### Attempts and restart

- Every accepted Advance or Preserve counts.
- Minimum successful run: three attempts.
- Over-advancing counts before resetting.
- Restart returns to Settled and clears Near Occurrence plus Creative Chaos.
- Entrance return preserves the stage.

### UI and accessibility

- Two large controls: Advance and Preserve.
- No timers, animation dependency, or hotspots.
- Current stage and feedback are announced.
- Image description mentions the tipped cup above a dry table and the room’s other suspended events.
- Existing responsive shell is sufficient.

### Tests

- Early preserve.
- Threshold solve.
- Over-advance reset.
- Attempts.
- Hall and recreation preservation.
- Restart cascading into Creative Chaos.

## 6. Creative Chaos

Asset: `creative_chaos.png`  
ID: `creative_chaos`

### Why it is the final playable exhibit

Creative Chaos provides the strongest ending because it reverses the player’s role.

Earlier exhibits ask the player to:

- Find what moved.
- Identify what is wrong.
- Interrupt what is looping.
- Judge what counts as progress.
- Preserve what almost happened.

Creative Chaos asks the player to create something.

Its theme—order emerging where imagination collides—provides a positive resolution after several exhibits about failure, bureaucracy, and absence. The final combination produces “meaning,” which naturally leads into the larger, stranger world shown in Kubernetes City.

### Exact mechanic

Three-step crafting using explicit selectable pieces.

Recipes:

1. Grid + Sketch → Pattern
2. Pattern + Code → Motion
3. Motion + Note → Meaning and solve

Flow:

1. Select up to two currently available pieces.
2. Press `Combine fragments`.
3. Fewer than two selected returns incomplete feedback without counting.
4. A complete wrong pair counts, clears selection, and does not advance.
5. A correct pair generates the next piece and clears selection.
6. The third recipe solves the final playable exhibit.
7. `Complete visit` opens the Kubernetes City Finale.

### State

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

Available pieces are derived from the current step.

### Feedback

- Incomplete: “Select two fragments to combine.”
- Too many selected: “Only two fragments can collide at once.”
- Wrong pair: “These fragments overlap, but no useful order emerges.”
- Pattern: “A loose sketch finds structure in the grid.”
- Motion: “Code teaches the new pattern how to move.”
- Solved: “The final note gives the moving pattern meaning. Chaos resolves into possibility.”

### Attempts and restart

- Selection changes do not count.
- Incomplete Combine does not count.
- Every complete pair submission counts.
- Minimum successful run: three attempts.
- Restart clears Creative Chaos only; it has no downstream playable exhibit.
- Restarting any earlier exhibit still clears Creative Chaos through normal cascading.

### UI and accessibility

- Portrait: image, current creative prompt, generated-piece summary, selectable grid, Combine, shared actions.
- Landscape: image left, crafting panel right.
- Pieces announce Available, Selected, Consumed, or Generated.
- Current creative step is a heading.
- Generated pieces and feedback are announced in text.
- No drag-and-drop or visual-only transformation.

### Tests

- Two-piece selection limit.
- Incomplete submission.
- Wrong pair and cleared selection.
- Order-insensitive correct recipes.
- All three generated stages.
- Final solve.
- Complete visit opens `MuseumDestination.Finale`.
- Restart preserves all earlier completed exhibits.

# Kubernetes City Finale

Asset: `kubernetes_city.png`  
Not in `ExhibitCatalog`  
No playable ID required unless a UI-only stable key becomes useful.

## Epilogue role

Kubernetes City is not a room the player solves. It is the view beyond the museum after the final gallery opens.

It should communicate:

- The museum visit is complete.
- Every playable exhibit has been solved.
- A larger strange world exists beyond this game.
- Kubernetes City may represent a future or separate project.

The artwork can remain rich and mysterious without requiring the player to understand Kubernetes terminology.

## Suggested content

Heading:

> Museum visit complete

Completion message:

> You found every minor mystery. Beyond the final gallery, another city is waiting.

Optional teaser:

> Check out my next game: The City of Strange Kubernetes Clusters.

Keep the teaser in a string resource so it can be replaced later. Do not add a nonfunctional external-link button until there is a real destination.

## Actions

Required:

- `Return to entrance`
- `Restart museum`

Optional later:

- `View finale again` from the completed entrance.
- `Learn more` only when a real URL or destination exists.

Restart museum uses the existing destructive confirmation explaining that all progress and attempts will be cleared.

## UI

- Portrait: completion heading, full-width city image, completion/teaser text, actions, all inside a vertical scroll.
- Landscape: city image left and completion panel right.
- The existing responsive image/content pattern can be reused without describing this as an exhibit.
- No attempts, status counter, puzzle controls, Continue, or Restart exhibit.

## Accessibility

- Completion title is a heading.
- Image description summarizes a fantastical night city divided into Kubernetes-themed districts.
- The teaser does not depend on reading text embedded in the image.
- Action order is Return to entrance, then Restart museum.
- Completion messaging is announced once when the destination opens; avoid repeatedly announcing the entire screen.
- Android back returns to the completed entrance.

## Files affected when implemented

- `MuseumDestination`: add `Finale`.
- `MuseumGameViewModel`: final-completion navigation and return behavior; no finale puzzle state.
- `MuseumScreen`: explicit Finale branch.
- New `MuseumFinaleContent.kt`.
- Copy `kubernetes_city.png` to `drawable-nodpi`.
- Add completion, teaser, action, and image-description strings.
- Update Creative Chaos Continue/Complete wiring.
- Update entrance completed-state behavior if `View finale again` is included.
- Add focused finale and final-journey tests.

## Finale acceptance criteria

- Kubernetes City is absent from the ordered exhibit catalog.
- It never appears in the entrance exhibit selector.
- It has no unlocked/completed/current status or attempts.
- Solving Creative Chaos marks all playable exhibits complete.
- Creative Chaos’s completion action opens the Finale.
- Returning to the entrance preserves full completion and Resume remains absent.
- Android back from the Finale returns to the entrance.
- Activity recreation preserves the Finale.
- Restart museum from the Finale resets all six puzzles and returns to the entrance after confirmation.
- Completed exhibits remain revisitable.
- The image and actions remain accessible in portrait and landscape.

This produces exactly six playable puzzles and one non-playable ending, while using all seven exhibit images without forcing Kubernetes City into a mechanic it does not need.