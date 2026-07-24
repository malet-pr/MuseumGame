package com.example.museumgame.game

import com.example.museumgame.model.Exhibit
import com.example.museumgame.model.ExhibitIds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MuseumGameTest {

    private val reappearingPen = Exhibit(
        id = ExhibitIds.REAPPEARING_PEN,
        name = "The Reappearing Pen",
        description = "The pen reappeared.",
        isAnomaly = true
    )

    private val slightlyWrong = Exhibit(
        id = ExhibitIds.SLIGHTLY_WRONG,
        name = "Slightly Wrong",
        description = "Familiar details are almost correct.",
        isAnomaly = true
    )

    @Test
    fun penChoicesIncrementOnlyPenAttemptsAndSolveOnTarget() {
        val game = MuseumGame(listOf(reappearingPen, slightlyWrong))

        game.inspectReappearingPen(PenLocation.PAPERS)
        game.inspectReappearingPen(PenLocation.EMPTY_DESK)
        game.inspectReappearingPen(PenLocation.PAPERS)

        assertEquals(3, game.reappearingPenProgress.attempts)
        assertTrue(game.reappearingPenProgress.solved)
        assertEquals(0, game.slightlyWrongProgress.attempts)
    }

    @Test
    fun penChoiceAfterSolvedDoesNotIncreaseAttempts() {
        val game = MuseumGame(listOf(reappearingPen, slightlyWrong))
        game.inspectReappearingPen(PenLocation.PAPERS)
        game.inspectReappearingPen(PenLocation.EMPTY_DESK)
        game.inspectReappearingPen(PenLocation.PAPERS)

        val result = game.inspectReappearingPen(PenLocation.FILING_CABINET)

        assertEquals(3, game.reappearingPenProgress.attempts)
        assertEquals(PenInspectionFeedback.ALREADY_SOLVED, result.feedback)
    }

    @Test
    fun slightlyWrongAnswersIncrementOnlyItsAttemptsAndSolveInOrder() {
        val game = MuseumGame(listOf(reappearingPen, slightlyWrong))

        game.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)
        game.answerSlightlyWrong(SlightlyWrongDetail.BOOKSHELF)
        game.answerSlightlyWrong(SlightlyWrongDetail.GLOBE)

        assertEquals(3, game.slightlyWrongProgress.attempts)
        assertTrue(game.slightlyWrongProgress.solved)
        assertEquals(0, game.reappearingPenProgress.attempts)
    }

    @Test
    fun wrongSlightlyWrongAnswerCountsWithoutAdvancing() {
        val game = MuseumGame(listOf(reappearingPen, slightlyWrong))

        val result = game.answerSlightlyWrong(SlightlyWrongDetail.ORRERY)

        assertEquals(1, game.slightlyWrongProgress.attempts)
        assertFalse(game.slightlyWrongProgress.solved)
        assertEquals(SlightlyWrongClue.WRONG_ORDER, result.state.currentClue)
        assertEquals(SlightlyWrongFeedback.INCORRECT, result.feedback)
    }

    @Test
    fun answerAfterSlightlyWrongSolvedDoesNotIncreaseAttempts() {
        val game = MuseumGame(listOf(reappearingPen, slightlyWrong))
        game.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)
        game.answerSlightlyWrong(SlightlyWrongDetail.BOOKSHELF)
        game.answerSlightlyWrong(SlightlyWrongDetail.GLOBE)

        val result = game.answerSlightlyWrong(SlightlyWrongDetail.ORRERY)

        assertEquals(3, game.slightlyWrongProgress.attempts)
        assertEquals(SlightlyWrongFeedback.ALREADY_SOLVED, result.feedback)
    }

    @Test
    fun restartingPenDoesNotResetSlightlyWrong() {
        val game = MuseumGame(listOf(reappearingPen, slightlyWrong))
        game.inspectReappearingPen(PenLocation.PAPERS)
        game.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)

        game.restartReappearingPen()

        assertEquals(ExhibitProgress(), game.reappearingPenProgress)
        assertEquals(ReappearingPenState(), game.reappearingPenState)
        assertEquals(1, game.slightlyWrongProgress.attempts)
        assertEquals(SlightlyWrongClue.INCOMPLETE_NAMES, game.slightlyWrongState.currentClue)
    }

    @Test
    fun restartingSlightlyWrongDoesNotResetPen() {
        val game = MuseumGame(listOf(reappearingPen, slightlyWrong))
        game.inspectReappearingPen(PenLocation.PAPERS)
        game.answerSlightlyWrong(SlightlyWrongDetail.CLOCK)

        game.restartSlightlyWrong()

        assertEquals(ExhibitProgress(), game.slightlyWrongProgress)
        assertEquals(SlightlyWrongState(), game.slightlyWrongState)
        assertEquals(1, game.reappearingPenProgress.attempts)
        assertEquals(PenLocation.PAPERS, game.reappearingPenState.targetLocation)
    }
}
