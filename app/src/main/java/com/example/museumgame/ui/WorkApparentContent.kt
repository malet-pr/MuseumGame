package com.example.museumgame.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.museumgame.R
import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.WorkApparentFeedback
import com.example.museumgame.game.WorkApparentInterruption
import com.example.museumgame.game.WorkApparentStage
import com.example.museumgame.game.WorkApparentState
import com.example.museumgame.model.ExhibitIds
import com.example.museumgame.ui.theme.MuseumGameTheme

@Composable
fun WorkApparentContent(
    progress: ExhibitProgress,
    puzzleState: WorkApparentState,
    feedback: WorkApparentFeedback?,
    onTrace: (WorkApparentStage) -> Unit,
    onInterrupt: (WorkApparentInterruption) -> Unit,
    onRestart: () -> Unit,
    onRestartMuseum: () -> Unit,
    onContinue: () -> Unit,
    isFinalExhibit: Boolean,
    onReturnToEntrance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resources = exhibitUiResources(ExhibitIds.WORK_APPARENT)
    ResponsiveExhibitLayout(
        titleResource = resources.nameResource,
        illustrationResource = resources.illustrationResource,
        illustrationDescriptionResource = resources.illustrationDescriptionResource,
        modifier = modifier
    ) {
        Text(
            stringResource(
                if (puzzleState.loopTraced) {
                    R.string.work_apparent_interrupt_instructions
                } else {
                    R.string.work_apparent_trace_instructions
                }
            )
        )
        MuseumFeedbackMessage(
            text = stringResource(workApparentFeedbackResource(feedback)),
            solved = puzzleState.solved,
            modifier = Modifier.semantics {
                liveRegion = LiveRegionMode.Polite
            }
        )
        MuseumSecondaryText(
            stringResource(R.string.work_apparent_choices, progress.attempts)
        )

        if (puzzleState.loopTraced) {
            WorkApparentInterruptionControls(
                enabled = !puzzleState.solved,
                onInterrupt = onInterrupt
            )
        } else {
            WorkApparentTraceControls(
                puzzleState = puzzleState,
                onTrace = onTrace
            )
        }

        if (puzzleState.solved) {
            MuseumSolvedSummary(
                text = pluralStringResource(
                    R.plurals.work_apparent_solved,
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
private fun WorkApparentTraceControls(
    puzzleState: WorkApparentState,
    onTrace: (WorkApparentStage) -> Unit
) {
    WorkApparentStage.entries.chunked(2).forEach { rowStages ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rowStages.forEach { stage ->
                val traced = stage in puzzleState.tracedStages
                val stateDescription = stringResource(
                    when {
                        traced -> R.string.work_apparent_stage_traced
                        stage == puzzleState.nextStage -> R.string.work_apparent_stage_next
                        else -> R.string.work_apparent_stage_pending
                    }
                )
                Button(
                    enabled = !traced,
                    onClick = { onTrace(stage) },
                    modifier = Modifier
                        .weight(1f)
                        .semantics {
                            this.stateDescription = stateDescription
                        }
                ) {
                    Text(stringResource(workApparentStageResource(stage)))
                }
            }
            if (rowStages.size == 1) {
                androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun WorkApparentInterruptionControls(
    enabled: Boolean,
    onInterrupt: (WorkApparentInterruption) -> Unit
) {
    WorkApparentInterruption.entries.forEach { interruption ->
        Button(
            enabled = enabled,
            onClick = { onInterrupt(interruption) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(workApparentInterruptionResource(interruption)))
        }
    }
}

@StringRes
private fun workApparentStageResource(stage: WorkApparentStage): Int = when (stage) {
    WorkApparentStage.TASKS_RECEIVED -> R.string.work_apparent_stage_tasks_received
    WorkApparentStage.TASKS_ORGANIZED -> R.string.work_apparent_stage_tasks_organized
    WorkApparentStage.PLAN_AND_REVIEW -> R.string.work_apparent_stage_plan_review
    WorkApparentStage.TASKS_REARRANGED -> R.string.work_apparent_stage_tasks_rearranged
    WorkApparentStage.RETURN_TO_INBOX -> R.string.work_apparent_stage_return_inbox
}

@StringRes
private fun workApparentInterruptionResource(
    interruption: WorkApparentInterruption
): Int = when (interruption) {
    WorkApparentInterruption.REORGANIZE_TASKS ->
        R.string.work_apparent_interruption_reorganize

    WorkApparentInterruption.UPDATE_EFFORT_METRICS ->
        R.string.work_apparent_interruption_update_metrics

    WorkApparentInterruption.COMPLETE_ONE_TASK ->
        R.string.work_apparent_interruption_complete_task
}

@StringRes
private fun workApparentFeedbackResource(feedback: WorkApparentFeedback?): Int =
    when (feedback) {
        null -> R.string.work_apparent_feedback_initial
        WorkApparentFeedback.LOCKED -> R.string.exhibit_locked
        WorkApparentFeedback.TRACE_ADVANCED -> R.string.work_apparent_feedback_trace_advanced
        WorkApparentFeedback.WRONG_NEXT_STAGE -> R.string.work_apparent_feedback_wrong_stage
        WorkApparentFeedback.LOOP_TRACED -> R.string.work_apparent_feedback_loop_traced
        WorkApparentFeedback.TRACE_ALREADY_COMPLETE ->
            R.string.work_apparent_feedback_trace_complete

        WorkApparentFeedback.INTERRUPT_TOO_EARLY ->
            R.string.work_apparent_feedback_interrupt_early

        WorkApparentFeedback.LOOP_CONTINUES -> R.string.work_apparent_feedback_loop_continues
        WorkApparentFeedback.PUZZLE_SOLVED -> R.string.work_apparent_feedback_solved
        WorkApparentFeedback.ALREADY_SOLVED -> R.string.exhibit_already_solved
    }

@Preview(name = "Work Apparent - portrait", widthDp = 412, heightDp = 915)
@Composable
private fun WorkApparentPortraitPreview() {
    MuseumGameTheme {
        WorkApparentContent(
            progress = ExhibitProgress(attempts = 2),
            puzzleState = WorkApparentState(
                tracedStages = WorkApparentStage.entries.take(2)
            ),
            feedback = WorkApparentFeedback.TRACE_ADVANCED,
            onTrace = {},
            onInterrupt = {},
            onRestart = {},
            onRestartMuseum = {},
            onContinue = {},
            isFinalExhibit = true,
            onReturnToEntrance = {}
        )
    }
}

@Preview(name = "Work Apparent - landscape", widthDp = 915, heightDp = 412)
@Composable
private fun WorkApparentLandscapePreview() {
    MuseumGameTheme {
        WorkApparentContent(
            progress = ExhibitProgress(attempts = 6),
            puzzleState = WorkApparentState(
                tracedStages = WorkApparentStage.entries
            ),
            feedback = WorkApparentFeedback.LOOP_TRACED,
            onTrace = {},
            onInterrupt = {},
            onRestart = {},
            onRestartMuseum = {},
            onContinue = {},
            isFinalExhibit = true,
            onReturnToEntrance = {}
        )
    }
}
