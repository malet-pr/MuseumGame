package com.example.museumgame.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.museumgame.R
import com.example.museumgame.viewmodel.MuseumDestination
import com.example.museumgame.viewmodel.MuseumGameViewModel

@Composable
fun MuseumScreen(
    modifier: Modifier = Modifier,
    viewModel: MuseumGameViewModel = viewModel()
) {
    val state = viewModel.uiState
    var showRestartMuseumConfirmation by rememberSaveable { mutableStateOf(false) }
    val requestMuseumRestart = { showRestartMuseumConfirmation = true }

    BackHandler(enabled = state.destination is MuseumDestination.ExhibitDetail) {
        viewModel.returnToEntrance()
    }

    when (val destination = state.destination) {
        MuseumDestination.Entrance -> MuseumEntranceContent(
            exhibits = state.exhibits,
            visitStatuses = state.visitStatuses,
            onResumeVisit = viewModel::resumeVisit,
            onOpenExhibit = viewModel::openExhibit,
            onRestartMuseum = requestMuseumRestart,
            modifier = modifier
        )

        is MuseumDestination.ExhibitDetail -> when (
            exhibitUiResources(destination.exhibitId).screen
        ) {
            ExhibitUiScreen.REAPPEARING_PEN -> ReappearingPenContent(
                progress = state.reappearingPen.progress,
                puzzleState = state.reappearingPen.puzzleState,
                feedback = state.reappearingPen.feedback,
                onInspectLocation = viewModel::inspectReappearingPen,
                onRestart = viewModel::restartCurrentExhibit,
                onRestartMuseum = requestMuseumRestart,
                onContinue = viewModel::continueVisit,
                isFinalExhibit =
                    state.visitStatuses.lastOrNull()?.exhibitId == destination.exhibitId,
                onReturnToEntrance = viewModel::returnToEntrance,
                modifier = modifier
            )

            ExhibitUiScreen.SLIGHTLY_WRONG -> SlightlyWrongContent(
                progress = state.slightlyWrong.progress,
                puzzleState = state.slightlyWrong.puzzleState,
                feedback = state.slightlyWrong.feedback,
                onAnswer = viewModel::answerSlightlyWrong,
                onRestart = viewModel::restartCurrentExhibit,
                onRestartMuseum = requestMuseumRestart,
                onContinue = viewModel::continueVisit,
                isFinalExhibit =
                    state.visitStatuses.lastOrNull()?.exhibitId == destination.exhibitId,
                onReturnToEntrance = viewModel::returnToEntrance,
                modifier = modifier
            )

            ExhibitUiScreen.WORK_APPARENT -> WorkApparentContent(
                progress = state.workApparent.progress,
                puzzleState = state.workApparent.puzzleState,
                feedback = state.workApparent.feedback,
                onTrace = viewModel::traceWorkApparent,
                onInterrupt = viewModel::interruptWorkApparent,
                onRestart = viewModel::restartCurrentExhibit,
                onRestartMuseum = requestMuseumRestart,
                onContinue = viewModel::continueVisit,
                isFinalExhibit =
                    state.visitStatuses.lastOrNull()?.exhibitId == destination.exhibitId,
                onReturnToEntrance = viewModel::returnToEntrance,
                modifier = modifier
            )

            ExhibitUiScreen.SIMULATED_PROGRESS -> SimulatedProgressContent(
                progress = state.simulatedProgress.progress,
                puzzleState = state.simulatedProgress.puzzleState,
                feedback = state.simulatedProgress.feedback,
                onClassify = viewModel::classifySimulatedProgress,
                onRestart = viewModel::restartCurrentExhibit,
                onRestartMuseum = requestMuseumRestart,
                onContinue = viewModel::continueVisit,
                isFinalExhibit =
                    state.visitStatuses.lastOrNull()?.exhibitId == destination.exhibitId,
                onReturnToEntrance = viewModel::returnToEntrance,
                modifier = modifier
            )

            ExhibitUiScreen.NEAR_OCCURRENCE -> NearOccurrenceContent(
                progress = state.nearOccurrence.progress,
                puzzleState = state.nearOccurrence.puzzleState,
                feedback = state.nearOccurrence.feedback,
                onAdvance = viewModel::advanceNearOccurrence,
                onPreserve = viewModel::preserveNearOccurrence,
                onRestart = viewModel::restartCurrentExhibit,
                onRestartMuseum = requestMuseumRestart,
                onContinue = viewModel::continueVisit,
                isFinalExhibit =
                    state.visitStatuses.lastOrNull()?.exhibitId == destination.exhibitId,
                onReturnToEntrance = viewModel::returnToEntrance,
                modifier = modifier
            )

        }
    }

    if (showRestartMuseumConfirmation) {
        AlertDialog(
            onDismissRequest = { showRestartMuseumConfirmation = false },
            title = {
                Text(stringResource(R.string.restart_museum_confirmation_title))
            },
            text = {
                Text(stringResource(R.string.restart_museum_confirmation_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRestartMuseumConfirmation = false
                        viewModel.restartMuseum()
                    }
                ) {
                    Text(stringResource(R.string.restart_museum_confirmation_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRestartMuseumConfirmation = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
