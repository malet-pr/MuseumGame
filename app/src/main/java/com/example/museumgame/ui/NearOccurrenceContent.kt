package com.example.museumgame.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.museumgame.R
import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.NearOccurrenceFeedback
import com.example.museumgame.game.NearOccurrenceStage
import com.example.museumgame.game.NearOccurrenceState
import com.example.museumgame.model.ExhibitIds
import com.example.museumgame.ui.theme.MuseumGameTheme

@Composable
fun NearOccurrenceContent(
    progress: ExhibitProgress,
    puzzleState: NearOccurrenceState,
    feedback: NearOccurrenceFeedback?,
    onAdvance: () -> Unit,
    onPreserve: () -> Unit,
    onRestart: () -> Unit,
    onRestartMuseum: () -> Unit,
    onContinue: () -> Unit,
    isFinalExhibit: Boolean,
    onReturnToEntrance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resources = exhibitUiResources(ExhibitIds.NEAR_OCCURRENCE)
    ResponsiveExhibitLayout(
        titleResource = resources.nameResource,
        illustrationResource = resources.illustrationResource,
        illustrationDescriptionResource = resources.illustrationDescriptionResource,
        modifier = modifier
    ) {
        Text(stringResource(R.string.near_occurrence_instructions))
        Text(
            text = stringResource(
                R.string.near_occurrence_stage,
                stringResource(nearOccurrenceStageResource(puzzleState.stage))
            ),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() }
        )
        Text(
            stringResource(nearOccurrenceFeedbackResource(feedback)),
            modifier = Modifier.semantics {
                liveRegion = LiveRegionMode.Polite
            }
        )
        Text(stringResource(R.string.near_occurrence_choices, progress.attempts))
        NearOccurrenceControls(
            enabled = !puzzleState.solved,
            onAdvance = onAdvance,
            onPreserve = onPreserve
        )
        if (puzzleState.solved) {
            Text(
                pluralStringResource(
                    R.plurals.near_occurrence_solved,
                    progress.attempts,
                    progress.attempts
                )
            )
        }
        ExhibitNavigationActions(
            solved = puzzleState.solved,
            isFinalExhibit = isFinalExhibit,
            onContinue = onContinue,
            onRestartExhibit = onRestart,
            onRestartMuseum = onRestartMuseum,
            onReturnToEntrance = onReturnToEntrance
        )
    }
}

@Composable
private fun NearOccurrenceControls(
    enabled: Boolean,
    onAdvance: () -> Unit,
    onPreserve: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            enabled = enabled,
            onClick = onAdvance,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.near_occurrence_advance))
        }
        Button(
            enabled = enabled,
            onClick = onPreserve,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.near_occurrence_preserve))
        }
    }
}

@StringRes
private fun nearOccurrenceStageResource(stage: NearOccurrenceStage): Int =
    when (stage) {
        NearOccurrenceStage.SETTLED -> R.string.near_occurrence_stage_settled
        NearOccurrenceStage.SHIFTING -> R.string.near_occurrence_stage_shifting
        NearOccurrenceStage.AT_THRESHOLD -> R.string.near_occurrence_stage_threshold
    }

@StringRes
private fun nearOccurrenceFeedbackResource(
    feedback: NearOccurrenceFeedback?
): Int = when (feedback) {
    null -> R.string.near_occurrence_feedback_initial
    NearOccurrenceFeedback.LOCKED -> R.string.exhibit_locked
    NearOccurrenceFeedback.SHIFTING -> R.string.near_occurrence_feedback_shifting
    NearOccurrenceFeedback.AT_THRESHOLD -> R.string.near_occurrence_feedback_threshold
    NearOccurrenceFeedback.TOO_SOON -> R.string.near_occurrence_feedback_too_soon
    NearOccurrenceFeedback.SPILL_RESET -> R.string.near_occurrence_feedback_spill_reset
    NearOccurrenceFeedback.PUZZLE_SOLVED -> R.string.near_occurrence_feedback_solved
    NearOccurrenceFeedback.ALREADY_SOLVED -> R.string.exhibit_already_solved
}

@Preview(name = "Near Occurrence - portrait", widthDp = 412, heightDp = 915)
@Composable
private fun NearOccurrencePortraitPreview() {
    MuseumGameTheme {
        NearOccurrenceContent(
            progress = ExhibitProgress(attempts = 1),
            puzzleState = NearOccurrenceState(stage = NearOccurrenceStage.SHIFTING),
            feedback = NearOccurrenceFeedback.SHIFTING,
            onAdvance = {},
            onPreserve = {},
            onRestart = {},
            onRestartMuseum = {},
            onContinue = {},
            isFinalExhibit = true,
            onReturnToEntrance = {}
        )
    }
}

@Preview(name = "Near Occurrence - landscape", widthDp = 915, heightDp = 412)
@Composable
private fun NearOccurrenceLandscapePreview() {
    MuseumGameTheme {
        NearOccurrenceContent(
            progress = ExhibitProgress(attempts = 2),
            puzzleState = NearOccurrenceState(stage = NearOccurrenceStage.AT_THRESHOLD),
            feedback = NearOccurrenceFeedback.AT_THRESHOLD,
            onAdvance = {},
            onPreserve = {},
            onRestart = {},
            onRestartMuseum = {},
            onContinue = {},
            isFinalExhibit = true,
            onReturnToEntrance = {}
        )
    }
}
