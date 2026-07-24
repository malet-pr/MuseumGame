package com.example.museumgame.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.museumgame.game.PenInspectionFeedback
import com.example.museumgame.game.PenLocation
import com.example.museumgame.game.ReappearingPenState
import com.example.museumgame.model.Exhibit
import com.example.museumgame.model.ExhibitIds
import com.example.museumgame.ui.theme.MuseumGameTheme
import org.junit.Rule
import org.junit.Test

class ExhibitContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun inspectionControlsAreVisibleAndTappingOneUpdatesFeedback() {
        val pen = Exhibit(
            id = ExhibitIds.REAPPEARING_PEN,
            name = "The Reappearing Pen",
            description = "The pen reappeared.",
            isAnomaly = true
        )

        composeRule.setContent {
            var feedback by mutableStateOf<PenInspectionFeedback?>(null)

            MuseumGameTheme {
                ExhibitContent(
                    exhibit = pen,
                    attempts = if (feedback == null) 0 else 1,
                    solved = false,
                    penState = ReappearingPenState(),
                    penFeedback = feedback,
                    onInspectLocation = {
                        feedback = PenInspectionFeedback.FIRST_LOCATION_EMPTY
                    },
                    onRestart = {},
                    onReturnToHall = {}
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
