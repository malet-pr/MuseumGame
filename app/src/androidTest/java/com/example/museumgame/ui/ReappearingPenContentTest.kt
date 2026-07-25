package com.example.museumgame.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.PenInspectionFeedback
import com.example.museumgame.game.PenLocation
import com.example.museumgame.game.ReappearingPenState
import com.example.museumgame.ui.theme.MuseumGameTheme
import org.junit.Rule
import org.junit.Test

class ReappearingPenContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun inspectionControlsAreVisibleAndTappingOneUpdatesFeedback() {
        composeRule.setContent {
            var feedback by remember { mutableStateOf<PenInspectionFeedback?>(null) }

            MuseumGameTheme {
                ReappearingPenContent(
                    progress = ExhibitProgress(
                        attempts = if (feedback == null) 0 else 1
                    ),
                    puzzleState = ReappearingPenState(),
                    feedback = feedback,
                    onInspectLocation = {
                        feedback = PenInspectionFeedback.FIRST_LOCATION_EMPTY
                    },
                    onRestart = {},
                    onRestartMuseum = {},
                    onContinue = {},
                    isFinalExhibit = false,
                    onReturnToEntrance = {}
                )
            }
        }

        composeRule.onNodeWithText("Papers").assertIsDisplayed()
        composeRule.onNodeWithText("Faded outline").assertIsDisplayed()
        composeRule.onNodeWithText("Empty desk").assertIsDisplayed()
        composeRule.onNodeWithText("Filing cabinet").assertIsDisplayed()

        composeRule.onNodeWithText("Papers").performClick()

        composeRule
            .onNodeWithText("No pen here. Remember this spot.")
            .assertIsDisplayed()
    }
}
