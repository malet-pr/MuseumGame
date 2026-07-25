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
    fun restartingPenAfterSlightlyWrongProgressResetsBothExhibits() {
        val game = newGame()
        game.solveThrough(ExhibitIds.REAPPEARING_PEN)
        game.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)

        game.restartExhibit(ExhibitIds.REAPPEARING_PEN)

        assertEquals(ExhibitProgress(), game.reappearingPenProgress)
        assertEquals(ReappearingPenState(), game.reappearingPenState)
        assertEquals(ExhibitProgress(), game.slightlyWrongProgress)
        assertEquals(SlightlyWrongState(), game.slightlyWrongState)
        assertNull(game.reappearingPenFeedback)
        assertNull(game.slightlyWrongFeedback)
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
    fun progressionStartsAtPenWithSlightlyWrongLocked() {
        val game = newGame()

        assertEquals(ExhibitIds.REAPPEARING_PEN, game.firstUnfinishedExhibitId())
        assertTrue(game.isUnlocked(ExhibitIds.REAPPEARING_PEN))
        assertFalse(game.isUnlocked(ExhibitIds.SLIGHTLY_WRONG))
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
        assertTrue(game.visitStatuses().first { it.exhibitId == ExhibitIds.REAPPEARING_PEN }.completed)
    }

    @Test
    fun restartMuseumClearsBothPuzzlesAndRestoresInitialProgression() {
        val game = newGame()
        game.solveThrough(ExhibitIds.REAPPEARING_PEN)
        game.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)

        game.restartMuseum()

        assertEquals(ExhibitProgress(), game.reappearingPenProgress)
        assertEquals(ExhibitProgress(), game.slightlyWrongProgress)
        assertEquals(ReappearingPenState(), game.reappearingPenState)
        assertEquals(SlightlyWrongState(), game.slightlyWrongState)
        assertNull(game.reappearingPenFeedback)
        assertNull(game.slightlyWrongFeedback)
        assertEquals(ExhibitIds.REAPPEARING_PEN, game.firstUnfinishedExhibitId())
        assertFalse(game.isUnlocked(ExhibitIds.SLIGHTLY_WRONG))
    }

    @Test
    fun noCompletedExhibitCanBeLockedAcrossProgressAndRestart() {
        val game = newGame()

        assertNoCompletedLockedState(game)
        game.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)
        assertNoCompletedLockedState(game)
        game.solveThrough(ExhibitIds.REAPPEARING_PEN)
        assertNoCompletedLockedState(game)
        game.solveThrough(ExhibitIds.SLIGHTLY_WRONG)
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
