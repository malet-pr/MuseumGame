package com.example.museumgame.viewmodel

import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.PenInspectionFeedback
import com.example.museumgame.game.PenLocation
import com.example.museumgame.game.ReappearingPenState
import com.example.museumgame.game.SlightlyWrongDetail
import com.example.museumgame.game.SlightlyWrongState
import com.example.museumgame.model.ExhibitIds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MuseumGameViewModelTest {

    @Test
    fun startsAtEntranceWithPenCurrentAndSlightlyWrongLocked() {
        val viewModel = MuseumGameViewModel()

        assertEquals(MuseumDestination.Entrance, viewModel.uiState.destination)
        assertTrue(status(viewModel, ExhibitIds.REAPPEARING_PEN).current)
        assertTrue(status(viewModel, ExhibitIds.REAPPEARING_PEN).unlocked)
        assertFalse(status(viewModel, ExhibitIds.SLIGHTLY_WRONG).unlocked)
    }

    @Test
    fun resumeVisitOpensFirstUnfinishedExhibit() {
        val viewModel = MuseumGameViewModel()

        viewModel.resumeVisit()

        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.REAPPEARING_PEN),
            viewModel.uiState.destination
        )
    }

    @Test
    fun lockedExhibitCannotBeOpened() {
        val viewModel = MuseumGameViewModel()

        viewModel.openExhibit(ExhibitIds.SLIGHTLY_WRONG)

        assertEquals(MuseumDestination.Entrance, viewModel.uiState.destination)
    }

    @Test
    fun continueDoesNothingUntilCurrentExhibitIsSolved() {
        val viewModel = MuseumGameViewModel()
        viewModel.resumeVisit()

        viewModel.continueVisit()

        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.REAPPEARING_PEN),
            viewModel.uiState.destination
        )
    }

    @Test
    fun solvingPenUnlocksAndContinueOpensSlightlyWrong() {
        val viewModel = MuseumGameViewModel()
        viewModel.resumeVisit()
        solvePen(viewModel)

        viewModel.continueVisit()

        assertTrue(status(viewModel, ExhibitIds.REAPPEARING_PEN).completed)
        assertTrue(status(viewModel, ExhibitIds.SLIGHTLY_WRONG).unlocked)
        assertTrue(status(viewModel, ExhibitIds.SLIGHTLY_WRONG).current)
        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.SLIGHTLY_WRONG),
            viewModel.uiState.destination
        )
    }

    @Test
    fun returningToEntrancePreservesProgressAndResumeUsesFirstUnfinished() {
        val viewModel = MuseumGameViewModel()
        viewModel.resumeVisit()
        solvePen(viewModel)

        viewModel.returnToEntrance()
        viewModel.resumeVisit()

        assertEquals(3, viewModel.uiState.reappearingPen.progress.attempts)
        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.SLIGHTLY_WRONG),
            viewModel.uiState.destination
        )
    }

    @Test
    fun completedExhibitCanBeRevisited() {
        val viewModel = MuseumGameViewModel()
        solvePen(viewModel)

        viewModel.openExhibit(ExhibitIds.REAPPEARING_PEN)

        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.REAPPEARING_PEN),
            viewModel.uiState.destination
        )
    }

    @Test
    fun completingFinalExhibitReturnsToCompletedEntrance() {
        val viewModel = MuseumGameViewModel()
        solvePen(viewModel)
        viewModel.openExhibit(ExhibitIds.SLIGHTLY_WRONG)
        viewModel.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)
        viewModel.answerSlightlyWrong(SlightlyWrongDetail.BOOKSHELF)
        viewModel.answerSlightlyWrong(SlightlyWrongDetail.GLOBE)

        viewModel.continueVisit()

        assertEquals(MuseumDestination.Entrance, viewModel.uiState.destination)
        assertTrue(viewModel.uiState.visitStatuses.all { it.completed })
        assertFalse(viewModel.uiState.visitStatuses.any { it.current })
    }

    @Test
    fun restartMuseumClearsAllProgressFeedbackAndReturnsToEntrance() {
        val viewModel = MuseumGameViewModel()
        solvePen(viewModel)
        viewModel.openExhibit(ExhibitIds.SLIGHTLY_WRONG)
        viewModel.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)

        viewModel.restartMuseum()

        assertEquals(MuseumDestination.Entrance, viewModel.uiState.destination)
        assertEquals(ExhibitProgress(), viewModel.uiState.reappearingPen.progress)
        assertEquals(ExhibitProgress(), viewModel.uiState.slightlyWrong.progress)
        assertEquals(ReappearingPenState(), viewModel.uiState.reappearingPen.puzzleState)
        assertEquals(SlightlyWrongState(), viewModel.uiState.slightlyWrong.puzzleState)
        assertEquals(null, viewModel.uiState.reappearingPen.feedback)
        assertEquals(null, viewModel.uiState.slightlyWrong.feedback)
        assertTrue(status(viewModel, ExhibitIds.REAPPEARING_PEN).current)
        assertFalse(status(viewModel, ExhibitIds.SLIGHTLY_WRONG).unlocked)
    }

    @Test
    fun actionsForANonCurrentDestinationAreIgnored() {
        val viewModel = MuseumGameViewModel()
        viewModel.resumeVisit()

        viewModel.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)

        assertEquals(ExhibitProgress(), viewModel.uiState.slightlyWrong.progress)
        assertEquals(null, viewModel.uiState.slightlyWrong.feedback)

        solvePen(viewModel)
        viewModel.continueVisit()
        val solvedPenState = viewModel.uiState.reappearingPen

        viewModel.inspectReappearingPen(PenLocation.FILING_CABINET)

        assertEquals(solvedPenState, viewModel.uiState.reappearingPen)
    }

    @Test
    fun restartingPenAfterSlightlyWrongProgressResetsBothAndStaysInPen() {
        val viewModel = MuseumGameViewModel()
        solvePen(viewModel)
        viewModel.continueVisit()
        viewModel.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)
        viewModel.returnToEntrance()
        viewModel.openExhibit(ExhibitIds.REAPPEARING_PEN)

        viewModel.restartCurrentExhibit()

        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.REAPPEARING_PEN),
            viewModel.uiState.destination
        )
        assertEquals(ReappearingPenUiState(), viewModel.uiState.reappearingPen)
        assertEquals(SlightlyWrongUiState(), viewModel.uiState.slightlyWrong)
        assertFalse(status(viewModel, ExhibitIds.SLIGHTLY_WRONG).unlocked)
    }

    @Test
    fun restartingSlightlyWrongPreservesCompletedPenAndStaysInSlightlyWrong() {
        val viewModel = MuseumGameViewModel()
        solvePen(viewModel)
        viewModel.continueVisit()
        viewModel.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)

        viewModel.restartCurrentExhibit()

        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.SLIGHTLY_WRONG),
            viewModel.uiState.destination
        )
        assertEquals(3, viewModel.uiState.reappearingPen.progress.attempts)
        assertTrue(viewModel.uiState.reappearingPen.puzzleState.solved)
        assertEquals(
            PenInspectionFeedback.PEN_FOUND,
            viewModel.uiState.reappearingPen.feedback
        )
        assertEquals(SlightlyWrongUiState(), viewModel.uiState.slightlyWrong)
        assertTrue(status(viewModel, ExhibitIds.REAPPEARING_PEN).completed)
    }

    @Test
    fun solvedPuzzleReturnsExplicitAlreadySolvedFeedbackWithoutAnotherAttempt() {
        val viewModel = MuseumGameViewModel()
        solvePen(viewModel)

        viewModel.inspectReappearingPen(PenLocation.FILING_CABINET)

        assertEquals(3, viewModel.uiState.reappearingPen.progress.attempts)
        assertEquals(
            PenInspectionFeedback.ALREADY_SOLVED,
            viewModel.uiState.reappearingPen.feedback
        )
    }

    @Test
    fun everyReachableSolvedPenStateRemainsConsistent() {
        PenLocation.entries.forEach { target ->
            val viewModel = MuseumGameViewModel()
            val differentLocation = PenLocation.entries.first { it != target }
            viewModel.resumeVisit()

            viewModel.inspectReappearingPen(target)
            viewModel.inspectReappearingPen(differentLocation)
            viewModel.inspectReappearingPen(target)

            assertTrue(viewModel.uiState.reappearingPen.puzzleState.solved)
            assertTrue(status(viewModel, ExhibitIds.REAPPEARING_PEN).completed)
            assertEquals(
                PenInspectionFeedback.PEN_FOUND,
                viewModel.uiState.reappearingPen.feedback
            )
        }
    }

    private fun solvePen(viewModel: MuseumGameViewModel) {
        viewModel.openExhibit(ExhibitIds.REAPPEARING_PEN)
        viewModel.inspectReappearingPen(PenLocation.PAPERS)
        viewModel.inspectReappearingPen(PenLocation.EMPTY_DESK)
        viewModel.inspectReappearingPen(PenLocation.PAPERS)
    }

    private fun status(viewModel: MuseumGameViewModel, exhibitId: String) =
        viewModel.uiState.visitStatuses.first { it.exhibitId == exhibitId }
}
