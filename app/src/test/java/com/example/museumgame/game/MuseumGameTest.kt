package com.example.museumgame.game

import com.example.museumgame.model.ExhibitIds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MuseumGameTest {

    @Test
    fun penChoicesIncrementOnlyPenAttemptsAndSolveOnTarget() {
        val game = newGame()

        game.inspectReappearingPen(PenLocation.PAPERS)
        game.inspectReappearingPen(PenLocation.EMPTY_DESK)
        game.inspectReappearingPen(PenLocation.PAPERS)

        assertEquals(3, game.reappearingPenProgress.attempts)
        assertTrue(game.isCompleted(ExhibitIds.REAPPEARING_PEN))
        assertEquals(0, game.slightlyWrongProgress.attempts)
        assertEquals(0, game.workApparentProgress.attempts)
    }

    @Test
    fun penChoiceAfterSolvedDoesNotIncreaseAttempts() {
        val game = newGame()
        game.inspectReappearingPen(PenLocation.PAPERS)
        game.inspectReappearingPen(PenLocation.EMPTY_DESK)
        game.inspectReappearingPen(PenLocation.PAPERS)

        val result = game.inspectReappearingPen(PenLocation.FILING_CABINET)

        assertEquals(3, game.reappearingPenProgress.attempts)
        assertEquals(PenInspectionFeedback.ALREADY_SOLVED, result.feedback)
    }

    @Test
    fun slightlyWrongAnswersIncrementOnlyItsAttemptsAndSolveInOrder() {
        val game = newGame()
        game.solveThrough(ExhibitIds.REAPPEARING_PEN)

        game.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)
        game.answerSlightlyWrong(SlightlyWrongDetail.BOOKSHELF)
        game.answerSlightlyWrong(SlightlyWrongDetail.GLOBE)

        assertEquals(3, game.slightlyWrongProgress.attempts)
        assertTrue(game.isCompleted(ExhibitIds.SLIGHTLY_WRONG))
        assertEquals(3, game.reappearingPenProgress.attempts)
        assertEquals(0, game.workApparentProgress.attempts)
    }

    @Test
    fun wrongSlightlyWrongAnswerCountsWithoutAdvancing() {
        val game = newGame()
        game.solveThrough(ExhibitIds.REAPPEARING_PEN)

        val result = game.answerSlightlyWrong(SlightlyWrongDetail.ORRERY)

        assertEquals(1, game.slightlyWrongProgress.attempts)
        assertFalse(game.isCompleted(ExhibitIds.SLIGHTLY_WRONG))
        assertEquals(SlightlyWrongClue.WRONG_ORDER, result.state.currentClue)
        assertEquals(SlightlyWrongFeedback.INCORRECT, result.feedback)
    }

    @Test
    fun answerAfterSlightlyWrongSolvedDoesNotIncreaseAttempts() {
        val game = newGame()
        game.solveThrough(ExhibitIds.REAPPEARING_PEN)
        game.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)
        game.answerSlightlyWrong(SlightlyWrongDetail.BOOKSHELF)
        game.answerSlightlyWrong(SlightlyWrongDetail.GLOBE)

        val result = game.answerSlightlyWrong(SlightlyWrongDetail.ORRERY)

        assertEquals(3, game.slightlyWrongProgress.attempts)
        assertEquals(SlightlyWrongFeedback.ALREADY_SOLVED, result.feedback)
    }

    @Test
    fun slightlyWrongCannotAdvanceBeforePenCompletion() {
        val game = newGame()

        val result = game.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)

        assertEquals(SlightlyWrongFeedback.LOCKED, result.feedback)
        assertEquals(ExhibitProgress(), game.slightlyWrongProgress)
        assertEquals(SlightlyWrongState(), game.slightlyWrongState)
        assertFalse(game.isCompleted(ExhibitIds.SLIGHTLY_WRONG))
    }

    @Test
    fun workApparentCannotAdvanceBeforeSlightlyWrongCompletion() {
        val game = newGame()
        game.solveThrough(ExhibitIds.REAPPEARING_PEN)

        val traceResult = game.traceWorkApparent(WorkApparentStage.TASKS_RECEIVED)
        val interruptResult = game.interruptWorkApparent(
            WorkApparentInterruption.COMPLETE_ONE_TASK
        )

        assertEquals(WorkApparentFeedback.LOCKED, traceResult.feedback)
        assertEquals(WorkApparentFeedback.LOCKED, interruptResult.feedback)
        assertEquals(ExhibitProgress(), game.workApparentProgress)
        assertEquals(WorkApparentState(), game.workApparentState)
        assertFalse(game.isCompleted(ExhibitIds.WORK_APPARENT))
    }

    @Test
    fun workApparentChoicesCountAndCompletingOneTaskSolvesIt() {
        val game = newGame()
        game.solveThrough(ExhibitIds.SLIGHTLY_WRONG)

        WorkApparentStage.entries.forEach(game::traceWorkApparent)
        game.interruptWorkApparent(WorkApparentInterruption.REORGANIZE_TASKS)
        val result = game.interruptWorkApparent(
            WorkApparentInterruption.COMPLETE_ONE_TASK
        )

        assertEquals(7, game.workApparentProgress.attempts)
        assertTrue(game.workApparentState.solved)
        assertEquals(WorkApparentFeedback.PUZZLE_SOLVED, result.feedback)
    }

    @Test
    fun workApparentChoiceAfterSolvedDoesNotIncreaseAttempts() {
        val game = newGame()
        game.solveThrough(ExhibitIds.WORK_APPARENT)

        val result = game.traceWorkApparent(WorkApparentStage.TASKS_RECEIVED)

        assertEquals(6, game.workApparentProgress.attempts)
        assertEquals(WorkApparentFeedback.ALREADY_SOLVED, result.feedback)
    }

    @Test
    fun restartingPenAfterSlightlyWrongProgressResetsBothExhibits() {
        val game = newGame()
        game.solveThrough(ExhibitIds.REAPPEARING_PEN)
        game.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)

        game.restartExhibit(ExhibitIds.REAPPEARING_PEN)

        assertEquals(ExhibitProgress(), game.reappearingPenProgress)
        assertEquals(ReappearingPenState(), game.reappearingPenState)
        assertEquals(ExhibitProgress(), game.slightlyWrongProgress)
        assertEquals(SlightlyWrongState(), game.slightlyWrongState)
        assertEquals(ExhibitProgress(), game.workApparentProgress)
        assertEquals(WorkApparentState(), game.workApparentState)
        assertNull(game.reappearingPenFeedback)
        assertNull(game.slightlyWrongFeedback)
        assertNull(game.workApparentFeedback)
        assertFalse(game.isUnlocked(ExhibitIds.SLIGHTLY_WRONG))
    }

    @Test
    fun restartingSlightlyWrongDoesNotResetPen() {
        val game = newGame()
        game.solveThrough(ExhibitIds.REAPPEARING_PEN)
        game.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)

        game.restartExhibit(ExhibitIds.SLIGHTLY_WRONG)

        assertEquals(ExhibitProgress(), game.slightlyWrongProgress)
        assertEquals(SlightlyWrongState(), game.slightlyWrongState)
        assertEquals(3, game.reappearingPenProgress.attempts)
        assertTrue(game.reappearingPenState.solved)
        assertEquals(PenInspectionFeedback.PEN_FOUND, game.reappearingPenFeedback)
        assertNull(game.slightlyWrongFeedback)
        assertTrue(game.isUnlocked(ExhibitIds.SLIGHTLY_WRONG))
    }

    @Test
    fun restartingSlightlyWrongAfterWorkProgressResetsBoth() {
        val game = newGame()
        game.solveThrough(ExhibitIds.SLIGHTLY_WRONG)
        game.traceWorkApparent(WorkApparentStage.TASKS_RECEIVED)

        game.restartExhibit(ExhibitIds.SLIGHTLY_WRONG)

        assertEquals(ExhibitProgress(), game.slightlyWrongProgress)
        assertEquals(SlightlyWrongState(), game.slightlyWrongState)
        assertEquals(ExhibitProgress(), game.workApparentProgress)
        assertEquals(WorkApparentState(), game.workApparentState)
        assertNull(game.slightlyWrongFeedback)
        assertNull(game.workApparentFeedback)
        assertFalse(game.isUnlocked(ExhibitIds.WORK_APPARENT))
    }

    @Test
    fun restartingWorkApparentPreservesEarlierExhibits() {
        val game = newGame()
        game.solveThrough(ExhibitIds.SLIGHTLY_WRONG)
        game.traceWorkApparent(WorkApparentStage.TASKS_RECEIVED)

        game.restartExhibit(ExhibitIds.WORK_APPARENT)

        assertTrue(game.reappearingPenState.solved)
        assertTrue(game.slightlyWrongState.solved)
        assertEquals(3, game.reappearingPenProgress.attempts)
        assertEquals(3, game.slightlyWrongProgress.attempts)
        assertEquals(ExhibitProgress(), game.workApparentProgress)
        assertEquals(WorkApparentState(), game.workApparentState)
        assertNull(game.workApparentFeedback)
        assertTrue(game.isUnlocked(ExhibitIds.WORK_APPARENT))
    }

    @Test
    fun progressionStartsAtPenWithSlightlyWrongLocked() {
        val game = newGame()

        assertEquals(ExhibitIds.REAPPEARING_PEN, game.firstUnfinishedExhibitId())
        assertTrue(game.isUnlocked(ExhibitIds.REAPPEARING_PEN))
        assertFalse(game.isUnlocked(ExhibitIds.SLIGHTLY_WRONG))
        assertFalse(game.isUnlocked(ExhibitIds.WORK_APPARENT))
        assertEquals(
            ExhibitIds.SLIGHTLY_WRONG,
            game.nextExhibitId(ExhibitIds.REAPPEARING_PEN)
        )
    }

    @Test
    fun solvingPenUnlocksSlightlyWrongAndMakesItCurrent() {
        val game = newGame()
        game.solveThrough(ExhibitIds.REAPPEARING_PEN)

        assertEquals(ExhibitIds.SLIGHTLY_WRONG, game.firstUnfinishedExhibitId())
        assertTrue(game.isUnlocked(ExhibitIds.SLIGHTLY_WRONG))
        assertFalse(game.isUnlocked(ExhibitIds.WORK_APPARENT))
        assertTrue(game.visitStatuses().first { it.exhibitId == ExhibitIds.REAPPEARING_PEN }.completed)
    }

    @Test
    fun solvingSlightlyWrongUnlocksWorkApparentAndMakesItCurrent() {
        val game = newGame()

        game.solveThrough(ExhibitIds.SLIGHTLY_WRONG)

        assertEquals(ExhibitIds.WORK_APPARENT, game.firstUnfinishedExhibitId())
        assertTrue(game.isUnlocked(ExhibitIds.WORK_APPARENT))
    }

    @Test
    fun restartMuseumClearsEveryPuzzleAndRestoresInitialProgression() {
        val game = newGame()
        game.solveThrough(ExhibitIds.SLIGHTLY_WRONG)
        game.traceWorkApparent(WorkApparentStage.TASKS_RECEIVED)

        game.restartMuseum()

        assertEquals(ExhibitProgress(), game.reappearingPenProgress)
        assertEquals(ExhibitProgress(), game.slightlyWrongProgress)
        assertEquals(ExhibitProgress(), game.workApparentProgress)
        assertEquals(ReappearingPenState(), game.reappearingPenState)
        assertEquals(SlightlyWrongState(), game.slightlyWrongState)
        assertEquals(WorkApparentState(), game.workApparentState)
        assertNull(game.reappearingPenFeedback)
        assertNull(game.slightlyWrongFeedback)
        assertNull(game.workApparentFeedback)
        assertEquals(ExhibitIds.REAPPEARING_PEN, game.firstUnfinishedExhibitId())
        assertFalse(game.isUnlocked(ExhibitIds.SLIGHTLY_WRONG))
        assertFalse(game.isUnlocked(ExhibitIds.WORK_APPARENT))
    }

    @Test
    fun noCompletedExhibitCanBeLockedAcrossProgressAndRestart() {
        val game = newGame()

        assertNoCompletedLockedState(game)
        game.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)
        assertNoCompletedLockedState(game)
        game.traceWorkApparent(WorkApparentStage.TASKS_RECEIVED)
        assertNoCompletedLockedState(game)
        game.solveThrough(ExhibitIds.REAPPEARING_PEN)
        assertNoCompletedLockedState(game)
        game.solveThrough(ExhibitIds.SLIGHTLY_WRONG)
        assertNoCompletedLockedState(game)
        game.solveThrough(ExhibitIds.WORK_APPARENT)
        assertNoCompletedLockedState(game)
        game.restartExhibit(ExhibitIds.WORK_APPARENT)
        assertNoCompletedLockedState(game)
        game.restartExhibit(ExhibitIds.SLIGHTLY_WRONG)
        assertNoCompletedLockedState(game)
        game.restartExhibit(ExhibitIds.REAPPEARING_PEN)
        assertNoCompletedLockedState(game)
    }

    private fun newGame() = MuseumGame()

    private fun assertNoCompletedLockedState(game: MuseumGame) {
        assertTrue(game.visitStatuses().none { it.completed && !it.unlocked })
    }
}
