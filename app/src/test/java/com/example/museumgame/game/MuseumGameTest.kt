package com.example.museumgame.game

import com.example.museumgame.model.Exhibit
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
}
