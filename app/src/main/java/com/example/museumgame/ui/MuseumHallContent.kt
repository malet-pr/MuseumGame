package com.example.museumgame.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.museumgame.R
import com.example.museumgame.model.Exhibit
import com.example.museumgame.ui.theme.MuseumGameTheme
import com.example.museumgame.viewmodel.MuseumGameViewModel

@Composable
fun MuseumHallContent(
    exhibits: List<Exhibit>,
    onOpenExhibit: (Exhibit) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        if (maxWidth >= WIDE_LAYOUT_MIN_WIDTH) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                HallIllustration(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                )
                HallActions(
                    exhibits = exhibits,
                    onOpenExhibit = onOpenExhibit,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(stringResource(R.string.museum_hall_title))
                HallIllustration(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(maxHeight = 420.dp)
                )
                HallExhibitButtons(exhibits, onOpenExhibit)
            }
        }
    }
}

@Composable
private fun HallIllustration(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.museum_hall),
        contentDescription = stringResource(R.string.museum_hall_description),
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun HallActions(
    exhibits: List<Exhibit>,
    onOpenExhibit: (Exhibit) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.museum_hall_title))
        HallExhibitButtons(exhibits, onOpenExhibit)
    }
}

@Composable
private fun HallExhibitButtons(
    exhibits: List<Exhibit>,
    onOpenExhibit: (Exhibit) -> Unit
) {
    exhibits.forEach { exhibit ->
        Button(onClick = { onOpenExhibit(exhibit) }) {
            Text(
                stringResource(
                    R.string.open_exhibit,
                    stringResource(exhibitNameResource(exhibit.id))
                )
            )
        }
    }
}

private val previewPen = Exhibit(
    id = MuseumGameViewModel.REAPPEARING_PEN_ID,
    name = "The Reappearing Pen",
    description = "The pen reappeared.",
    isAnomaly = true
)

@Preview(name = "Museum hall - portrait", widthDp = 412, heightDp = 915)
@Composable
private fun MuseumHallPortraitPreview() {
    MuseumGameTheme {
        MuseumHallContent(
            exhibits = listOf(previewPen),
            onOpenExhibit = {}
        )
    }
}

@Preview(name = "Museum hall - landscape", widthDp = 915, heightDp = 412)
@Composable
private fun MuseumHallLandscapePreview() {
    MuseumGameTheme {
        MuseumHallContent(
            exhibits = listOf(previewPen),
            onOpenExhibit = {}
        )
    }
}

private val WIDE_LAYOUT_MIN_WIDTH = 600.dp
