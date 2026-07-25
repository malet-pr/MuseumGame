package com.example.museumgame.viewmodel

import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.ChaosPiece
import com.example.museumgame.game.CreativeChaosFeedback
import com.example.museumgame.game.CreativeChaosState
import com.example.museumgame.game.CreativeChaosStep
import com.example.museumgame.game.NearOccurrenceFeedback
import com.example.museumgame.game.NearOccurrenceStage
import com.example.museumgame.game.NearOccurrenceState
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
import com.example.museumgame.testsupport.WORK_APPARENT_TRACE_SEQUENCE
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
        assertFalse(status(viewModel, ExhibitIds.NEAR_OCCURRENCE).unlocked)
        assertFalse(status(viewModel, ExhibitIds.CREATIVE_CHAOS).unlocked)
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
    fun nearOccurrenceCannotBeOpenedBeforeSimulatedProgressIsSolved() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.WORK_APPARENT)

        viewModel.openExhibit(ExhibitIds.NEAR_OCCURRENCE)

        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.WORK_APPARENT),
            viewModel.uiState.destination
        )
    }

    @Test
    fun creativeChaosCannotBeOpenedBeforeNearOccurrenceIsSolved() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.SIMULATED_PROGRESS)

        viewModel.openExhibit(ExhibitIds.CREATIVE_CHAOS)

        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.SIMULATED_PROGRESS),
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
        assertFalse(status(viewModel, ExhibitIds.NEAR_OCCURRENCE).unlocked)
        assertFalse(status(viewModel, ExhibitIds.CREATIVE_CHAOS).unlocked)
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
    fun solvingSimulatedProgressUnlocksAndContinueOpensNearOccurrence() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.SIMULATED_PROGRESS)

        viewModel.continueVisit()

        assertTrue(status(viewModel, ExhibitIds.SIMULATED_PROGRESS).completed)
        assertTrue(status(viewModel, ExhibitIds.NEAR_OCCURRENCE).unlocked)
        assertTrue(status(viewModel, ExhibitIds.NEAR_OCCURRENCE).current)
        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.NEAR_OCCURRENCE),
            viewModel.uiState.destination
        )
    }

    @Test
    fun solvingNearOccurrenceUnlocksAndContinueOpensCreativeChaos() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.NEAR_OCCURRENCE)

        viewModel.continueVisit()

        assertTrue(status(viewModel, ExhibitIds.NEAR_OCCURRENCE).completed)
        assertTrue(status(viewModel, ExhibitIds.CREATIVE_CHAOS).unlocked)
        assertTrue(status(viewModel, ExhibitIds.CREATIVE_CHAOS).current)
        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.CREATIVE_CHAOS),
            viewModel.uiState.destination
        )
    }

    @Test
    fun completingCreativeChaosOpensFinale() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.CREATIVE_CHAOS)

        viewModel.continueVisit()

        assertEquals(MuseumDestination.Finale, viewModel.uiState.destination)
        assertTrue(viewModel.uiState.visitStatuses.all { it.completed })
        assertFalse(viewModel.uiState.visitStatuses.any { it.current })
    }

    @Test
    fun returningFromFinaleOpensCompletedEntranceWithoutLosingProgress() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.CREATIVE_CHAOS)
        viewModel.continueVisit()

        viewModel.returnToEntrance()

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

        WORK_APPARENT_TRACE_SEQUENCE.forEach(viewModel::traceWorkApparent)
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
    fun advancingAndPreservingNearOccurrenceUpdatesUiState() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.SIMULATED_PROGRESS)
        viewModel.continueVisit()

        viewModel.advanceNearOccurrence()
        viewModel.advanceNearOccurrence()
        viewModel.preserveNearOccurrence()

        assertEquals(3, viewModel.uiState.nearOccurrence.progress.attempts)
        assertEquals(
            NearOccurrenceStage.AT_THRESHOLD,
            viewModel.uiState.nearOccurrence.puzzleState.stage
        )
        assertTrue(viewModel.uiState.nearOccurrence.puzzleState.solved)
        assertEquals(
            NearOccurrenceFeedback.PUZZLE_SOLVED,
            viewModel.uiState.nearOccurrence.feedback
        )
        assertTrue(status(viewModel, ExhibitIds.NEAR_OCCURRENCE).completed)
    }

    @Test
    fun returningToEntrancePreservesNearOccurrenceStageAndAttempts() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.SIMULATED_PROGRESS)
        viewModel.continueVisit()
        viewModel.advanceNearOccurrence()

        viewModel.returnToEntrance()
        viewModel.resumeVisit()

        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.NEAR_OCCURRENCE),
            viewModel.uiState.destination
        )
        assertEquals(1, viewModel.uiState.nearOccurrence.progress.attempts)
        assertEquals(
            NearOccurrenceStage.SHIFTING,
            viewModel.uiState.nearOccurrence.puzzleState.stage
        )
        assertEquals(
            NearOccurrenceFeedback.SHIFTING,
            viewModel.uiState.nearOccurrence.feedback
        )
    }

    @Test
    fun selectingAndCombiningCreativeChaosUpdatesUiStateWithoutCountingSelections() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.NEAR_OCCURRENCE)
        viewModel.continueVisit()

        viewModel.toggleCreativeChaosPiece(ChaosPiece.GRID)
        viewModel.toggleCreativeChaosPiece(ChaosPiece.SKETCH)

        assertEquals(
            setOf(ChaosPiece.GRID, ChaosPiece.SKETCH),
            viewModel.uiState.creativeChaos.puzzleState.selectedPieces
        )
        assertEquals(0, viewModel.uiState.creativeChaos.progress.attempts)

        viewModel.combineCreativeChaos()

        assertEquals(1, viewModel.uiState.creativeChaos.progress.attempts)
        assertEquals(
            CreativeChaosStep.ADD_MOTION,
            viewModel.uiState.creativeChaos.puzzleState.step
        )
        assertEquals(
            CreativeChaosFeedback.PATTERN_CREATED,
            viewModel.uiState.creativeChaos.feedback
        )
    }

    @Test
    fun restartMuseumClearsAllProgressFeedbackAndReturnsToEntrance() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.NEAR_OCCURRENCE)
        viewModel.continueVisit()
        viewModel.toggleCreativeChaosPiece(ChaosPiece.GRID)
        viewModel.toggleCreativeChaosPiece(ChaosPiece.CODE)
        viewModel.combineCreativeChaos()

        viewModel.restartMuseum()

        assertEquals(MuseumDestination.Entrance, viewModel.uiState.destination)
        assertEquals(ExhibitProgress(), viewModel.uiState.reappearingPen.progress)
        assertEquals(ExhibitProgress(), viewModel.uiState.slightlyWrong.progress)
        assertEquals(ExhibitProgress(), viewModel.uiState.workApparent.progress)
        assertEquals(ExhibitProgress(), viewModel.uiState.simulatedProgress.progress)
        assertEquals(ExhibitProgress(), viewModel.uiState.nearOccurrence.progress)
        assertEquals(ExhibitProgress(), viewModel.uiState.creativeChaos.progress)
        assertEquals(ReappearingPenState(), viewModel.uiState.reappearingPen.puzzleState)
        assertEquals(SlightlyWrongState(), viewModel.uiState.slightlyWrong.puzzleState)
        assertEquals(WorkApparentState(), viewModel.uiState.workApparent.puzzleState)
        assertEquals(
            SimulatedProgressState(),
            viewModel.uiState.simulatedProgress.puzzleState
        )
        assertEquals(
            NearOccurrenceState(),
            viewModel.uiState.nearOccurrence.puzzleState
        )
        assertEquals(
            CreativeChaosState(),
            viewModel.uiState.creativeChaos.puzzleState
        )
        assertEquals(null, viewModel.uiState.reappearingPen.feedback)
        assertEquals(null, viewModel.uiState.slightlyWrong.feedback)
        assertEquals(null, viewModel.uiState.workApparent.feedback)
        assertEquals(null, viewModel.uiState.simulatedProgress.feedback)
        assertEquals(null, viewModel.uiState.nearOccurrence.feedback)
        assertEquals(null, viewModel.uiState.creativeChaos.feedback)
        assertTrue(status(viewModel, ExhibitIds.REAPPEARING_PEN).current)
        assertFalse(status(viewModel, ExhibitIds.SLIGHTLY_WRONG).unlocked)
        assertFalse(status(viewModel, ExhibitIds.WORK_APPARENT).unlocked)
        assertFalse(status(viewModel, ExhibitIds.SIMULATED_PROGRESS).unlocked)
        assertFalse(status(viewModel, ExhibitIds.NEAR_OCCURRENCE).unlocked)
        assertFalse(status(viewModel, ExhibitIds.CREATIVE_CHAOS).unlocked)
    }

    @Test
    fun actionsForANonCurrentDestinationAreIgnored() {
        val viewModel = MuseumGameViewModel()
        viewModel.resumeVisit()

        viewModel.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)
        viewModel.traceWorkApparent(WorkApparentStage.TASKS_RECEIVED)
        viewModel.classifySimulatedProgress(ProgressCategory.ACTIVITY)
        viewModel.advanceNearOccurrence()
        viewModel.toggleCreativeChaosPiece(ChaosPiece.GRID)
        viewModel.combineCreativeChaos()

        assertEquals(ExhibitProgress(), viewModel.uiState.slightlyWrong.progress)
        assertEquals(null, viewModel.uiState.slightlyWrong.feedback)
        assertEquals(ExhibitProgress(), viewModel.uiState.workApparent.progress)
        assertEquals(null, viewModel.uiState.workApparent.feedback)
        assertEquals(ExhibitProgress(), viewModel.uiState.simulatedProgress.progress)
        assertEquals(null, viewModel.uiState.simulatedProgress.feedback)
        assertEquals(ExhibitProgress(), viewModel.uiState.nearOccurrence.progress)
        assertEquals(null, viewModel.uiState.nearOccurrence.feedback)
        assertEquals(ExhibitProgress(), viewModel.uiState.creativeChaos.progress)
        assertEquals(null, viewModel.uiState.creativeChaos.feedback)

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
        assertEquals(NearOccurrenceUiState(), viewModel.uiState.nearOccurrence)
        assertEquals(CreativeChaosUiState(), viewModel.uiState.creativeChaos)
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
        assertEquals(NearOccurrenceUiState(), viewModel.uiState.nearOccurrence)
        assertEquals(CreativeChaosUiState(), viewModel.uiState.creativeChaos)
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
        assertEquals(NearOccurrenceUiState(), viewModel.uiState.nearOccurrence)
        assertEquals(CreativeChaosUiState(), viewModel.uiState.creativeChaos)
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
        assertEquals(NearOccurrenceUiState(), viewModel.uiState.nearOccurrence)
        assertEquals(CreativeChaosUiState(), viewModel.uiState.creativeChaos)
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
    fun restartingSimulatedProgressAfterNearOccurrenceProgressResetsBoth() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.SIMULATED_PROGRESS)
        viewModel.continueVisit()
        viewModel.advanceNearOccurrence()
        viewModel.returnToEntrance()
        viewModel.openExhibit(ExhibitIds.SIMULATED_PROGRESS)

        viewModel.restartCurrentExhibit()

        assertEquals(SimulatedProgressUiState(), viewModel.uiState.simulatedProgress)
        assertEquals(NearOccurrenceUiState(), viewModel.uiState.nearOccurrence)
        assertEquals(CreativeChaosUiState(), viewModel.uiState.creativeChaos)
        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.SIMULATED_PROGRESS),
            viewModel.uiState.destination
        )
        assertFalse(status(viewModel, ExhibitIds.NEAR_OCCURRENCE).unlocked)
        assertFalse(status(viewModel, ExhibitIds.CREATIVE_CHAOS).unlocked)
    }

    @Test
    fun restartingNearOccurrencePreservesEarlierCompletedExhibits() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.NEAR_OCCURRENCE)
        viewModel.continueVisit()
        viewModel.toggleCreativeChaosPiece(ChaosPiece.GRID)
        viewModel.returnToEntrance()
        viewModel.openExhibit(ExhibitIds.NEAR_OCCURRENCE)

        viewModel.restartCurrentExhibit()

        assertTrue(viewModel.uiState.reappearingPen.puzzleState.solved)
        assertTrue(viewModel.uiState.slightlyWrong.puzzleState.solved)
        assertTrue(viewModel.uiState.workApparent.puzzleState.solved)
        assertTrue(viewModel.uiState.simulatedProgress.puzzleState.solved)
        assertEquals(NearOccurrenceUiState(), viewModel.uiState.nearOccurrence)
        assertEquals(CreativeChaosUiState(), viewModel.uiState.creativeChaos)
        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.NEAR_OCCURRENCE),
            viewModel.uiState.destination
        )
    }

    @Test
    fun restartingCreativeChaosPreservesEarlierCompletedExhibits() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.NEAR_OCCURRENCE)
        viewModel.continueVisit()
        viewModel.toggleCreativeChaosPiece(ChaosPiece.GRID)

        viewModel.restartCurrentExhibit()

        assertTrue(viewModel.uiState.reappearingPen.puzzleState.solved)
        assertTrue(viewModel.uiState.slightlyWrong.puzzleState.solved)
        assertTrue(viewModel.uiState.workApparent.puzzleState.solved)
        assertTrue(viewModel.uiState.simulatedProgress.puzzleState.solved)
        assertTrue(viewModel.uiState.nearOccurrence.puzzleState.solved)
        assertEquals(CreativeChaosUiState(), viewModel.uiState.creativeChaos)
        assertEquals(
            MuseumDestination.ExhibitDetail(ExhibitIds.CREATIVE_CHAOS),
            viewModel.uiState.destination
        )
    }

    @Test
    fun restartingMuseumFromFinaleClearsAllPuzzlesAndReturnsToEntrance() {
        val viewModel = MuseumGameViewModel()
        viewModel.solveThrough(ExhibitIds.CREATIVE_CHAOS)
        viewModel.continueVisit()

        viewModel.restartMuseum()

        assertEquals(MuseumDestination.Entrance, viewModel.uiState.destination)
        assertEquals(ReappearingPenUiState(), viewModel.uiState.reappearingPen)
        assertEquals(SlightlyWrongUiState(), viewModel.uiState.slightlyWrong)
        assertEquals(WorkApparentUiState(), viewModel.uiState.workApparent)
        assertEquals(SimulatedProgressUiState(), viewModel.uiState.simulatedProgress)
        assertEquals(NearOccurrenceUiState(), viewModel.uiState.nearOccurrence)
        assertEquals(CreativeChaosUiState(), viewModel.uiState.creativeChaos)
        assertTrue(status(viewModel, ExhibitIds.REAPPEARING_PEN).current)
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
