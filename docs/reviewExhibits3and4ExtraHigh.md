Overall, both exhibits fit the prepared architecture well. I found no reachable correctness, progression, restart, lifecycle, or platform-coupling errors.
The current validation remains green: 74 unit tests, 14 instrumentation tests, and Android lint all pass.
Findings by severity
Severity	Finding	Impact
Critical	None	—
High	None	—
Medium	The full museum journey test is becoming brittle and oversized.	[MuseumFlowTest.kt (line 160)](C:\\Users\\nmale\\Documents\\games\\code\\MuseumGame\\app\\src\\androidTest\\java\\com\\example\\museumgame\\ui\\MuseumFlowTest.kt:160) must change whenever an exhibit is added and depends on exact text, ordering, scrolling, and menu dimensions. The emulator failure you encountered is evidence of this risk, although the current version passes.
Medium	Work Apparent’s intended sequence is not independently protected by tests.	The puzzle, UI, domain tests, and journey helpers all derive the order from WorkApparentStage.entries. Reordering the enum would silently change the puzzle while most tests continued passing. See [WorkApparentPuzzle.kt (line 33)](C:\\Users\\nmale\\Documents\\games\\code\\MuseumGame\\app\\src\\main\\java\\com\\example\\museumgame\\game\\WorkApparentPuzzle.kt:33) and [WorkApparentPuzzleTest.kt (line 14)](C:\\Users\\nmale\\Documents\\games\\code\\MuseumGame\\app\\src\\test\\java\\com\\example\\museumgame\\game\\WorkApparentPuzzleTest.kt:14).
Low	Later-room configuration-change coverage is indirect.	The activity recreation test only exercises the Pen. Work Apparent and Simulated Progress should survive recreation because the same retained ViewModel owns them, but there is no direct regression test for their newly added UI-state fields.
Low	Work Apparent replaces the entire control group at its phase transition.	When the last trace step is selected, five trace buttons disappear and three intervention buttons appear in [WorkApparentContent.kt (line 65)](C:\\Users\\nmale\\Documents\\games\\code\\MuseumGame\\app\\src\\main\\java\\com\\example\\museumgame\\ui\\WorkApparentContent.kt:65). The live-region announcement helps, but TalkBack focus behavior is not explicitly managed or tested.
Low	Entrance previews have drifted behind the catalog.	previewVisitStatuses still contains only the first two exhibits, so previews do not represent the current four-room entrance selector. See [MuseumEntranceContent.kt (line 252)](C:\\Users\\nmale\\Documents\\games\\code\\MuseumGame\\app\\src\\main\\java\\com\\example\\museumgame\\ui\\MuseumEntranceContent.kt:252).

Architecture assessment
The preparation work is doing what it was intended to do:
ExhibitCatalog remains the authoritative order.
Attempts are catalog-keyed rather than maintained as unrelated counters.
Restarting an exhibit resets it and every downstream room.
Work Apparent and Simulated Progress have independent, platform-neutral state machines.
MuseumGameViewModel continues to use one domain-refresh path for every puzzle slice.
Drawable and string mappings remain centralized in the UI layer.
Both rooms reuse the responsive layout and shared navigation actions.
Resource and catalog coverage tests fail when an exhibit is incompletely wired.
No Android or Compose types have entered the domain or model packages.
The mechanics remain meaningfully distinct: Work Apparent is sequence tracing plus intervention; Simulated Progress is evidence classification.
I would not add a generic Puzzle interface, action registry, or map-based MuseumUiState yet. The explicit typed state remains clearer than the abstraction those mechanisms would require.
Improvements ranked by expected gain
High gain — stabilize the test structure. Keep one complete journey smoke test, but move lock, revisit, completion, and restart assertions into smaller focused tests. Share only test-side solution data where useful; do not introduce a production puzzle framework.

High gain, low effort — protect narrative order explicitly. Add a test asserting the exact named Work Apparent sequence rather than comparing against entries. Do the same for the six Simulated Progress signals and their expected categories.

Medium gain — add one later-room recreation test. Advance to Work Apparent or Simulated Progress, make one choice, recreate the activity, and verify destination, attempts, feedback, and puzzle state.

Medium gain — verify phase-transition accessibility. Test the transition to Work Apparent’s intervention phase and manually check TalkBack focus. Add explicit focus movement only if the real device behavior is confusing.

Low gain — derive preview statuses from the catalog. This would keep portrait and landscape previews current as the remaining exhibits are added.

Low gain — minor naming/resource cleanup. ProgressCategory could become SimulatedProgressCategory, and the plural-candidate lint warnings could eventually use plurals. Neither affects current behavior.

No files were modified during this review.