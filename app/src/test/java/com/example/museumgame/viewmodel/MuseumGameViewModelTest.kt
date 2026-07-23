package com.example.museumgame.viewmodel

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
}
