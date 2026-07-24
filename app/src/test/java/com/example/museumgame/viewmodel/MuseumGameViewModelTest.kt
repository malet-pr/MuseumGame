package com.example.museumgame.viewmodel

import com.example.museumgame.game.PenInspectionFeedback
import com.example.museumgame.game.PenLocation
import com.example.museumgame.game.ReappearingPenState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MuseumGameViewModelTest {

    @Test
    fun initialDestinationIsHall() {
        val viewModel = MuseumGameViewModel()

        assertEquals(MuseumDestination.Hall, viewModel.uiState.destination)
    }

    @Test
    fun openingPenEntersItsExhibit() {
        val viewModel = MuseumGameViewModel()
        val pen = viewModel.uiState.exhibits.single()

        viewModel.openExhibit(pen)

        assertEquals(
            MuseumDestination.ExhibitDetail(pen),
            viewModel.uiState.destination
        )
    }

    @Test
    fun returningToHallPreservesAttemptsAndSolvedState() {
        val viewModel = MuseumGameViewModel()
        val pen = viewModel.uiState.exhibits.single()
        viewModel.openExhibit(pen)
        viewModel.inspect(pen)

        viewModel.returnToHall()

        assertEquals(MuseumDestination.Hall, viewModel.uiState.destination)
        assertEquals(1, viewModel.uiState.attempts)
        assertTrue(viewModel.uiState.solved)
    }

    @Test
    fun inspectingDelegatesToMuseumGame() {
        val viewModel = MuseumGameViewModel()
        val pen = viewModel.uiState.exhibits.single()

        viewModel.inspect(pen)

        assertEquals(1, viewModel.uiState.attempts)
        assertTrue(viewModel.uiState.solved)
        assertEquals(pen.description, viewModel.uiState.message)
    }

    @Test
    fun navigationStateChangesDoNotCountAsInspections() {
        val viewModel = MuseumGameViewModel()
        val pen = viewModel.uiState.exhibits.single()

        viewModel.openExhibit(pen)
        viewModel.returnToHall()
        viewModel.openExhibit(pen)

        assertEquals(0, viewModel.uiState.attempts)
        assertFalse(viewModel.uiState.solved)
    }

    @Test
    fun penInspectionUpdatesPuzzleStateAndAttempts() {
        val viewModel = MuseumGameViewModel()

        viewModel.inspectReappearingPen(PenLocation.PAPERS)

        assertEquals(1, viewModel.uiState.attempts)
        assertEquals(
            PenLocation.PAPERS,
            viewModel.uiState.reappearingPenState.targetLocation
        )
        assertEquals(
            PenInspectionFeedback.FIRST_LOCATION_EMPTY,
            viewModel.uiState.penFeedback
        )
        assertFalse(viewModel.uiState.solved)
    }

    @Test
    fun returningToHallPreservesPenPuzzleProgress() {
        val viewModel = MuseumGameViewModel()
        val pen = viewModel.uiState.exhibits.single()
        viewModel.openExhibit(pen)
        viewModel.inspectReappearingPen(PenLocation.PAPERS)
        viewModel.inspectReappearingPen(PenLocation.EMPTY_DESK)

        viewModel.returnToHall()
        viewModel.openExhibit(pen)

        assertEquals(2, viewModel.uiState.attempts)
        assertEquals(
            PenLocation.PAPERS,
            viewModel.uiState.reappearingPenState.penLocation
        )
        assertFalse(viewModel.uiState.solved)
    }

    @Test
    fun penSequenceSolvesThroughMuseumGame() {
        val viewModel = MuseumGameViewModel()
        viewModel.inspectReappearingPen(PenLocation.PAPERS)
        viewModel.inspectReappearingPen(PenLocation.EMPTY_DESK)

        viewModel.inspectReappearingPen(PenLocation.PAPERS)

        assertEquals(3, viewModel.uiState.attempts)
        assertTrue(viewModel.uiState.solved)
        assertEquals(PenInspectionFeedback.PEN_FOUND, viewModel.uiState.penFeedback)
    }

    @Test
    fun restartClearsAllPenPuzzleProgress() {
        val viewModel = MuseumGameViewModel()
        viewModel.inspectReappearingPen(PenLocation.PAPERS)
        viewModel.inspectReappearingPen(PenLocation.EMPTY_DESK)

        viewModel.restart()

        assertEquals(0, viewModel.uiState.attempts)
        assertFalse(viewModel.uiState.solved)
        assertEquals(ReappearingPenState(), viewModel.uiState.reappearingPenState)
        assertEquals(null, viewModel.uiState.penFeedback)
    }
}
