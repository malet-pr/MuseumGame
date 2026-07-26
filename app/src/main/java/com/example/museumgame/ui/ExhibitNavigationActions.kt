package com.example.museumgame.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (solved) {
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            ) {
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
        FilledTonalButton(
            onClick = onRestartExhibit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.restart_exhibit))
        }
        MuseumDestructiveButton(
            onClick = onRestartMuseum,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.restart_museum))
        }
        OutlinedButton(
            onClick = onReturnToEntrance,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.return_to_entrance))
        }
    }
}
