package com.example.museumgame.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.SlightlyWrongFeedback
import com.example.museumgame.game.SlightlyWrongState
import com.example.museumgame.ui.theme.MuseumGameTheme
import org.junit.Rule
import org.junit.Test

class SlightlyWrongContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun answerControlsAreVisibleAndTappingOneUpdatesFeedback() {
        composeRule.setContent {
            var feedback by mutableStateOf<SlightlyWrongFeedback?>(null)

            MuseumGameTheme {
                SlightlyWrongContent(
                    progress = ExhibitProgress(
                        attempts = if (feedback == null) 0 else 1
                    ),
                    puzzleState = SlightlyWrongState(),
                    feedback = feedback,
                    onAnswer = {
                        feedback = SlightlyWrongFeedback.CORRECT_NEXT_CLUE
                    },
                    onRestart = {},
                    onRestartMuseum = {},
                    onContinue = {},
                    isFinalExhibit = true,
                    onReturnToEntrance = {}
                )
            }
        }

        composeRule.onNodeWithText("Clock").assertIsDisplayed()
        composeRule.onNodeWithText("Bookshelf").assertIsDisplayed()
        composeRule.onNodeWithText("Globe").assertIsDisplayed()
        composeRule.onNodeWithText("Orrery").assertIsDisplayed()

        composeRule.onNodeWithText("Clock").performClick()

        composeRule
            .onNodeWithText("That memory fits. One detail becomes clearer.")
            .assertIsDisplayed()
    }
}
