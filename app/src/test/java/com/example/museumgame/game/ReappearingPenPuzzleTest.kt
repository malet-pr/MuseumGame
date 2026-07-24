package com.example.museumgame.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReappearingPenPuzzleTest {

    @Test
    fun firstInspectionSetsHiddenTargetWithoutRevealingPen() {
        val puzzle = ReappearingPenPuzzle()

        val result = puzzle.inspect(PenLocation.PAPERS)

        assertEquals(PenLocation.PAPERS, result.state.targetLocation)
        assertNull(result.state.penLocation)
        assertFalse(result.state.solved)
        assertEquals(PenInspectionFeedback.FIRST_LOCATION_EMPTY, result.feedback)
    }

    @Test
    fun repeatingFirstLocationDoesNotRevealPen() {
        val puzzle = ReappearingPenPuzzle()
        puzzle.inspect(PenLocation.PAPERS)

        val result = puzzle.inspect(PenLocation.PAPERS)

        assertNull(result.state.penLocation)
        assertFalse(result.state.solved)
        assertEquals(PenInspectionFeedback.SAME_LOCATION_STILL_EMPTY, result.feedback)
    }

    @Test
    fun differentLocationMakesPenAppearAtFirstLocation() {
        val puzzle = ReappearingPenPuzzle()
        puzzle.inspect(PenLocation.PAPERS)

        val result = puzzle.inspect(PenLocation.EMPTY_DESK)

        assertEquals(PenLocation.PAPERS, result.state.penLocation)
        assertFalse(result.state.solved)
        assertEquals(PenInspectionFeedback.PEN_REAPPEARED, result.feedback)
    }

    @Test
    fun reinspectingFirstLocationSolvesPuzzle() {
        val puzzle = ReappearingPenPuzzle()
        puzzle.inspect(PenLocation.PAPERS)
        puzzle.inspect(PenLocation.EMPTY_DESK)

        val result = puzzle.inspect(PenLocation.PAPERS)

        assertTrue(result.state.solved)
        assertEquals(PenInspectionFeedback.PEN_FOUND, result.feedback)
    }

    @Test
    fun otherLocationsDoNotMovePen() {
        val puzzle = ReappearingPenPuzzle()
        puzzle.inspect(PenLocation.PAPERS)
        puzzle.inspect(PenLocation.EMPTY_DESK)

        val result = puzzle.inspect(PenLocation.FILING_CABINET)

        assertEquals(PenLocation.PAPERS, result.state.penLocation)
        assertFalse(result.state.solved)
        assertEquals(PenInspectionFeedback.LOCATION_EMPTY, result.feedback)
    }

    @Test
    fun inspectionAfterSolvedDoesNotChangePuzzleState() {
        val puzzle = ReappearingPenPuzzle()
        puzzle.inspect(PenLocation.PAPERS)
        puzzle.inspect(PenLocation.EMPTY_DESK)
        val solvedState = puzzle.inspect(PenLocation.PAPERS).state

        val result = puzzle.inspect(PenLocation.FILING_CABINET)

        assertEquals(solvedState, result.state)
        assertEquals(PenInspectionFeedback.ALREADY_SOLVED, result.feedback)
    }

    @Test
    fun restartClearsAllPuzzleProgress() {
        val puzzle = ReappearingPenPuzzle()
        puzzle.inspect(PenLocation.PAPERS)
        puzzle.inspect(PenLocation.EMPTY_DESK)

        puzzle.restart()

        assertEquals(ReappearingPenState(), puzzle.state)
    }
}
