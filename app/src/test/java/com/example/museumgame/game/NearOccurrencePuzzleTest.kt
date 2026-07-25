package com.example.museumgame.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NearOccurrencePuzzleTest {

    @Test
    fun preservingBeforeThresholdIsTooSoonAndDoesNotAdvance() {
        val puzzle = NearOccurrencePuzzle()

        val result = puzzle.preserve()

        assertEquals(NearOccurrenceState(), puzzle.state)
        assertEquals(NearOccurrenceFeedback.TOO_SOON, result.feedback)
    }

    @Test
    fun twoAdvancesReachThresholdAndPreservingSolves() {
        val puzzle = NearOccurrencePuzzle()

        val shifting = puzzle.advance()
        val threshold = puzzle.advance()
        val solved = puzzle.preserve()

        assertEquals(NearOccurrenceFeedback.SHIFTING, shifting.feedback)
        assertEquals(NearOccurrenceFeedback.AT_THRESHOLD, threshold.feedback)
        assertEquals(NearOccurrenceStage.AT_THRESHOLD, puzzle.state.stage)
        assertTrue(puzzle.state.solved)
        assertEquals(NearOccurrenceFeedback.PUZZLE_SOLVED, solved.feedback)
    }

    @Test
    fun advancingPastThresholdSpillsAndResetsTheMoment() {
        val puzzle = NearOccurrencePuzzle()
        puzzle.advance()
        puzzle.advance()

        val result = puzzle.advance()

        assertEquals(NearOccurrenceState(), puzzle.state)
        assertEquals(NearOccurrenceFeedback.SPILL_RESET, result.feedback)
    }

    @Test
    fun actionAfterSolvedReturnsExplicitFeedbackWithoutChangingState() {
        val puzzle = NearOccurrencePuzzle()
        puzzle.advance()
        puzzle.advance()
        puzzle.preserve()
        val solvedState = puzzle.state

        val result = puzzle.advance()

        assertEquals(solvedState, puzzle.state)
        assertEquals(NearOccurrenceFeedback.ALREADY_SOLVED, result.feedback)
    }

    @Test
    fun restartReturnsToSettledAndClearsSolvedState() {
        val puzzle = NearOccurrencePuzzle()
        puzzle.advance()
        puzzle.advance()
        puzzle.preserve()

        puzzle.restart()

        assertEquals(NearOccurrenceStage.SETTLED, puzzle.state.stage)
        assertFalse(puzzle.state.solved)
        assertEquals(NearOccurrenceState(), puzzle.state)
    }
}
