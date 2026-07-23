package com.example.museumgame.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.museumgame.viewmodel.MuseumDestination
import com.example.museumgame.viewmodel.MuseumGameViewModel

@Composable
fun MuseumScreen(
    modifier: Modifier = Modifier,
    viewModel: MuseumGameViewModel = viewModel()
) {
    val state = viewModel.uiState

    BackHandler(enabled = state.destination is MuseumDestination.ExhibitDetail) {
        viewModel.returnToHall()
    }

    when (val destination = state.destination) {
        MuseumDestination.Hall -> MuseumHallContent(
            exhibits = state.exhibits,
            onOpenExhibit = viewModel::openExhibit,
            modifier = modifier
        )

        is MuseumDestination.ExhibitDetail -> ExhibitContent(
            exhibit = destination.exhibit,
            attempts = state.attempts,
            solved = state.solved,
            onInspect = viewModel::inspect,
            onRestart = viewModel::restart,
            onReturnToHall = viewModel::returnToHall,
            modifier = modifier
        )
    }
}
