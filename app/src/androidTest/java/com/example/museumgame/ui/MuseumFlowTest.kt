package com.example.museumgame.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.espresso.Espresso.pressBack
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
            .onNodeWithText("Back to museum entrance")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun restartClearsPuzzleProgress() {
        openPenAndInspectTwoLocations()

        composeRule
            .onNodeWithText("Restart exhibit")
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
        openPenAndInspectTwoLocations()
        composeRule
            .onNodeWithText("Papers", substring = true)
            .performScrollTo()
            .performClick()
        composeRule
            .onNodeWithText("Continue to next exhibit")
            .performScrollTo()
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

    @Test
    fun restartMuseumResetsProgressAndReturnsToEntrance() {
        openPenAndInspectTwoLocations()

        composeRule
            .onNodeWithText("Restart museum")
            .performScrollTo()
            .performClick()
        composeRule
            .onNodeWithText(
                "All exhibit progress and attempts will be cleared, and you will return to the museum entrance."
            )
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Clear progress and restart")
            .performClick()
        composeRule
            .onNodeWithText("Resume visit")
            .performClick()

        composeRule
            .onNodeWithText("Inspections: 0")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun systemBackReturnsToEntranceAndResumePreservesProgress() {
        openPenAndInspectTwoLocations()

        pressBack()
        composeRule.waitForIdle()

        composeRule
            .onNodeWithText("Resume visit")
            .assertIsDisplayed()
            .performClick()
        composeRule
            .onNodeWithText("Inspections: 2")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Papers", substring = true)
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Empty desk", substring = true)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun entranceReflectsLocksRevisitsAndCompletedVisit() {
        composeRule
            .onNodeWithText("Locked: Slightly Wrong")
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsNotEnabled()

        composeRule
            .onNodeWithText("Resume visit")
            .performClick()
        solvePenFromScratch()
        composeRule
            .onNodeWithText("Back to museum entrance")
            .performScrollTo()
            .performClick()

        composeRule
            .onNodeWithText("Revisit The Reappearing Pen")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Resume visit")
            .performClick()
        solveSlightlyWrong()
        composeRule
            .onNodeWithText("Complete visit")
            .performScrollTo()
            .performClick()

        composeRule
            .onNodeWithText("Museum visit complete. Every exhibit may be revisited.")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onAllNodesWithText("Resume visit")
            .assertCountEquals(0)
    }

    private fun openPenAndInspectTwoLocations() {
        composeRule
            .onNodeWithText("Resume visit")
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

    private fun solvePenFromScratch() {
        composeRule
            .onNodeWithText("Papers")
            .performScrollTo()
            .performClick()
        composeRule
            .onNodeWithText("Empty desk")
            .performScrollTo()
            .performClick()
        composeRule
            .onNodeWithText("Papers", substring = true)
            .performScrollTo()
            .performClick()
    }

    private fun solveSlightlyWrong() {
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
    }
}
