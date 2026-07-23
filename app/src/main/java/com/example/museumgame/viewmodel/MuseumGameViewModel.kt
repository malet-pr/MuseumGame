package com.example.museumgame.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.museumgame.game.MuseumGame
import com.example.museumgame.model.Exhibit

class MuseumGameViewModel : ViewModel() {

    val exhibits = listOf(
        Exhibit(
            name = "Ancient vase",
            description = "An ordinary ancient vase. Nothing suspicious.",
            isAnomaly = false
        ),
        Exhibit(
            name = "Portrait",
            description = "The portrait just blinked. You found the anomaly!",
            isAnomaly = true
        ),
        Exhibit(
            name = "Roman coin",
            description = "A perfectly normal Roman coin.",
            isAnomaly = false
        )
    )

    private val game = MuseumGame(exhibits)

    var message by mutableStateOf(INITIAL_MESSAGE)
        private set

    var attempts by mutableIntStateOf(0)
        private set

    var solved by mutableStateOf(false)
        private set

    fun inspect(exhibit: Exhibit) {
        message = game.inspect(exhibit)
        attempts = game.attempts
        solved = game.solved
    }

    fun restart() {
        game.restart()

        message = INITIAL_MESSAGE
        attempts = game.attempts
        solved = game.solved
    }

    companion object {
        private const val INITIAL_MESSAGE =
            "One exhibit does not belong. Inspect the museum."
    }
}
