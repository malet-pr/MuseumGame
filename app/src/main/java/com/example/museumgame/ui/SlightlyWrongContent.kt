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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.museumgame.R
import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.SlightlyWrongClue
import com.example.museumgame.game.SlightlyWrongDetail
import com.example.museumgame.game.SlightlyWrongFeedback
import com.example.museumgame.game.SlightlyWrongState
import com.example.museumgame.ui.theme.MuseumGameTheme

@Composable
fun SlightlyWrongContent(
    progress: ExhibitProgress,
    puzzleState: SlightlyWrongState,
    feedback: SlightlyWrongFeedback?,
    onAnswer: (SlightlyWrongDetail) -> Unit,
    onRestart: () -> Unit,
    onRestartMuseum: () -> Unit,
    onContinue: () -> Unit,
    isFinalExhibit: Boolean,
    onReturnToEntrance: () -> Unit,
    modifier: Modifier = Modifier
) {
    ResponsiveExhibitLayout(
        titleResource = R.string.slightly_wrong_name,
        illustrationResource = R.drawable.slightly_wrong,
        illustrationDescriptionResource = R.string.slightly_wrong_image_description,
        modifier = modifier
    ) {
        Text(
            text = if (puzzleState.solved) {
                stringResource(R.string.slightly_wrong_all_clues_complete)
            } else {
                stringResource(slightlyWrongClueResource(requireNotNull(puzzleState.currentClue)))
            }
        )
        Text(
            stringResource(slightlyWrongFeedbackResource(feedback)),
            modifier = Modifier.semantics {
                liveRegion = LiveRegionMode.Polite
            }
        )
        Text(stringResource(R.string.inspections, progress.attempts))
        SlightlyWrongAnswerGrid(
            enabled = !puzzleState.solved,
            onAnswer = onAnswer
        )
        if (puzzleState.solved) {
            Text(stringResource(R.string.exhibit_solved, progress.attempts))
            Button(onClick = onContinue) {
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
        Button(onClick = onRestart) {
            Text(stringResource(R.string.restart))
        }
        Button(onClick = onRestartMuseum) {
            Text(stringResource(R.string.restart_museum))
        }
        Button(onClick = onReturnToEntrance) {
            Text(stringResource(R.string.return_to_entrance))
        }
    }
}

@Composable
private fun SlightlyWrongAnswerGrid(
    enabled: Boolean,
    onAnswer: (SlightlyWrongDetail) -> Unit
) {
    SlightlyWrongDetail.entries.chunked(2).forEach { rowDetails ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rowDetails.forEach { detail ->
                Button(
                    enabled = enabled,
                    onClick = { onAnswer(detail) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(slightlyWrongDetailResource(detail)))
                }
            }
        }
    }
}

@StringRes
private fun slightlyWrongDetailResource(detail: SlightlyWrongDetail): Int = when (detail) {
    SlightlyWrongDetail.CLOCK -> R.string.slightly_wrong_detail_clock
    SlightlyWrongDetail.BOOKSHELF -> R.string.slightly_wrong_detail_bookshelf
    SlightlyWrongDetail.GLOBE -> R.string.slightly_wrong_detail_globe
    SlightlyWrongDetail.ORRERY -> R.string.slightly_wrong_detail_orrery
}

@StringRes
private fun slightlyWrongClueResource(clue: SlightlyWrongClue): Int = when (clue) {
    SlightlyWrongClue.WRONG_ORDER -> R.string.slightly_wrong_clue_wrong_order
    SlightlyWrongClue.INCOMPLETE_NAMES -> R.string.slightly_wrong_clue_incomplete_names
    SlightlyWrongClue.FRAGMENTED_PLACES -> R.string.slightly_wrong_clue_fragmented_places
}

@StringRes
private fun slightlyWrongFeedbackResource(feedback: SlightlyWrongFeedback?): Int = when (feedback) {
    null -> R.string.slightly_wrong_instructions
    SlightlyWrongFeedback.INCORRECT -> R.string.slightly_wrong_feedback_incorrect
    SlightlyWrongFeedback.CORRECT_NEXT_CLUE -> R.string.slightly_wrong_feedback_correct
    SlightlyWrongFeedback.PUZZLE_SOLVED -> R.string.slightly_wrong_feedback_solved
    SlightlyWrongFeedback.ALREADY_SOLVED -> R.string.exhibit_already_solved
}

@Preview(name = "Slightly Wrong - portrait", widthDp = 412, heightDp = 915)
@Composable
private fun SlightlyWrongPortraitPreview() {
    MuseumGameTheme {
        SlightlyWrongContent(
            progress = ExhibitProgress(attempts = 1),
            puzzleState = SlightlyWrongState(
                currentClue = SlightlyWrongClue.INCOMPLETE_NAMES,
                completedClues = setOf(SlightlyWrongClue.WRONG_ORDER)
            ),
            feedback = SlightlyWrongFeedback.CORRECT_NEXT_CLUE,
            onAnswer = {},
            onRestart = {},
            onRestartMuseum = {},
            onContinue = {},
            isFinalExhibit = true,
            onReturnToEntrance = {}
        )
    }
}

@Preview(name = "Slightly Wrong - landscape", widthDp = 915, heightDp = 412)
@Composable
private fun SlightlyWrongLandscapePreview() {
    MuseumGameTheme {
        SlightlyWrongContent(
            progress = ExhibitProgress(attempts = 1),
            puzzleState = SlightlyWrongState(
                currentClue = SlightlyWrongClue.INCOMPLETE_NAMES,
                completedClues = setOf(SlightlyWrongClue.WRONG_ORDER)
            ),
            feedback = SlightlyWrongFeedback.CORRECT_NEXT_CLUE,
            onAnswer = {},
            onRestart = {},
            onRestartMuseum = {},
            onContinue = {},
            isFinalExhibit = true,
            onReturnToEntrance = {}
        )
    }
}
