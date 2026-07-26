package com.example.museumgame.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.museumgame.R
import com.example.museumgame.game.ChaosPiece
import com.example.museumgame.game.CreativeChaosFeedback
import com.example.museumgame.game.CreativeChaosState
import com.example.museumgame.game.CreativeChaosStep
import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.model.ExhibitIds
import com.example.museumgame.ui.theme.MuseumGameTheme

@Composable
fun CreativeChaosContent(
    progress: ExhibitProgress,
    puzzleState: CreativeChaosState,
    feedback: CreativeChaosFeedback?,
    onTogglePiece: (ChaosPiece) -> Unit,
    onCombine: () -> Unit,
    onRestart: () -> Unit,
    onRestartMuseum: () -> Unit,
    onContinue: () -> Unit,
    isFinalExhibit: Boolean,
    onReturnToEntrance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resources = exhibitUiResources(ExhibitIds.CREATIVE_CHAOS)
    ResponsiveExhibitLayout(
        titleResource = resources.nameResource,
        illustrationResource = resources.illustrationResource,
        illustrationDescriptionResource = resources.illustrationDescriptionResource,
        modifier = modifier
    ) {
        Text(stringResource(R.string.creative_chaos_instructions))
        Text(
            text = stringResource(creativeChaosStepResource(puzzleState.step)),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() }
        )
        Text(stringResource(creativeChaosPromptResource(puzzleState.step)))
        Text(stringResource(creativeChaosGeneratedResource(puzzleState.step)))
        MuseumFeedbackMessage(
            text = stringResource(creativeChaosFeedbackResource(feedback)),
            solved = puzzleState.solved,
            modifier = Modifier.semantics {
                liveRegion = LiveRegionMode.Polite
            }
        )
        MuseumSecondaryText(
            text = stringResource(
                R.string.creative_chaos_combinations,
                progress.attempts
            )
        )
        CreativeChaosPieceControls(
            puzzleState = puzzleState,
            onTogglePiece = onTogglePiece
        )
        Button(
            enabled = !puzzleState.solved,
            onClick = onCombine,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.creative_chaos_combine))
        }
        if (puzzleState.solved) {
            MuseumSolvedSummary(
                text = pluralStringResource(
                    R.plurals.creative_chaos_solved,
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
private fun CreativeChaosPieceControls(
    puzzleState: CreativeChaosState,
    onTogglePiece: (ChaosPiece) -> Unit
) {
    val visiblePieces = ChaosPiece.entries.filter { piece ->
        piece in puzzleState.availablePieces ||
            piece in puzzleState.generatedPieces ||
            piece in puzzleState.consumedPieces
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        visiblePieces.chunked(2).forEach { rowPieces ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowPieces.forEach { piece ->
                    CreativeChaosPieceButton(
                        piece = piece,
                        puzzleState = puzzleState,
                        onTogglePiece = onTogglePiece,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowPieces.size == 1) {
                    androidx.compose.foundation.layout.Spacer(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CreativeChaosPieceButton(
    piece: ChaosPiece,
    puzzleState: CreativeChaosState,
    onTogglePiece: (ChaosPiece) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = piece in puzzleState.selectedPieces
    val statusResource = when {
        isSelected -> R.string.creative_chaos_piece_selected
        piece in puzzleState.consumedPieces -> R.string.creative_chaos_piece_consumed
        piece in puzzleState.generatedPieces -> R.string.creative_chaos_piece_generated
        else -> R.string.creative_chaos_piece_available
    }
    val status = stringResource(statusResource)

    Button(
        enabled = !puzzleState.solved && piece in puzzleState.availablePieces,
        onClick = { onTogglePiece(piece) },
        modifier = modifier.semantics {
            selected = isSelected
            stateDescription = status
        }
    ) {
        Text(
            stringResource(
                R.string.exhibit_piece_with_status,
                stringResource(creativeChaosPieceResource(piece)),
                status
            )
        )
    }
}

@StringRes
private fun creativeChaosStepResource(step: CreativeChaosStep): Int =
    when (step) {
        CreativeChaosStep.FORM_PATTERN -> R.string.creative_chaos_step_pattern
        CreativeChaosStep.ADD_MOTION -> R.string.creative_chaos_step_motion
        CreativeChaosStep.ADD_MEANING -> R.string.creative_chaos_step_meaning
        CreativeChaosStep.COMPLETE -> R.string.creative_chaos_step_complete
    }

@StringRes
private fun creativeChaosPromptResource(step: CreativeChaosStep): Int =
    when (step) {
        CreativeChaosStep.FORM_PATTERN -> R.string.creative_chaos_prompt_pattern
        CreativeChaosStep.ADD_MOTION -> R.string.creative_chaos_prompt_motion
        CreativeChaosStep.ADD_MEANING -> R.string.creative_chaos_prompt_meaning
        CreativeChaosStep.COMPLETE -> R.string.creative_chaos_prompt_complete
    }

@StringRes
private fun creativeChaosGeneratedResource(step: CreativeChaosStep): Int =
    when (step) {
        CreativeChaosStep.FORM_PATTERN -> R.string.creative_chaos_generated_none
        CreativeChaosStep.ADD_MOTION -> R.string.creative_chaos_generated_pattern
        CreativeChaosStep.ADD_MEANING -> R.string.creative_chaos_generated_motion
        CreativeChaosStep.COMPLETE -> R.string.creative_chaos_generated_meaning
    }

@StringRes
private fun creativeChaosPieceResource(piece: ChaosPiece): Int = when (piece) {
    ChaosPiece.GRID -> R.string.creative_chaos_piece_grid
    ChaosPiece.SKETCH -> R.string.creative_chaos_piece_sketch
    ChaosPiece.CODE -> R.string.creative_chaos_piece_code
    ChaosPiece.NOTE -> R.string.creative_chaos_piece_note
    ChaosPiece.PATTERN -> R.string.creative_chaos_piece_pattern
    ChaosPiece.MOTION -> R.string.creative_chaos_piece_motion
}

@StringRes
private fun creativeChaosFeedbackResource(
    feedback: CreativeChaosFeedback?
): Int = when (feedback) {
    null -> R.string.creative_chaos_feedback_initial
    CreativeChaosFeedback.LOCKED -> R.string.exhibit_locked
    CreativeChaosFeedback.TOO_MANY_SELECTED ->
        R.string.creative_chaos_feedback_too_many

    CreativeChaosFeedback.INCOMPLETE_SELECTION ->
        R.string.creative_chaos_feedback_incomplete

    CreativeChaosFeedback.WRONG_PAIR -> R.string.creative_chaos_feedback_wrong
    CreativeChaosFeedback.PATTERN_CREATED -> R.string.creative_chaos_feedback_pattern
    CreativeChaosFeedback.MOTION_CREATED -> R.string.creative_chaos_feedback_motion
    CreativeChaosFeedback.PUZZLE_SOLVED -> R.string.creative_chaos_feedback_solved
    CreativeChaosFeedback.ALREADY_SOLVED -> R.string.exhibit_already_solved
}

@Preview(name = "Creative Chaos - portrait", widthDp = 412, heightDp = 915)
@Composable
private fun CreativeChaosPortraitPreview() {
    MuseumGameTheme {
        CreativeChaosContent(
            progress = ExhibitProgress(attempts = 1),
            puzzleState = CreativeChaosState(step = CreativeChaosStep.ADD_MOTION),
            feedback = CreativeChaosFeedback.PATTERN_CREATED,
            onTogglePiece = {},
            onCombine = {},
            onRestart = {},
            onRestartMuseum = {},
            onContinue = {},
            isFinalExhibit = true,
            onReturnToEntrance = {}
        )
    }
}

@Preview(name = "Creative Chaos - landscape", widthDp = 915, heightDp = 412)
@Composable
private fun CreativeChaosLandscapePreview() {
    MuseumGameTheme {
        CreativeChaosContent(
            progress = ExhibitProgress(attempts = 2),
            puzzleState = CreativeChaosState(step = CreativeChaosStep.ADD_MEANING),
            feedback = CreativeChaosFeedback.MOTION_CREATED,
            onTogglePiece = {},
            onCombine = {},
            onRestart = {},
            onRestartMuseum = {},
            onContinue = {},
            isFinalExhibit = true,
            onReturnToEntrance = {}
        )
    }
}
