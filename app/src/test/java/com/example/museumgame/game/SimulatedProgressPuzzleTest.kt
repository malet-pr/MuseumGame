package com.example.museumgame.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulatedProgressPuzzleTest {

    @Test
    fun correctCategoriesAdvanceThroughActivityOutputAndImpact() {
        val puzzle = SimulatedProgressPuzzle()

        val activity = puzzle.classify(ProgressCategory.ACTIVITY)
        val output = puzzle.classify(ProgressCategory.OUTPUT)
        val impact = puzzle.classify(ProgressCategory.IMPACT)

        assertEquals(SimulatedProgressFeedback.CORRECT_ACTIVITY, activity.feedback)
        assertEquals(SimulatedProgressFeedback.CORRECT_OUTPUT, output.feedback)
        assertEquals(SimulatedProgressFeedback.CORRECT_IMPACT, impact.feedback)
        assertEquals(3, puzzle.state.classifiedSignals.size)
        assertFalse(puzzle.state.solved)
    }

    @Test
    fun incorrectCategoryDoesNotAdvanceTheCurrentSignal() {
        val puzzle = SimulatedProgressPuzzle()

        val result = puzzle.classify(ProgressCategory.IMPACT)

        assertEquals(
            SimulatedProgressSignal.ALIGNMENT_MEETINGS,
            puzzle.state.currentSignal
        )
        assertTrue(puzzle.state.classifiedSignals.isEmpty())
        assertEquals(
            SimulatedProgressFeedback.INCORRECT_ACTIVITY,
            result.feedback
        )
    }

    @Test
    fun classifyingAllSixSignalsSolvesThePuzzle() {
        val puzzle = SimulatedProgressPuzzle()

        val result = solve(puzzle)

        assertTrue(puzzle.state.solved)
        assertEquals(null, puzzle.state.currentSignal)
        assertEquals(
            SimulatedProgressFeedback.PUZZLE_SOLVED,
            result.feedback
        )
    }

    @Test
    fun classificationAfterSolvedReturnsExplicitFeedback() {
        val puzzle = SimulatedProgressPuzzle()
        solve(puzzle)

        val result = puzzle.classify(ProgressCategory.ACTIVITY)

        assertEquals(
            SimulatedProgressFeedback.ALREADY_SOLVED,
            result.feedback
        )
        assertEquals(6, puzzle.state.classifiedSignals.size)
    }

    @Test
    fun restartClearsEveryClassification() {
        val puzzle = SimulatedProgressPuzzle()
        puzzle.classify(ProgressCategory.ACTIVITY)
        puzzle.classify(ProgressCategory.OUTPUT)

        puzzle.restart()

        assertEquals(SimulatedProgressState(), puzzle.state)
    }

    private fun solve(
        puzzle: SimulatedProgressPuzzle
    ): SimulatedProgressResult {
        val categories = listOf(
            ProgressCategory.ACTIVITY,
            ProgressCategory.OUTPUT,
            ProgressCategory.IMPACT,
            ProgressCategory.ACTIVITY,
            ProgressCategory.OUTPUT,
            ProgressCategory.IMPACT
        )
        return categories.map(puzzle::classify).last()
    }
}
