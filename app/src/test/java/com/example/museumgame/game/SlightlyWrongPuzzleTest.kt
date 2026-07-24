package com.example.museumgame.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SlightlyWrongPuzzleTest {

    @Test
    fun startsWithWrongOrderClue() {
        assertEquals(
            SlightlyWrongClue.WRONG_ORDER,
            SlightlyWrongPuzzle().state.currentClue
        )
    }

    @Test
    fun incorrectAnswerDoesNotAdvance() {
        val puzzle = SlightlyWrongPuzzle()

        val result = puzzle.answer(SlightlyWrongDetail.ORRERY)

        assertEquals(SlightlyWrongClue.WRONG_ORDER, result.state.currentClue)
        assertTrue(result.state.completedClues.isEmpty())
        assertEquals(SlightlyWrongFeedback.INCORRECT, result.feedback)
    }

    @Test
    fun clockAdvancesToIncompleteNames() {
        val result = SlightlyWrongPuzzle().answer(SlightlyWrongDetail.CLOCK)

        assertEquals(SlightlyWrongClue.INCOMPLETE_NAMES, result.state.currentClue)
        assertEquals(
            setOf(SlightlyWrongClue.WRONG_ORDER),
            result.state.completedClues
        )
        assertEquals(SlightlyWrongFeedback.CORRECT_NEXT_CLUE, result.feedback)
    }

    @Test
    fun correctThreeAnswerSequenceSolves() {
        val puzzle = SlightlyWrongPuzzle()
        puzzle.answer(SlightlyWrongDetail.CLOCK)
        puzzle.answer(SlightlyWrongDetail.BOOKSHELF)

        val result = puzzle.answer(SlightlyWrongDetail.GLOBE)

        assertTrue(result.state.solved)
        assertNull(result.state.currentClue)
        assertEquals(3, result.state.completedClues.size)
        assertEquals(SlightlyWrongFeedback.PUZZLE_SOLVED, result.feedback)
    }

    @Test
    fun correctDetailsGivenOutOfOrderDoNotAdvance() {
        val puzzle = SlightlyWrongPuzzle()

        val result = puzzle.answer(SlightlyWrongDetail.BOOKSHELF)

        assertFalse(result.state.solved)
        assertEquals(SlightlyWrongClue.WRONG_ORDER, result.state.currentClue)
        assertEquals(SlightlyWrongFeedback.INCORRECT, result.feedback)
    }

    @Test
    fun answerAfterSolvedDoesNotChangeState() {
        val puzzle = SlightlyWrongPuzzle()
        puzzle.answer(SlightlyWrongDetail.CLOCK)
        puzzle.answer(SlightlyWrongDetail.BOOKSHELF)
        val solvedState = puzzle.answer(SlightlyWrongDetail.GLOBE).state

        val result = puzzle.answer(SlightlyWrongDetail.ORRERY)

        assertEquals(solvedState, result.state)
        assertEquals(SlightlyWrongFeedback.ALREADY_SOLVED, result.feedback)
    }

    @Test
    fun restartClearsProgress() {
        val puzzle = SlightlyWrongPuzzle()
        puzzle.answer(SlightlyWrongDetail.CLOCK)

        puzzle.restart()

        assertEquals(SlightlyWrongState(), puzzle.state)
    }
}
