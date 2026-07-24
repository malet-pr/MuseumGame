package com.example.museumgame.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.ExhibitVisitStatus
import com.example.museumgame.game.MuseumGame
import com.example.museumgame.game.PenInspectionFeedback
import com.example.museumgame.game.PenLocation
import com.example.museumgame.game.ReappearingPenState
import com.example.museumgame.game.SlightlyWrongDetail
import com.example.museumgame.game.SlightlyWrongFeedback
import com.example.museumgame.game.SlightlyWrongState
import com.example.museumgame.model.Exhibit
import com.example.museumgame.model.ExhibitIds

sealed interface MuseumDestination {
    data object Entrance : MuseumDestination
    data class ExhibitDetail(val exhibitId: String) : MuseumDestination
}

data class MuseumUiState(
    val destination: MuseumDestination = MuseumDestination.Entrance,
    val exhibits: List<Exhibit>,
    val visitStatuses: List<ExhibitVisitStatus>,
    val reappearingPen: ReappearingPenUiState = ReappearingPenUiState(),
    val slightlyWrong: SlightlyWrongUiState = SlightlyWrongUiState()
)

data class ReappearingPenUiState(
    val progress: ExhibitProgress = ExhibitProgress(),
    val puzzleState: ReappearingPenState = ReappearingPenState(),
    val feedback: PenInspectionFeedback? = null
)

data class SlightlyWrongUiState(
    val progress: ExhibitProgress = ExhibitProgress(),
    val puzzleState: SlightlyWrongState = SlightlyWrongState(),
    val feedback: SlightlyWrongFeedback? = null
)

class MuseumGameViewModel : ViewModel() {

    private val exhibits = listOf(
        Exhibit(
            id = ExhibitIds.REAPPEARING_PEN,
            name = "The Reappearing Pen",
            description = "The pen vanishes from its case, then quietly reappears. You found the anomaly!",
            isAnomaly = true
        ),
        Exhibit(
            id = ExhibitIds.SLIGHTLY_WRONG,
            name = "Slightly Wrong",
            description = "Familiar details have been remembered almost, but not quite, correctly.",
            isAnomaly = true
        )
    )

    private val game = MuseumGame(exhibits)

    var uiState by mutableStateOf(
        MuseumUiState(
            exhibits = exhibits,
            visitStatuses = game.visitStatuses()
        )
    )
        private set

    fun resumeVisit() {
        game.firstUnfinishedExhibitId()?.let { exhibitId ->
            uiState = uiState.copy(
                destination = MuseumDestination.ExhibitDetail(exhibitId)
            )
        }
    }

    fun openExhibit(exhibitId: String) {
        if (game.isCompleted(exhibitId) || game.isUnlocked(exhibitId)) {
            uiState = uiState.copy(
                destination = MuseumDestination.ExhibitDetail(exhibitId)
            )
        }
    }

    fun continueVisit() {
        val exhibitId =
            (uiState.destination as? MuseumDestination.ExhibitDetail)?.exhibitId
                ?: return
        if (!game.isCompleted(exhibitId)) return

        uiState = uiState.copy(
            destination = game.nextExhibitId(exhibitId)
                ?.let { MuseumDestination.ExhibitDetail(it) }
                ?: MuseumDestination.Entrance
        )
    }

    fun returnToEntrance() {
        uiState = uiState.copy(destination = MuseumDestination.Entrance)
    }

    fun inspectReappearingPen(location: PenLocation) {
        val result = game.inspectReappearingPen(location)
        uiState = uiState.copy(
            reappearingPen = uiState.reappearingPen.copy(
                progress = game.reappearingPenProgress,
                puzzleState = result.state,
                feedback = result.feedback
            ),
            visitStatuses = game.visitStatuses()
        )
    }

    fun answerSlightlyWrong(detail: SlightlyWrongDetail) {
        val result = game.answerSlightlyWrong(detail)
        uiState = uiState.copy(
            slightlyWrong = uiState.slightlyWrong.copy(
                progress = game.slightlyWrongProgress,
                puzzleState = result.state,
                feedback = result.feedback
            ),
            visitStatuses = game.visitStatuses()
        )
    }

    fun restartCurrentExhibit() {
        when ((uiState.destination as? MuseumDestination.ExhibitDetail)?.exhibitId) {
            ExhibitIds.REAPPEARING_PEN -> {
                game.restartReappearingPen()
                uiState = uiState.copy(
                    reappearingPen = ReappearingPenUiState(
                        progress = game.reappearingPenProgress,
                        puzzleState = game.reappearingPenState
                    ),
                    visitStatuses = game.visitStatuses()
                )
            }

            ExhibitIds.SLIGHTLY_WRONG -> {
                game.restartSlightlyWrong()
                uiState = uiState.copy(
                    slightlyWrong = SlightlyWrongUiState(
                        progress = game.slightlyWrongProgress,
                        puzzleState = game.slightlyWrongState
                    ),
                    visitStatuses = game.visitStatuses()
                )
            }

            else -> Unit
        }
    }

    fun restartMuseum() {
        game.restartMuseum()
        uiState = MuseumUiState(
            destination = MuseumDestination.Entrance,
            exhibits = exhibits,
            visitStatuses = game.visitStatuses(),
            reappearingPen = ReappearingPenUiState(
                progress = game.reappearingPenProgress,
                puzzleState = game.reappearingPenState
            ),
            slightlyWrong = SlightlyWrongUiState(
                progress = game.slightlyWrongProgress,
                puzzleState = game.slightlyWrongState
            )
        )
    }
}
