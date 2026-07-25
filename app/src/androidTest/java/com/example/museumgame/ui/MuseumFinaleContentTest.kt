package com.example.museumgame.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.museumgame.ui.theme.MuseumGameTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MuseumFinaleContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun completionTeaserAndRequiredActionsAreVisible() {
        var selectedAction: String? = null
        composeRule.setContent {
            MuseumGameTheme {
                MuseumFinaleContent(
                    onReturnToEntrance = { selectedAction = "entrance" },
                    onRestartMuseum = { selectedAction = "restart" }
                )
            }
        }

        composeRule
            .onNodeWithText("Museum visit complete")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(
                "You found every minor mystery. Beyond the final gallery, another city is waiting."
            )
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(
                "Check out my next game: The City of Strange Kubernetes Clusters."
            )
            .performScrollTo()
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Back to museum entrance")
            .performScrollTo()
            .performClick()
        composeRule.runOnIdle {
            assertEquals("entrance", selectedAction)
        }

        composeRule
            .onNodeWithText("Restart museum")
            .performScrollTo()
            .performClick()
        composeRule.runOnIdle {
            assertEquals("restart", selectedAction)
        }
    }
}
