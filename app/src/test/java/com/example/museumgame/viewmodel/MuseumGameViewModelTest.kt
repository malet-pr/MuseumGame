package com.example.museumgame.viewmodel

import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.PenInspectionFeedback
import com.example.museumgame.game.PenLocation
import com.example.museumgame.game.ProgressCategory
import com.example.museumgame.game.ReappearingPenState
import com.example.museumgame.game.SimulatedProgressFeedback
import com.example.museumgame.game.SimulatedProgressSignal
import com.example.museumgame.game.SimulatedProgressState
import com.example.museumgame.game.SlightlyWrongDetail
import com.example.museumgame.game.SlightlyWrongState
import com.example.museumgame.game.WorkApparentFeedback
import com.example.museumgame.game.WorkApparentInterruption
import com.example.museumgame.game.WorkApparentStage
import com.example.museumgame.game.WorkApparentState
import com.example.museumgame.model.ExhibitIds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MuseumGameViewModelTest {

    @Test
    fun startsAtEntranceWithOnlyPenUnlocked() {
        val viewModel = MuseumGameViewModel()

        assertEquals(MuseumDestination.Entrance, viewModel.uiState.destination)
        assertTrue(status(viewModel, ExhibitIds.REAPPEARING_PEN).current)
        assertTrue(status(viewModel, ExhibitIds.REAPPEARING_PEN).unlocked)
        assertFalse(status(viewModel, ExhibitIds.SLIGHTLY_WRONG).unlocked)
        assertFalse(status(viewModel, ExhibitIds.WORK_APPARENT).unlocked)
        assertFalse(status(viewModel, ExhibitIds.SIMULATED_PROGRESS).unlocked)
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
    fun workApparentCannotBeOpenedBeforeSlightlyWrongIsSolved() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.REAPPEARING_PEN)

        viewModel.openExhibit(ExhibitIds.WORK_APPARENT)

        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.REAPPEARING_PEN),
            viewModel.uiState.destination
        )
    }

    @Test
    fun simulatedProgressCannotBeOpenedBeforeWorkApparentIsSolved() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.SLIGHTLY_WRONG)

        viewModel.openExhibit(ExhibitIds.SIMULATED_PROGRESS)

        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.SLIGHTLY_WRONG),
            viewModel.uiState.destination
        )
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
        viewModel.solveThrough(ExhibitIds.REAPPEARING_PEN)

        viewModel.continueVisit()

        assertTrue(status(viewModel, ExhibitIds.REAPPEARING_PEN).completed)
        assertTrue(status(viewModel, ExhibitIds.SLIGHTLY_WRONG).unlocked)
        assertTrue(status(viewModel, ExhibitIds.SLIGHTLY_WRONG).current)
        assertFalse(status(viewModel, ExhibitIds.WORK_APPARENT).unlocked)
        assertFalse(status(viewModel, ExhibitIds.SIMULATED_PROGRESS).unlocked)
        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.SLIGHTLY_WRONG),
            viewModel.uiState.destination
        )
    }

    @Test
    fun returningToEntrancePreservesProgressAndResumeUsesFirstUnfinished() {
        val viewModel = MuseumGameViewModel()
        viewModel.resumeVisit()
        viewModel.solveThrough(ExhibitIds.REAPPEARING_PEN)

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
        viewModel.solveThrough(ExhibitIds.REAPPEARING_PEN)

        viewModel.openExhibit(ExhibitIds.REAPPEARING_PEN)

        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.REAPPEARING_PEN),
            viewModel.uiState.destination
        )
    }

    @Test
    fun completingSimulatedProgressReturnsToCompletedEntrance() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.SIMULATED_PROGRESS)

        viewModel.continueVisit()

        assertEquals(MuseumDestination.Entrance, viewModel.uiState.destination)
        assertTrue(viewModel.uiState.visitStatuses.all { it.completed })
        assertFalse(viewModel.uiState.visitStatuses.any { it.current })
    }

    @Test
    fun solvingSlightlyWrongUnlocksAndContinueOpensWorkApparent() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.SLIGHTLY_WRONG)

        viewModel.continueVisit()

        assertTrue(status(viewModel, ExhibitIds.SLIGHTLY_WRONG).completed)
        assertTrue(status(viewModel, ExhibitIds.WORK_APPARENT).unlocked)
        assertTrue(status(viewModel, ExhibitIds.WORK_APPARENT).current)
        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.WORK_APPARENT),
            viewModel.uiState.destination
        )
    }

    @Test
    fun tracingAndInterruptingWorkApparentUpdatesUiState() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.SLIGHTLY_WRONG)
        viewModel.continueVisit()

        WorkApparentStage.entries.forEach(viewModel::traceWorkApparent)
        viewModel.interruptWorkApparent(WorkApparentInterruption.COMPLETE_ONE_TASK)

        assertEquals(6, viewModel.uiState.workApparent.progress.attempts)
        assertTrue(viewModel.uiState.workApparent.puzzleState.solved)
        assertEquals(
            WorkApparentFeedback.PUZZLE_SOLVED,
            viewModel.uiState.workApparent.feedback
        )
        assertTrue(status(viewModel, ExhibitIds.WORK_APPARENT).completed)
    }

    @Test
    fun solvingWorkApparentUnlocksAndContinueOpensSimulatedProgress() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.WORK_APPARENT)

        viewModel.continueVisit()

        assertTrue(status(viewModel, ExhibitIds.WORK_APPARENT).completed)
        assertTrue(status(viewModel, ExhibitIds.SIMULATED_PROGRESS).unlocked)
        assertTrue(status(viewModel, ExhibitIds.SIMULATED_PROGRESS).current)
        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.SIMULATED_PROGRESS),
            viewModel.uiState.destination
        )
    }

    @Test
    fun classifyingSimulatedProgressUpdatesUiState() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.WORK_APPARENT)
        viewModel.continueVisit()

        viewModel.classifySimulatedProgress(ProgressCategory.ACTIVITY)

        assertEquals(1, viewModel.uiState.simulatedProgress.progress.attempts)
        assertEquals(
            listOf(SimulatedProgressSignal.ALIGNMENT_MEETINGS),
            viewModel.uiState.simulatedProgress.puzzleState.classifiedSignals
        )
        assertEquals(
            SimulatedProgressFeedback.CORRECT_ACTIVITY,
            viewModel.uiState.simulatedProgress.feedback
        )
    }

    @Test
    fun restartMuseumClearsAllProgressFeedbackAndReturnsToEntrance() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.WORK_APPARENT)
        viewModel.continueVisit()
        viewModel.classifySimulatedProgress(ProgressCategory.ACTIVITY)

        viewModel.restartMuseum()

        assertEquals(MuseumDestination.Entrance, viewModel.uiState.destination)
        assertEquals(ExhibitProgress(), viewModel.uiState.reappearingPen.progress)
        assertEquals(ExhibitProgress(), viewModel.uiState.slightlyWrong.progress)
        assertEquals(ExhibitProgress(), viewModel.uiState.workApparent.progress)
        assertEquals(ExhibitProgress(), viewModel.uiState.simulatedProgress.progress)
        assertEquals(ReappearingPenState(), viewModel.uiState.reappearingPen.puzzleState)
        assertEquals(SlightlyWrongState(), viewModel.uiState.slightlyWrong.puzzleState)
        assertEquals(WorkApparentState(), viewModel.uiState.workApparent.puzzleState)
        assertEquals(
            SimulatedProgressState(),
            viewModel.uiState.simulatedProgress.puzzleState
        )
        assertEquals(null, viewModel.uiState.reappearingPen.feedback)
        assertEquals(null, viewModel.uiState.slightlyWrong.feedback)
        assertEquals(null, viewModel.uiState.workApparent.feedback)
        assertEquals(null, viewModel.uiState.simulatedProgress.feedback)
        assertTrue(status(viewModel, ExhibitIds.REAPPEARING_PEN).current)
        assertFalse(status(viewModel, ExhibitIds.SLIGHTLY_WRONG).unlocked)
        assertFalse(status(viewModel, ExhibitIds.WORK_APPARENT).unlocked)
        assertFalse(status(viewModel, ExhibitIds.SIMULATED_PROGRESS).unlocked)
    }

    @Test
    fun actionsForANonCurrentDestinationAreIgnored() {
        val viewModel = MuseumGameViewModel()
        viewModel.resumeVisit()

        viewModel.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)
        viewModel.traceWorkApparent(WorkApparentStage.TASKS_RECEIVED)
        viewModel.classifySimulatedProgress(ProgressCategory.ACTIVITY)

        assertEquals(ExhibitProgress(), viewModel.uiState.slightlyWrong.progress)
        assertEquals(null, viewModel.uiState.slightlyWrong.feedback)
        assertEquals(ExhibitProgress(), viewModel.uiState.workApparent.progress)
        assertEquals(null, viewModel.uiState.workApparent.feedback)
        assertEquals(ExhibitProgress(), viewModel.uiState.simulatedProgress.progress)
        assertEquals(null, viewModel.uiState.simulatedProgress.feedback)

        viewModel.solveThrough(ExhibitIds.REAPPEARING_PEN)
        viewModel.continueVisit()
        val solvedPenState = viewModel.uiState.reappearingPen

        viewModel.inspectReappearingPen(PenLocation.FILING_CABINET)

        assertEquals(solvedPenState, viewModel.uiState.reappearingPen)
    }

    @Test
    fun restartingPenAfterSlightlyWrongProgressResetsBothAndStaysInPen() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.REAPPEARING_PEN)
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
        assertEquals(WorkApparentUiState(), viewModel.uiState.workApparent)
        assertEquals(SimulatedProgressUiState(), viewModel.uiState.simulatedProgress)
        assertFalse(status(viewModel, ExhibitIds.SLIGHTLY_WRONG).unlocked)
    }

    @Test
    fun restartingSlightlyWrongPreservesCompletedPenAndStaysInSlightlyWrong() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.REAPPEARING_PEN)
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
        assertEquals(WorkApparentUiState(), viewModel.uiState.workApparent)
        assertEquals(SimulatedProgressUiState(), viewModel.uiState.simulatedProgress)
        assertTrue(status(viewModel, ExhibitIds.REAPPEARING_PEN).completed)
    }

    @Test
    fun restartingSlightlyWrongAfterWorkProgressAlsoResetsWork() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.SLIGHTLY_WRONG)
        viewModel.continueVisit()
        viewModel.traceWorkApparent(WorkApparentStage.TASKS_RECEIVED)
        viewModel.returnToEntrance()
        viewModel.openExhibit(ExhibitIds.SLIGHTLY_WRONG)

        viewModel.restartCurrentExhibit()

        assertEquals(SlightlyWrongUiState(), viewModel.uiState.slightlyWrong)
        assertEquals(WorkApparentUiState(), viewModel.uiState.workApparent)
        assertEquals(SimulatedProgressUiState(), viewModel.uiState.simulatedProgress)
        assertFalse(status(viewModel, ExhibitIds.WORK_APPARENT).unlocked)
    }

    @Test
    fun restartingWorkApparentAfterSimulatedProgressResetsBothAndPreservesEarlierExhibits() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.WORK_APPARENT)
        viewModel.continueVisit()
        viewModel.classifySimulatedProgress(ProgressCategory.ACTIVITY)
        viewModel.returnToEntrance()
        viewModel.openExhibit(ExhibitIds.WORK_APPARENT)

        viewModel.restartCurrentExhibit()

        assertTrue(viewModel.uiState.reappearingPen.puzzleState.solved)
        assertTrue(viewModel.uiState.slightlyWrong.puzzleState.solved)
        assertEquals(WorkApparentUiState(), viewModel.uiState.workApparent)
        assertEquals(SimulatedProgressUiState(), viewModel.uiState.simulatedProgress)
        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.WORK_APPARENT),
            viewModel.uiState.destination
        )
    }

    @Test
    fun restartingSimulatedProgressPreservesEarlierCompletedExhibits() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.WORK_APPARENT)
        viewModel.continueVisit()
        viewModel.classifySimulatedProgress(ProgressCategory.ACTIVITY)

        viewModel.restartCurrentExhibit()

        assertTrue(viewModel.uiState.reappearingPen.puzzleState.solved)
        assertTrue(viewModel.uiState.slightlyWrong.puzzleState.solved)
        assertTrue(viewModel.uiState.workApparent.puzzleState.solved)
        assertEquals(SimulatedProgressUiState(), viewModel.uiState.simulatedProgress)
        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.SIMULATED_PROGRESS),
            viewModel.uiState.destination
        )
    }

    @Test
    fun solvedPuzzleReturnsExplicitAlreadySolvedFeedbackWithoutAnotherAttempt() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.REAPPEARING_PEN)

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

    private fun status(viewModel: MuseumGameViewModel, exhibitId: String) =
        viewModel.uiState.visitStatuses.first { it.exhibitId == exhibitId }
}
