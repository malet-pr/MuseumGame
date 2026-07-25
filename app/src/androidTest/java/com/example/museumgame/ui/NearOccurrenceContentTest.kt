package com.example.museumgame.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.NearOccurrenceFeedback
import com.example.museumgame.game.NearOccurrenceStage
import com.example.museumgame.game.NearOccurrenceState
import com.example.museumgame.ui.theme.MuseumGameTheme
import org.junit.Rule
import org.junit.Test

class NearOccurrenceContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun controlsAreVisibleAndAdvancingUpdatesStageAndFeedback() {
        composeRule.setContent {
            var puzzleState by remember { mutableStateOf(NearOccurrenceState()) }
            var feedback by remember {
                mutableStateOf<NearOccurrenceFeedback?>(null)
            }
            var attempts by remember { mutableIntStateOf(0) }

            MuseumGameTheme {
                NearOccurrenceContent(
                    progress = ExhibitProgress(attempts = attempts),
                    puzzleState = puzzleState,
                    feedback = feedback,
                    onAdvance = {
                        attempts += 1
                        puzzleState = puzzleState.copy(
                            stage = NearOccurrenceStage.SHIFTING
                        )
                        feedback = NearOccurrenceFeedback.SHIFTING
                    },
                    onPreserve = {},
                    onRestart = {},
                    onRestartMuseum = {},
                    onContinue = {},
                    isFinalExhibit = true,
                    onReturnToEntrance = {}
                )
            }
        }

        composeRule
            .onNodeWithText("Advance")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Preserve")
            .performScrollTo()
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Advance")
            .performClick()

        composeRule
            .onNodeWithText("Current moment: Shifting")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("The cup edges toward the table’s rim.")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Choices: 1")
            .performScrollTo()
            .assertIsDisplayed()
    }
}
