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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.museumgame.R
import com.example.museumgame.game.ExhibitVisitStatus
import com.example.museumgame.model.Exhibit
import com.example.museumgame.model.ExhibitCatalog
import com.example.museumgame.model.ExhibitIds
import com.example.museumgame.ui.theme.MuseumGameTheme

@Composable
fun MuseumEntranceContent(
    exhibits: List<Exhibit>,
    visitStatuses: List<ExhibitVisitStatus>,
    onResumeVisit: () -> Unit,
    onOpenExhibit: (String) -> Unit,
    onRestartMuseum: () -> Unit,
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
                EntranceIllustration(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                )
                EntranceActions(
                    exhibits = exhibits,
                    visitStatuses = visitStatuses,
                    onResumeVisit = onResumeVisit,
                    onOpenExhibit = onOpenExhibit,
                    onRestartMuseum = onRestartMuseum,
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
                Text(
                    stringResource(R.string.museum_entrance_title),
                    modifier = Modifier.semantics { heading() }
                )
                EntranceIllustration(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(maxHeight = 420.dp)
                )
                EntranceVisitActions(
                    exhibits = exhibits,
                    visitStatuses = visitStatuses,
                    onResumeVisit = onResumeVisit,
                    onOpenExhibit = onOpenExhibit,
                    onRestartMuseum = onRestartMuseum
                )
            }
        }
    }
}

@Composable
private fun EntranceIllustration(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.museum_hall),
        contentDescription = stringResource(R.string.museum_entrance_description),
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun EntranceActions(
    exhibits: List<Exhibit>,
    visitStatuses: List<ExhibitVisitStatus>,
    onResumeVisit: () -> Unit,
    onOpenExhibit: (String) -> Unit,
    onRestartMuseum: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            stringResource(R.string.museum_entrance_title),
            modifier = Modifier.semantics { heading() }
        )
        EntranceVisitActions(
            exhibits = exhibits,
            visitStatuses = visitStatuses,
            onResumeVisit = onResumeVisit,
            onOpenExhibit = onOpenExhibit,
            onRestartMuseum = onRestartMuseum
        )
    }
}

@Composable
private fun EntranceVisitActions(
    exhibits: List<Exhibit>,
    visitStatuses: List<ExhibitVisitStatus>,
    onResumeVisit: () -> Unit,
    onOpenExhibit: (String) -> Unit,
    onRestartMuseum: () -> Unit
) {
    val exhibitsById = exhibits.associateBy(Exhibit::id)
    val currentStatus = visitStatuses.firstOrNull(ExhibitVisitStatus::current)

    if (currentStatus != null) {
        Button(onClick = onResumeVisit) {
            Text(stringResource(R.string.resume_visit))
        }
    } else {
        Text(stringResource(R.string.museum_visit_complete))
    }

    visitStatuses.forEach { status ->
        val exhibit = requireNotNull(exhibitsById[status.exhibitId])
        val exhibitName = stringResource(exhibitNameResource(exhibit.id))
        Button(
            enabled = status.completed || status.unlocked,
            onClick = { onOpenExhibit(status.exhibitId) }
        ) {
            Text(
                when {
                    status.completed -> stringResource(R.string.revisit_exhibit, exhibitName)
                    status.current -> stringResource(R.string.current_exhibit, exhibitName)
                    else -> stringResource(R.string.locked_exhibit, exhibitName)
                }
            )
        }
    }

    Button(onClick = onRestartMuseum) {
        Text(stringResource(R.string.restart_museum))
    }
}

private val previewVisitStatuses = listOf(
    ExhibitVisitStatus(
        exhibitId = ExhibitIds.REAPPEARING_PEN,
        completed = false,
        unlocked = true,
        current = true
    ),
    ExhibitVisitStatus(
        exhibitId = ExhibitIds.SLIGHTLY_WRONG,
        completed = false,
        unlocked = false,
        current = false
    )
)

@Preview(name = "Museum entrance - portrait", widthDp = 412, heightDp = 915)
@Composable
private fun MuseumEntrancePortraitPreview() {
    MuseumGameTheme {
        MuseumEntranceContent(
            exhibits = ExhibitCatalog.orderedExhibits,
            visitStatuses = previewVisitStatuses,
            onResumeVisit = {},
            onOpenExhibit = {},
            onRestartMuseum = {}
        )
    }
}

@Preview(name = "Museum entrance - landscape", widthDp = 915, heightDp = 412)
@Composable
private fun MuseumEntranceLandscapePreview() {
    MuseumGameTheme {
        MuseumEntranceContent(
            exhibits = ExhibitCatalog.orderedExhibits,
            visitStatuses = previewVisitStatuses,
            onResumeVisit = {},
            onOpenExhibit = {},
            onRestartMuseum = {}
        )
    }
}

private val WIDE_LAYOUT_MIN_WIDTH = 600.dp
