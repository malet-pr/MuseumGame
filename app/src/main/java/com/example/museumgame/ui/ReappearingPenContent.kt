package com.example.museumgame.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.museumgame.R
import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.PenInspectionFeedback
import com.example.museumgame.game.PenLocation
import com.example.museumgame.game.ReappearingPenState
import com.example.museumgame.ui.theme.MuseumGameTheme

@Composable
fun ReappearingPenContent(
    progress: ExhibitProgress,
    puzzleState: ReappearingPenState,
    feedback: PenInspectionFeedback?,
    onInspectLocation: (PenLocation) -> Unit,
    onRestart: () -> Unit,
    onReturnToHall: () -> Unit,
    modifier: Modifier = Modifier
) {
    ResponsiveExhibitLayout(
        titleResource = R.string.reappearing_pen_name,
        illustrationResource = R.drawable.pen_reappears,
        illustrationDescriptionResource = R.string.reappearing_pen_image_description,
        modifier = modifier
    ) {
        Text(
            stringResource(penFeedbackResource(feedback)),
            modifier = Modifier.semantics {
                liveRegion = LiveRegionMode.Polite
            }
        )
        Text(stringResource(R.string.inspections, progress.attempts))
        PenInspectionGrid(
            enabled = !progress.solved,
            inspectedLocations = puzzleState.inspectedLocations,
            onInspectLocation = onInspectLocation
        )
        if (puzzleState.penLocation != null && !progress.solved) {
            Text(stringResource(R.string.pen_reappeared_hint))
        }
        if (progress.solved) {
            Text(stringResource(R.string.exhibit_solved, progress.attempts))
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
                val inspected = location in inspectedLocations
                val inspectionState = stringResource(
                    if (inspected) {
                        R.string.inspection_state_checked
                    } else {
                        R.string.inspection_state_not_checked
                    }
                )
                Button(
                    enabled = enabled,
                    onClick = { onInspectLocation(location) },
                    modifier = Modifier
                        .weight(1f)
                        .semantics {
                            stateDescription = inspectionState
                        }
                ) {
                    val label = stringResource(penLocationResource(location))
                    Text(
                        if (inspected) {
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
    PenInspectionFeedback.ALREADY_SOLVED -> R.string.exhibit_already_solved
}

@Preview(name = "Reappearing Pen - portrait", widthDp = 412, heightDp = 915)
@Composable
private fun ReappearingPenPortraitPreview() {
    MuseumGameTheme {
        ReappearingPenContent(
            progress = ExhibitProgress(attempts = 2),
            puzzleState = penPreviewState,
            feedback = PenInspectionFeedback.PEN_REAPPEARED,
            onInspectLocation = {},
            onRestart = {},
            onReturnToHall = {}
        )
    }
}

@Preview(name = "Reappearing Pen - landscape", widthDp = 915, heightDp = 412)
@Composable
private fun ReappearingPenLandscapePreview() {
    MuseumGameTheme {
        ReappearingPenContent(
            progress = ExhibitProgress(attempts = 2),
            puzzleState = penPreviewState,
            feedback = PenInspectionFeedback.PEN_REAPPEARED,
            onInspectLocation = {},
            onRestart = {},
            onReturnToHall = {}
        )
    }
}

private val penPreviewState = ReappearingPenState(
    inspectedLocations = setOf(PenLocation.PAPERS, PenLocation.EMPTY_DESK),
    targetLocation = PenLocation.PAPERS,
    penLocation = PenLocation.PAPERS
)
