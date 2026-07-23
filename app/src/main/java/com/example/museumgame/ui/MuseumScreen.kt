package com.example.museumgame.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.museumgame.viewmodel.MuseumGameViewModel

@Composable
fun MuseumScreen(
    modifier: Modifier = Modifier,
    viewModel: MuseumGameViewModel = viewModel()
) {
    MuseumScreenContent(
        exhibits = viewModel.exhibits,
        message = viewModel.message,
        attempts = viewModel.attempts,
        solved = viewModel.solved,
        onInspect = viewModel::inspect,
        onRestart = viewModel::restart,
        modifier = modifier
    )
}