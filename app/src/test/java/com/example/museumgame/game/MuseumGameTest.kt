package com.example.museumgame.game

import com.example.museumgame.model.Exhibit
import com.example.museumgame.model.ExhibitIds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MuseumGameTest {

    private val vase = Exhibit(
        id = "ancient_vase",
        name = "Ancient vase",
        description = "Nothing suspicious.",
        isAnomaly = false
    )

    private val portrait = Exhibit(
        id = "portrait",
        name = "Portrait",
        description = "The portrait blinked.",
        isAnomaly = true
    )

    private val reappearingPen = Exhibit(
        id = ExhibitIds.REAPPEARING_PEN,
        name = "The Reappearing Pen",
        description = "The pen reappeared.",
        isAnomaly = true
    )

    @Test
    fun inspectingNormalExhibitIncreasesAttemptsWithoutSolvingRoom() {
        val game = MuseumGame(listOf(vase, portrait))

        game.inspect(vase)

        assertEquals(1, game.attempts)
        assertFalse(game.solved)
    }

    @Test
    fun inspectingAnomalySolvesRoom() {
        val game = MuseumGame(listOf(vase, portrait))

        game.inspect(portrait)

        assertEquals(1, game.attempts)
        assertTrue(game.solved)
    }

    @Test
    fun restartClearsProgress() {
        val game = MuseumGame(listOf(vase, portrait))
        game.inspect(portrait)

        game.restart()

        assertEquals(0, game.attempts)
        assertFalse(game.solved)
    }

    @Test
    fun inspectingAfterRoomIsSolvedDoesNotIncreaseAttempts() {
        val game = MuseumGame(listOf(vase, portrait))

        game.inspect(portrait)
        val message = game.inspect(vase)

        assertEquals(1, game.attempts)
        assertTrue(game.solved)
        assertEquals("The room is already solved.", message)
    }

    @Test
    fun penInspectionChoicesIncrementAttemptsAndSolveOnReturnToTarget() {
        val game = MuseumGame(listOf(reappearingPen))

        game.inspectReappearingPen(PenLocation.PAPERS)
        game.inspectReappearingPen(PenLocation.EMPTY_DESK)
        game.inspectReappearingPen(PenLocation.PAPERS)

        assertEquals(3, game.attempts)
        assertTrue(game.solved)
    }

    @Test
    fun penInspectionAfterSolvedDoesNotIncreaseAttempts() {
        val game = MuseumGame(listOf(reappearingPen))
        game.inspectReappearingPen(PenLocation.PAPERS)
        game.inspectReappearingPen(PenLocation.EMPTY_DESK)
        game.inspectReappearingPen(PenLocation.PAPERS)

        val result = game.inspectReappearingPen(PenLocation.FILING_CABINET)

        assertEquals(3, game.attempts)
        assertEquals(PenInspectionFeedback.ALREADY_SOLVED, result.feedback)
    }

    @Test
    fun restartClearsPenProgressAlongWithGameProgress() {
        val game = MuseumGame(listOf(reappearingPen))
        game.inspectReappearingPen(PenLocation.PAPERS)
        game.inspectReappearingPen(PenLocation.EMPTY_DESK)

        game.restart()

        assertEquals(0, game.attempts)
        assertFalse(game.solved)
        assertEquals(ReappearingPenState(), game.reappearingPenState)
    }
}
