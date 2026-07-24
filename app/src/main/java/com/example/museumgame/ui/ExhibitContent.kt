package com.example.museumgame.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import com.example.museumgame.R
import com.example.museumgame.game.PenInspectionFeedback
import com.example.museumgame.game.PenLocation
import com.example.museumgame.game.ReappearingPenState
import com.example.museumgame.model.Exhibit
import com.example.museumgame.viewmodel.MuseumGameViewModel

@Composable
fun ExhibitContent(
    exhibit: Exhibit,
    attempts: Int,
    solved: Boolean,
    penState: ReappearingPenState,
    penFeedback: PenInspectionFeedback?,
    onInspectLocation: (PenLocation) -> Unit,
    onRestart: () -> Unit,
    onReturnToHall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(exhibitNameResource(exhibit.id)))
        Image(
            painter = painterResource(exhibitImageResource(exhibit.id)),
            contentDescription = stringResource(R.string.reappearing_pen_image_description),
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(maxHeight = 420.dp),
            contentScale = ContentScale.Fit
        )
        Text(stringResource(penFeedbackResource(penFeedback)))
        Text(stringResource(R.string.inspections, attempts))

        PenInspectionGrid(
            enabled = !solved,
            inspectedLocations = penState.inspectedLocations,
            onInspectLocation = onInspectLocation
        )

        if (penState.penLocation != null && !solved) {
            Text(stringResource(R.string.pen_reappeared_hint))
        }

        if (solved) {
            Text(stringResource(R.string.room_solved, attempts))
        }

        Button(onClick = onRestart) {
            Text(stringResource(R.string.restart))
        }
        Button(onClick = onReturnToHall) {
            Text(stringResource(R.string.return_to_hall))
        }
    }
}

@Composable
private fun PenInspectionGrid(
    enabled: Boolean,
    inspectedLocations: Set<PenLocation>,
    onInspectLocation: (PenLocation) -> Unit
) {
    PenLocation.entries.chunked(2).forEach { rowLocations ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rowLocations.forEach { location ->
                Button(
                    enabled = enabled,
                    onClick = { onInspectLocation(location) },
                    modifier = Modifier.weight(1f)
                ) {
                    val label = stringResource(penLocationResource(location))
                    Text(
                        if (location in inspectedLocations) {
                            stringResource(R.string.inspected_location, label)
                        } else {
                            label
                        }
                    )
                }
            }
        }
    }
}

@DrawableRes
private fun exhibitImageResource(exhibitId: String): Int = when (exhibitId) {
    MuseumGameViewModel.REAPPEARING_PEN_ID -> R.drawable.pen_reappears
    else -> error("No image resource mapped for exhibit ID: $exhibitId")
}

@StringRes
internal fun exhibitNameResource(exhibitId: String): Int = when (exhibitId) {
    MuseumGameViewModel.REAPPEARING_PEN_ID -> R.string.reappearing_pen_name
    else -> error("No name resource mapped for exhibit ID: $exhibitId")
}

@StringRes
private fun penLocationResource(location: PenLocation): Int = when (location) {
    PenLocation.PAPERS -> R.string.pen_location_papers
    PenLocation.FADED_OUTLINE -> R.string.pen_location_faded_outline
    PenLocation.EMPTY_DESK -> R.string.pen_location_empty_desk
    PenLocation.FILING_CABINET -> R.string.pen_location_filing_cabinet
}

@StringRes
private fun penFeedbackResource(feedback: PenInspectionFeedback?): Int = when (feedback) {
    null -> R.string.reappearing_pen_instructions
    PenInspectionFeedback.FIRST_LOCATION_EMPTY -> R.string.pen_feedback_first_empty
    PenInspectionFeedback.SAME_LOCATION_STILL_EMPTY -> R.string.pen_feedback_same_empty
    PenInspectionFeedback.PEN_REAPPEARED -> R.string.pen_feedback_reappeared
    PenInspectionFeedback.LOCATION_EMPTY -> R.string.pen_feedback_location_empty
    PenInspectionFeedback.PEN_FOUND -> R.string.pen_feedback_found
    PenInspectionFeedback.ALREADY_SOLVED -> R.string.room_already_solved
}
