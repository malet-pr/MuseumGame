package com.example.museumgame.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.museumgame.model.ExhibitIds
import com.example.museumgame.viewmodel.MuseumDestination
import com.example.museumgame.viewmodel.MuseumGameViewModel

@Composable
fun MuseumScreen(
    modifier: Modifier = Modifier,
    viewModel: MuseumGameViewModel = viewModel()
) {
    val state = viewModel.uiState

    BackHandler(enabled = state.destination is MuseumDestination.ExhibitDetail) {
        viewModel.returnToEntrance()
    }

    when (val destination = state.destination) {
        MuseumDestination.Entrance -> MuseumEntranceContent(
            exhibits = state.exhibits,
            visitStatuses = state.visitStatuses,
            onResumeVisit = viewModel::resumeVisit,
            onOpenExhibit = viewModel::openExhibit,
            onRestartMuseum = viewModel::restartMuseum,
            modifier = modifier
        )

        is MuseumDestination.ExhibitDetail -> when (destination.exhibitId) {
            ExhibitIds.REAPPEARING_PEN -> ReappearingPenContent(
                progress = state.reappearingPen.progress,
                puzzleState = state.reappearingPen.puzzleState,
                feedback = state.reappearingPen.feedback,
                onInspectLocation = viewModel::inspectReappearingPen,
                onRestart = viewModel::restartCurrentExhibit,
                onRestartMuseum = viewModel::restartMuseum,
                onContinue = viewModel::continueVisit,
                isFinalExhibit =
                    state.visitStatuses.lastOrNull()?.exhibitId == destination.exhibitId,
                onReturnToEntrance = viewModel::returnToEntrance,
                modifier = modifier
            )

            ExhibitIds.SLIGHTLY_WRONG -> SlightlyWrongContent(
                progress = state.slightlyWrong.progress,
                puzzleState = state.slightlyWrong.puzzleState,
                feedback = state.slightlyWrong.feedback,
                onAnswer = viewModel::answerSlightlyWrong,
                onRestart = viewModel::restartCurrentExhibit,
                onRestartMuseum = viewModel::restartMuseum,
                onContinue = viewModel::continueVisit,
                isFinalExhibit =
                    state.visitStatuses.lastOrNull()?.exhibitId == destination.exhibitId,
                onReturnToEntrance = viewModel::returnToEntrance,
                modifier = modifier
            )

            else -> error("No screen mapped for exhibit ID: ${destination.exhibitId}")
        }
    }
}
