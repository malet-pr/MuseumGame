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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.museumgame.R
import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.ProgressCategory
import com.example.museumgame.game.SimulatedProgressFeedback
import com.example.museumgame.game.SimulatedProgressSignal
import com.example.museumgame.game.SimulatedProgressState
import com.example.museumgame.model.ExhibitIds
import com.example.museumgame.ui.theme.MuseumGameTheme

@Composable
fun SimulatedProgressContent(
    progress: ExhibitProgress,
    puzzleState: SimulatedProgressState,
    feedback: SimulatedProgressFeedback?,
    onClassify: (ProgressCategory) -> Unit,
    onRestart: () -> Unit,
    onRestartMuseum: () -> Unit,
    onContinue: () -> Unit,
    isFinalExhibit: Boolean,
    onReturnToEntrance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resources = exhibitUiResources(ExhibitIds.SIMULATED_PROGRESS)
    ResponsiveExhibitLayout(
        titleResource = resources.nameResource,
        illustrationResource = resources.illustrationResource,
        illustrationDescriptionResource = resources.illustrationDescriptionResource,
        modifier = modifier
    ) {
        Text(stringResource(R.string.simulated_progress_instructions))
        Text(stringResource(R.string.simulated_progress_category_guide))
        Text(
            text = if (puzzleState.solved) {
                stringResource(R.string.simulated_progress_all_classified)
            } else {
                stringResource(
                    simulatedProgressSignalResource(
                        requireNotNull(puzzleState.currentSignal)
                    )
                )
            },
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() }
        )
        Text(
            stringResource(simulatedProgressFeedbackResource(feedback)),
            modifier = Modifier.semantics {
                liveRegion = LiveRegionMode.Polite
            }
        )
        Text(
            stringResource(
                R.string.simulated_progress_classified,
                puzzleState.classifiedSignals.size,
                SimulatedProgressSignal.entries.size
            )
        )
        Text(stringResource(R.string.simulated_progress_choices, progress.attempts))
        SimulatedProgressCategoryControls(
            enabled = !puzzleState.solved,
            onClassify = onClassify
        )
        if (puzzleState.solved) {
            Text(stringResource(R.string.simulated_progress_solved, progress.attempts))
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
private fun SimulatedProgressCategoryControls(
    enabled: Boolean,
    onClassify: (ProgressCategory) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProgressCategory.entries.forEach { category ->
            Button(
                enabled = enabled,
                onClick = { onClassify(category) },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(simulatedProgressCategoryResource(category)))
            }
        }
    }
}

@StringRes
private fun simulatedProgressSignalResource(
    signal: SimulatedProgressSignal
): Int = when (signal) {
    SimulatedProgressSignal.ALIGNMENT_MEETINGS ->
        R.string.simulated_progress_signal_alignment_meetings

    SimulatedProgressSignal.WORKFLOW_GUIDE ->
        R.string.simulated_progress_signal_workflow_guide

    SimulatedProgressSignal.FASTER_SETUP ->
        R.string.simulated_progress_signal_faster_setup

    SimulatedProgressSignal.CODE_REVIEW_TIME ->
        R.string.simulated_progress_signal_code_review_time

    SimulatedProgressSignal.AUTOMATION_SCRIPTS ->
        R.string.simulated_progress_signal_automation_scripts

    SimulatedProgressSignal.FEWER_SUPPORT_REQUESTS ->
        R.string.simulated_progress_signal_fewer_support_requests
}

@StringRes
private fun simulatedProgressCategoryResource(category: ProgressCategory): Int =
    when (category) {
        ProgressCategory.ACTIVITY -> R.string.simulated_progress_category_activity
        ProgressCategory.OUTPUT -> R.string.simulated_progress_category_output
        ProgressCategory.IMPACT -> R.string.simulated_progress_category_impact
    }

@StringRes
private fun simulatedProgressFeedbackResource(
    feedback: SimulatedProgressFeedback?
): Int = when (feedback) {
    null -> R.string.simulated_progress_feedback_initial
    SimulatedProgressFeedback.LOCKED -> R.string.exhibit_locked
    SimulatedProgressFeedback.CORRECT_ACTIVITY ->
        R.string.simulated_progress_feedback_correct_activity

    SimulatedProgressFeedback.CORRECT_OUTPUT ->
        R.string.simulated_progress_feedback_correct_output

    SimulatedProgressFeedback.CORRECT_IMPACT ->
        R.string.simulated_progress_feedback_correct_impact

    SimulatedProgressFeedback.INCORRECT_ACTIVITY ->
        R.string.simulated_progress_feedback_incorrect_activity

    SimulatedProgressFeedback.INCORRECT_OUTPUT ->
        R.string.simulated_progress_feedback_incorrect_output

    SimulatedProgressFeedback.INCORRECT_IMPACT ->
        R.string.simulated_progress_feedback_incorrect_impact

    SimulatedProgressFeedback.PUZZLE_SOLVED ->
        R.string.simulated_progress_feedback_solved

    SimulatedProgressFeedback.ALREADY_SOLVED -> R.string.exhibit_already_solved
}

@Preview(name = "Simulated Progress - portrait", widthDp = 412, heightDp = 915)
@Composable
private fun SimulatedProgressPortraitPreview() {
    MuseumGameTheme {
        SimulatedProgressContent(
            progress = ExhibitProgress(attempts = 2),
            puzzleState = SimulatedProgressState(
                classifiedSignals = SimulatedProgressSignal.entries.take(2)
            ),
            feedback = SimulatedProgressFeedback.CORRECT_OUTPUT,
            onClassify = {},
            onRestart = {},
            onRestartMuseum = {},
            onContinue = {},
            isFinalExhibit = true,
            onReturnToEntrance = {}
        )
    }
}

@Preview(name = "Simulated Progress - landscape", widthDp = 915, heightDp = 412)
@Composable
private fun SimulatedProgressLandscapePreview() {
    MuseumGameTheme {
        SimulatedProgressContent(
            progress = ExhibitProgress(attempts = 4),
            puzzleState = SimulatedProgressState(
                classifiedSignals = SimulatedProgressSignal.entries.take(4)
            ),
            feedback = SimulatedProgressFeedback.CORRECT_ACTIVITY,
            onClassify = {},
            onRestart = {},
            onRestartMuseum = {},
            onContinue = {},
            isFinalExhibit = true,
            onReturnToEntrance = {}
        )
    }
}
