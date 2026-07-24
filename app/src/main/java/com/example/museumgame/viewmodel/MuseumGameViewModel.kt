package com.example.museumgame.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.museumgame.game.MuseumGame
import com.example.museumgame.game.PenInspectionFeedback
import com.example.museumgame.game.PenLocation
import com.example.museumgame.game.ReappearingPenState
import com.example.museumgame.model.Exhibit
import com.example.museumgame.model.ExhibitIds

sealed interface MuseumDestination {
    data object Hall : MuseumDestination
    data class ExhibitDetail(val exhibit: Exhibit) : MuseumDestination
}

data class MuseumUiState(
    val destination: MuseumDestination = MuseumDestination.Hall,
    val exhibits: List<Exhibit>,
    val attempts: Int = 0,
    val solved: Boolean = false,
    val reappearingPenState: ReappearingPenState = ReappearingPenState(),
    val penFeedback: PenInspectionFeedback? = null
)

class MuseumGameViewModel : ViewModel() {

    private val exhibits = listOf(
        Exhibit(
            id = ExhibitIds.REAPPEARING_PEN,
            name = "The Reappearing Pen",
            description = "The pen vanishes from its case, then quietly reappears. You found the anomaly!",
            isAnomaly = true
        )
    )

    private val game = MuseumGame(exhibits)

    var uiState by mutableStateOf(
        MuseumUiState(
            exhibits = exhibits
        )
    )
        private set

    fun openExhibit(exhibit: Exhibit) {
        if (exhibit in exhibits) {
            uiState = uiState.copy(
                destination = MuseumDestination.ExhibitDetail(exhibit)
            )
        }
    }

    fun returnToHall() {
        uiState = uiState.copy(destination = MuseumDestination.Hall)
    }

    fun inspectReappearingPen(location: PenLocation) {
        val result = game.inspectReappearingPen(location)
        uiState = uiState.copy(
            attempts = game.attempts,
            solved = game.solved,
            reappearingPenState = result.state,
            penFeedback = result.feedback
        )
    }

    fun restart() {
        game.restart()

        uiState = uiState.copy(
            attempts = game.attempts,
            solved = game.solved,
            reappearingPenState = game.reappearingPenState,
            penFeedback = null
        )
    }
}
