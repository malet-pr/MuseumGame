package com.example.museumgame.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.museumgame.R

@Composable
internal fun ExhibitNavigationActions(
    solved: Boolean,
    isFinalExhibit: Boolean,
    onContinue: () -> Unit,
    onRestartExhibit: () -> Unit,
    onRestartMuseum: () -> Unit,
    onReturnToEntrance: () -> Unit
) {
    if (solved) {
        Button(onClick = onContinue) {
            Text(
                stringResource(
                    if (isFinalExhibit) {
                        R.string.complete_visit
                    } else {
                        R.string.continue_visit
                    }
                )
            )
        }
    }
    Button(onClick = onRestartExhibit) {
        Text(stringResource(R.string.restart_exhibit))
    }
    Button(onClick = onRestartMuseum) {
        Text(stringResource(R.string.restart_museum))
    }
    Button(onClick = onReturnToEntrance) {
        Text(stringResource(R.string.return_to_entrance))
    }
}
