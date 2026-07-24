package com.example.museumgame.viewmodel

import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.PenInspectionFeedback
import com.example.museumgame.game.PenLocation
import com.example.museumgame.game.ReappearingPenState
import com.example.museumgame.game.SlightlyWrongClue
import com.example.museumgame.game.SlightlyWrongDetail
import com.example.museumgame.game.SlightlyWrongFeedback
import com.example.museumgame.game.SlightlyWrongState
import com.example.museumgame.model.ExhibitIds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MuseumGameViewModelTest {

    @Test
    fun initialDestinationIsHallWithBothExhibits() {
        val viewModel = MuseumGameViewModel()

        assertEquals(MuseumDestination.Hall, viewModel.uiState.destination)
        assertEquals(
            setOf(ExhibitIds.REAPPEARING_PEN, ExhibitIds.SLIGHTLY_WRONG),
            viewModel.uiState.exhibits.map { it.id }.toSet()
        )
    }

    @Test
    fun eachExhibitCanBeOpenedFromHall() {
        val viewModel = MuseumGameViewModel()

        viewModel.uiState.exhibits.forEach { exhibit ->
            viewModel.openExhibit(exhibit)
            assertEquals(
                MuseumDestination.ExhibitDetail(exhibit),
                viewModel.uiState.destination
            )
            viewModel.returnToHall()
        }
    }

    @Test
    fun navigationDoesNotCountAsAnAttempt() {
        val viewModel = MuseumGameViewModel()

        viewModel.uiState.exhibits.forEach { exhibit ->
            viewModel.openExhibit(exhibit)
            viewModel.returnToHall()
        }

        assertEquals(0, viewModel.uiState.reappearingPen.progress.attempts)
        assertEquals(0, viewModel.uiState.slightlyWrong.progress.attempts)
    }

    @Test
    fun returningToHallPreservesPenProgress() {
        val viewModel = MuseumGameViewModel()
        val pen = viewModel.uiState.exhibits.first { it.id == ExhibitIds.REAPPEARING_PEN }
        viewModel.openExhibit(pen)
        viewModel.inspectReappearingPen(PenLocation.PAPERS)
        viewModel.inspectReappearingPen(PenLocation.EMPTY_DESK)

        viewModel.returnToHall()
        viewModel.openExhibit(pen)

        assertEquals(2, viewModel.uiState.reappearingPen.progress.attempts)
        assertEquals(
            PenLocation.PAPERS,
            viewModel.uiState.reappearingPen.puzzleState.penLocation
        )
    }

    @Test
    fun everyReachableSolvedPenStateIsConsistent() {
        PenLocation.entries.forEach { target ->
            val viewModel = MuseumGameViewModel()
            val differentLocation = PenLocation.entries.first { it != target }

            viewModel.inspectReappearingPen(target)
            viewModel.inspectReappearingPen(differentLocation)
            viewModel.inspectReappearingPen(target)

            assertTrue(viewModel.uiState.reappearingPen.progress.solved)
            assertTrue(viewModel.uiState.reappearingPen.puzzleState.solved)
            assertEquals(
                PenInspectionFeedback.PEN_FOUND,
                viewModel.uiState.reappearingPen.feedback
            )
        }
    }

    @Test
    fun slightlyWrongAnswerUpdatesOnlyItsState() {
        val viewModel = MuseumGameViewModel()

        viewModel.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)

        assertEquals(1, viewModel.uiState.slightlyWrong.progress.attempts)
        assertEquals(
            SlightlyWrongClue.INCOMPLETE_NAMES,
            viewModel.uiState.slightlyWrong.puzzleState.currentClue
        )
        assertEquals(
            SlightlyWrongFeedback.CORRECT_NEXT_CLUE,
            viewModel.uiState.slightlyWrong.feedback
        )
        assertEquals(ExhibitProgress(), viewModel.uiState.reappearingPen.progress)
        assertEquals(ReappearingPenState(), viewModel.uiState.reappearingPen.puzzleState)
    }

    @Test
    fun returningToHallPreservesSlightlyWrongProgress() {
        val viewModel = MuseumGameViewModel()
        val exhibit = viewModel.uiState.exhibits.first { it.id == ExhibitIds.SLIGHTLY_WRONG }
        viewModel.openExhibit(exhibit)
        viewModel.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)

        viewModel.returnToHall()
        viewModel.openExhibit(exhibit)

        assertEquals(1, viewModel.uiState.slightlyWrong.progress.attempts)
        assertEquals(
            SlightlyWrongClue.INCOMPLETE_NAMES,
            viewModel.uiState.slightlyWrong.puzzleState.currentClue
        )
    }

    @Test
    fun restartingCurrentSlightlyWrongExhibitDoesNotResetPen() {
        val viewModel = MuseumGameViewModel()
        val slightlyWrong = viewModel.uiState.exhibits.first {
            it.id == ExhibitIds.SLIGHTLY_WRONG
        }
        viewModel.inspectReappearingPen(PenLocation.PAPERS)
        viewModel.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)
        viewModel.openExhibit(slightlyWrong)

        viewModel.restartCurrentExhibit()

        assertEquals(ExhibitProgress(), viewModel.uiState.slightlyWrong.progress)
        assertEquals(SlightlyWrongState(), viewModel.uiState.slightlyWrong.puzzleState)
        assertEquals(1, viewModel.uiState.reappearingPen.progress.attempts)
        assertFalse(viewModel.uiState.reappearingPen.puzzleState.solved)
    }
}
