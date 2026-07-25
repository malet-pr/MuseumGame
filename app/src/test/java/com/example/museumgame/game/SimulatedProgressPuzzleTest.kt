package com.example.museumgame.game

import com.example.museumgame.testsupport.SIMULATED_PROGRESS_CLASSIFICATIONS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulatedProgressPuzzleTest {

    @Test
    fun everyNamedSignalRequiresItsExplicitExpectedCategory() {
        val puzzle = SimulatedProgressPuzzle()

        assertClassification(
            puzzle,
            SimulatedProgressSignal.ALIGNMENT_MEETINGS,
            ProgressCategory.ACTIVITY
        )
        assertClassification(
            puzzle,
            SimulatedProgressSignal.WORKFLOW_GUIDE,
            ProgressCategory.OUTPUT
        )
        assertClassification(
            puzzle,
            SimulatedProgressSignal.FASTER_SETUP,
            ProgressCategory.IMPACT
        )
        assertClassification(
            puzzle,
            SimulatedProgressSignal.CODE_REVIEW_TIME,
            ProgressCategory.ACTIVITY
        )
        assertClassification(
            puzzle,
            SimulatedProgressSignal.AUTOMATION_SCRIPTS,
            ProgressCategory.OUTPUT
        )
        assertClassification(
            puzzle,
            SimulatedProgressSignal.FEWER_SUPPORT_REQUESTS,
            ProgressCategory.IMPACT
        )

        assertTrue(puzzle.state.solved)
        assertEquals(
            listOf(
                SimulatedProgressSignal.ALIGNMENT_MEETINGS,
                SimulatedProgressSignal.WORKFLOW_GUIDE,
                SimulatedProgressSignal.FASTER_SETUP,
                SimulatedProgressSignal.CODE_REVIEW_TIME,
                SimulatedProgressSignal.AUTOMATION_SCRIPTS,
                SimulatedProgressSignal.FEWER_SUPPORT_REQUESTS
            ),
            puzzle.state.classifiedSignals
        )
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
        return SIMULATED_PROGRESS_CLASSIFICATIONS
            .map(puzzle::classify)
            .last()
    }

    private fun assertClassification(
        puzzle: SimulatedProgressPuzzle,
        expectedSignal: SimulatedProgressSignal,
        expectedCategory: ProgressCategory
    ) {
        assertEquals(expectedSignal, puzzle.state.currentSignal)
        puzzle.classify(expectedCategory)
        assertEquals(expectedSignal, puzzle.state.classifiedSignals.last())
    }
}
