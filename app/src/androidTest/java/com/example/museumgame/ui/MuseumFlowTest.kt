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
    fun futureExhibitsAreLockedAtTheEntrance() {
        composeRule
            .onNodeWithText("Choose exhibit")
            .performScrollTo()
            .performClick()
        composeRule
            .onNodeWithText("Locked: Slightly Wrong")
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsNotEnabled()
        composeRule
            .onNodeWithText("Locked: Work Apparent")
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsNotEnabled()
        composeRule
            .onNodeWithText("Locked: Simulated Progress")
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun completedPenCanBeRevisitedFromTheEntranceSelector() {
        composeRule
            .onNodeWithText("Resume visit")
            .performClick()
        solvePenFromScratch()
        composeRule
            .onNodeWithText("Back to museum entrance")
            .performScrollTo()
            .performClick()

        composeRule
            .onNodeWithText("Choose exhibit")
            .performScrollTo()
            .performClick()
        composeRule
            .onNodeWithText("Completed: The Reappearing Pen")
            .assertIsDisplayed()
            .performClick()

        composeRule
            .onNodeWithText("You solved the exhibit in 3 inspections.")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun completeMuseumJourneySmokeTest() {
        openSimulatedProgress()
        solveSimulatedProgress()
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

    @Test
    fun recreationPreservesSimulatedProgressDestinationAndClassification() {
        openSimulatedProgress()
        composeRule
            .onNodeWithText("Activity")
            .performScrollTo()
            .performClick()

        assertFirstSimulatedProgressClassification()

        composeRule.activityRule.scenario.recreate()
        composeRule.waitForIdle()

        composeRule
            .onNodeWithText("Simulated Progress")
            .assertIsDisplayed()
        assertFirstSimulatedProgressClassification()
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

    private fun openSimulatedProgress() {
        composeRule
            .onNodeWithText("Resume visit")
            .performClick()
        solvePenFromScratch()
        continueToNextExhibit()
        solveSlightlyWrong()
        continueToNextExhibit()
        solveWorkApparent()
        continueToNextExhibit()
    }

    private fun continueToNextExhibit() {
        composeRule
            .onNodeWithText("Continue to next exhibit")
            .performScrollTo()
            .performClick()
    }

    private fun assertFirstSimulatedProgressClassification() {
        composeRule
            .onNodeWithText("Choices: 1")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(
                "Correct. This records effort expended, not something produced or changed."
            )
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Signals classified: 1 of 6")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("A revised workflow guide is now available to the team.")
            .performScrollTo()
            .assertIsDisplayed()
    }

    private fun solveWorkApparent() {
        listOf(
            "Tasks received",
            "Tasks organized",
            "Plan and review",
            "Tasks rearranged",
            "Return to inbox",
            "Complete one task"
        ).forEach { label ->
            composeRule
                .onNodeWithText(label)
                .performScrollTo()
                .performClick()
        }
    }

    private fun solveSimulatedProgress() {
        listOf(
            "Activity",
            "Output",
            "Impact",
            "Activity",
            "Output",
            "Impact"
        ).forEach { category ->
            composeRule
                .onNodeWithText(category)
                .performScrollTo()
                .performClick()
        }
    }
}
