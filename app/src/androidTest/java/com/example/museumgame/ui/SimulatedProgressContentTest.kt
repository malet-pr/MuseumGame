package com.example.museumgame.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.ProgressCategory
import com.example.museumgame.game.SimulatedProgressFeedback
import com.example.museumgame.game.SimulatedProgressSignal
import com.example.museumgame.game.SimulatedProgressState
import com.example.museumgame.ui.theme.MuseumGameTheme
import org.junit.Rule
import org.junit.Test

class SimulatedProgressContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun categoryControlsAreVisibleAndCorrectChoiceAdvancesTheEvidence() {
        composeRule.setContent {
            var puzzleState by remember { mutableStateOf(SimulatedProgressState()) }
            var feedback by remember {
                mutableStateOf<SimulatedProgressFeedback?>(null)
            }

            MuseumGameTheme {
                SimulatedProgressContent(
                    progress = ExhibitProgress(
                        attempts = if (feedback == null) 0 else 1
                    ),
                    puzzleState = puzzleState,
                    feedback = feedback,
                    onClassify = { category ->
                        if (category == ProgressCategory.ACTIVITY) {
                            puzzleState = puzzleState.copy(
                                classifiedSignals =
                                    puzzleState.classifiedSignals +
                                        SimulatedProgressSignal.ALIGNMENT_MEETINGS
                            )
                            feedback = SimulatedProgressFeedback.CORRECT_ACTIVITY
                        }
                    },
                    onRestart = {},
                    onRestartMuseum = {},
                    onContinue = {},
                    isFinalExhibit = true,
                    onReturnToEntrance = {}
                )
            }
        }

        listOf("Activity", "Output", "Impact").forEach { category ->
            composeRule
                .onNodeWithText(category)
                .performScrollTo()
                .assertIsDisplayed()
        }

        composeRule
            .onNodeWithText("Activity")
            .performClick()

        composeRule
            .onNodeWithText(
                "Correct. This records effort expended, not something produced or changed."
            )
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("A revised workflow guide is now available to the team.")
            .performScrollTo()
            .assertIsDisplayed()
    }
}
