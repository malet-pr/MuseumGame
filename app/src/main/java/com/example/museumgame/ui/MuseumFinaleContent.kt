package com.example.museumgame.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.museumgame.R
import com.example.museumgame.ui.theme.MuseumGameTheme

@Composable
fun MuseumFinaleContent(
    onReturnToEntrance: () -> Unit,
    onRestartMuseum: () -> Unit,
    modifier: Modifier = Modifier
) {
    ResponsiveExhibitLayout(
        titleResource = R.string.finale_title,
        illustrationResource = R.drawable.kubernetes_city,
        illustrationDescriptionResource = R.string.finale_image_description,
        modifier = modifier
    ) {
        Text(stringResource(R.string.finale_completion_message))
        Text(stringResource(R.string.finale_teaser))
        Button(onClick = onReturnToEntrance) {
            Text(stringResource(R.string.return_to_entrance))
        }
        Button(onClick = onRestartMuseum) {
            Text(stringResource(R.string.restart_museum))
        }
    }
}

@Preview(name = "Museum finale - portrait", widthDp = 412, heightDp = 915)
@Composable
private fun MuseumFinalePortraitPreview() {
    MuseumGameTheme {
        MuseumFinaleContent(
            onReturnToEntrance = {},
            onRestartMuseum = {}
        )
    }
}

@Preview(name = "Museum finale - landscape", widthDp = 915, heightDp = 412)
@Composable
private fun MuseumFinaleLandscapePreview() {
    MuseumGameTheme {
        MuseumFinaleContent(
            onReturnToEntrance = {},
            onRestartMuseum = {}
        )
    }
}
