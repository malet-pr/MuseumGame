package com.example.museumgame.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.museumgame.game.ChaosPiece
import com.example.museumgame.game.CreativeChaosFeedback
import com.example.museumgame.game.CreativeChaosState
import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.ExhibitVisitStatus
import com.example.museumgame.game.MuseumGame
import com.example.museumgame.game.NearOccurrenceFeedback
import com.example.museumgame.game.NearOccurrenceState
import com.example.museumgame.game.PenInspectionFeedback
import com.example.museumgame.game.PenLocation
import com.example.museumgame.game.ProgressCategory
import com.example.museumgame.game.ReappearingPenState
import com.example.museumgame.game.SimulatedProgressFeedback
import com.example.museumgame.game.SimulatedProgressState
import com.example.museumgame.game.SlightlyWrongDetail
import com.example.museumgame.game.SlightlyWrongFeedback
import com.example.museumgame.game.SlightlyWrongState
import com.example.museumgame.game.WorkApparentFeedback
import com.example.museumgame.game.WorkApparentInterruption
import com.example.museumgame.game.WorkApparentStage
import com.example.museumgame.game.WorkApparentState
import com.example.museumgame.model.Exhibit
import com.example.museumgame.model.ExhibitIds

sealed interface MuseumDestination {
    data object Entrance : MuseumDestination
    data class ExhibitDetail(val exhibitId: String) : MuseumDestination
    data object Finale : MuseumDestination
}

data class MuseumUiState(
    val destination: MuseumDestination = MuseumDestination.Entrance,
    val exhibits: List<Exhibit>,
    val visitStatuses: List<ExhibitVisitStatus>,
    val reappearingPen: ReappearingPenUiState = ReappearingPenUiState(),
    val slightlyWrong: SlightlyWrongUiState = SlightlyWrongUiState(),
    val workApparent: WorkApparentUiState = WorkApparentUiState(),
    val simulatedProgress: SimulatedProgressUiState = SimulatedProgressUiState(),
    val nearOccurrence: NearOccurrenceUiState = NearOccurrenceUiState(),
    val creativeChaos: CreativeChaosUiState = CreativeChaosUiState()
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

data class WorkApparentUiState(
    val progress: ExhibitProgress = ExhibitProgress(),
    val puzzleState: WorkApparentState = WorkApparentState(),
    val feedback: WorkApparentFeedback? = null
)

data class SimulatedProgressUiState(
    val progress: ExhibitProgress = ExhibitProgress(),
    val puzzleState: SimulatedProgressState = SimulatedProgressState(),
    val feedback: SimulatedProgressFeedback? = null
)

data class NearOccurrenceUiState(
    val progress: ExhibitProgress = ExhibitProgress(),
    val puzzleState: NearOccurrenceState = NearOccurrenceState(),
    val feedback: NearOccurrenceFeedback? = null
)

data class CreativeChaosUiState(
    val progress: ExhibitProgress = ExhibitProgress(),
    val puzzleState: CreativeChaosState = CreativeChaosState(),
    val feedback: CreativeChaosFeedback? = null
)

class MuseumGameViewModel : ViewModel() {

    private val game = MuseumGame()
    private val exhibits = game.exhibits

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

        val nextExhibitId = game.nextExhibitId(exhibitId)
        uiState = uiState.copy(
            destination = when {
                nextExhibitId != null ->
                    MuseumDestination.ExhibitDetail(nextExhibitId)

                exhibitId == ExhibitIds.CREATIVE_CHAOS ->
                    MuseumDestination.Finale

                else -> MuseumDestination.Entrance
            }
        )
    }

    fun returnToEntrance() {
        uiState = uiState.copy(destination = MuseumDestination.Entrance)
    }

    fun inspectReappearingPen(location: PenLocation) {
        if (!canActIn(ExhibitIds.REAPPEARING_PEN)) return

        game.inspectReappearingPen(location)
        refreshDomainState()
    }

    fun answerSlightlyWrong(detail: SlightlyWrongDetail) {
        if (!canActIn(ExhibitIds.SLIGHTLY_WRONG)) return

        game.answerSlightlyWrong(detail)
        refreshDomainState()
    }

    fun traceWorkApparent(stage: WorkApparentStage) {
        if (!canActIn(ExhibitIds.WORK_APPARENT)) return

        game.traceWorkApparent(stage)
        refreshDomainState()
    }

    fun interruptWorkApparent(interruption: WorkApparentInterruption) {
        if (!canActIn(ExhibitIds.WORK_APPARENT)) return

        game.interruptWorkApparent(interruption)
        refreshDomainState()
    }

    fun classifySimulatedProgress(category: ProgressCategory) {
        if (!canActIn(ExhibitIds.SIMULATED_PROGRESS)) return

        game.classifySimulatedProgress(category)
        refreshDomainState()
    }

    fun advanceNearOccurrence() {
        if (!canActIn(ExhibitIds.NEAR_OCCURRENCE)) return

        game.advanceNearOccurrence()
        refreshDomainState()
    }

    fun preserveNearOccurrence() {
        if (!canActIn(ExhibitIds.NEAR_OCCURRENCE)) return

        game.preserveNearOccurrence()
        refreshDomainState()
    }

    fun toggleCreativeChaosPiece(piece: ChaosPiece) {
        if (!canActIn(ExhibitIds.CREATIVE_CHAOS)) return

        game.toggleCreativeChaosPiece(piece)
        refreshDomainState()
    }

    fun combineCreativeChaos() {
        if (!canActIn(ExhibitIds.CREATIVE_CHAOS)) return

        game.combineCreativeChaos()
        refreshDomainState()
    }

    fun restartCurrentExhibit() {
        val exhibitId =
            (uiState.destination as? MuseumDestination.ExhibitDetail)?.exhibitId
                ?: return
        if (!game.isUnlocked(exhibitId)) return

        game.restartExhibit(exhibitId)
        refreshDomainState()
    }

    fun restartMuseum() {
        game.restartMuseum()
        uiState = uiState.copy(destination = MuseumDestination.Entrance)
        refreshDomainState()
    }

    private fun canActIn(exhibitId: String): Boolean =
        uiState.destination == MuseumDestination.ExhibitDetail(exhibitId) &&
            game.isUnlocked(exhibitId)

    private fun refreshDomainState() {
        uiState = uiState.copy(
            visitStatuses = game.visitStatuses(),
            reappearingPen = ReappearingPenUiState(
                progress = game.reappearingPenProgress,
                puzzleState = game.reappearingPenState,
                feedback = game.reappearingPenFeedback
            ),
            slightlyWrong = SlightlyWrongUiState(
                progress = game.slightlyWrongProgress,
                puzzleState = game.slightlyWrongState,
                feedback = game.slightlyWrongFeedback
            ),
            workApparent = WorkApparentUiState(
                progress = game.workApparentProgress,
                puzzleState = game.workApparentState,
                feedback = game.workApparentFeedback
            ),
            simulatedProgress = SimulatedProgressUiState(
                progress = game.simulatedProgressProgress,
                puzzleState = game.simulatedProgressState,
                feedback = game.simulatedProgressFeedback
            ),
            nearOccurrence = NearOccurrenceUiState(
                progress = game.nearOccurrenceProgress,
                puzzleState = game.nearOccurrenceState,
                feedback = game.nearOccurrenceFeedback
            ),
            creativeChaos = CreativeChaosUiState(
                progress = game.creativeChaosProgress,
                puzzleState = game.creativeChaosState,
                feedback = game.creativeChaosFeedback
            )
        )
    }
}
