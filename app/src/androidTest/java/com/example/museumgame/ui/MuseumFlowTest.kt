package com.example.museumgame.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.museumgame.MainActivity
import org.junit.Rule
import org.junit.Test

class MuseumFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun recreationPreservesExhibitDestinationAndPuzzleProgress() {
        openPenAndInspectTwoLocations()

        composeRule.activityRule.scenario.recreate()
        composeRule.waitForIdle()

        composeRule
            .onNodeWithText("The Reappearing Pen")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Inspections: 2")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Papers — checked")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Empty desk — checked")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Back to museum hall")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun restartClearsPuzzleProgress() {
        openPenAndInspectTwoLocations()

        composeRule
            .onNodeWithText("Restart")
            .performScrollTo()
            .performClick()

        composeRule
            .onNodeWithText("Inspections: 0")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Papers")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Empty desk")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(
                "Inspect the scene. The pen may return somewhere you have already checked."
            )
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun slightlyWrongCanBeOpenedAndSolved() {
        composeRule
            .onNodeWithText("Open Slightly Wrong")
            .performClick()
        composeRule
            .onNodeWithText("Clock")
            .performScrollTo()
            .performClick()
        composeRule
            .onNodeWithText("Bookshelf")
            .performScrollTo()
            .performClick()
        composeRule
            .onNodeWithText("Globe")
            .performScrollTo()
            .performClick()

        composeRule
            .onNodeWithText("You solved the exhibit in 3 inspections.")
            .performScrollTo()
            .assertIsDisplayed()
    }

    private fun openPenAndInspectTwoLocations() {
        composeRule
            .onNodeWithText("Open The Reappearing Pen")
            .performClick()
        composeRule
            .onNodeWithText("Papers")
            .performScrollTo()
            .performClick()
        composeRule
            .onNodeWithText("Empty desk")
            .performScrollTo()
            .performClick()
    }
}
