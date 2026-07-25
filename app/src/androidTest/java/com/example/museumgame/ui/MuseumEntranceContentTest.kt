package com.example.museumgame.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.museumgame.game.ExhibitVisitStatus
import com.example.museumgame.model.ExhibitCatalog
import com.example.museumgame.model.ExhibitIds
import com.example.museumgame.ui.theme.MuseumGameTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class MuseumEntranceContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun completedAndCurrentOptionsOpenTheirExhibits() {
        var openedExhibitId: String? = null
        setEntranceContent(
            visitStatuses = listOf(
                status(
                    exhibitId = ExhibitIds.REAPPEARING_PEN,
                    completed = true,
                    unlocked = true
                ),
                status(
                    exhibitId = ExhibitIds.SLIGHTLY_WRONG,
                    unlocked = true,
                    current = true
                )
            ),
            onOpenExhibit = { openedExhibitId = it }
        )

        openSelector()
        composeRule
            .onNodeWithText("Completed: The Reappearing Pen")
            .assertIsDisplayed()
            .performClick()
        composeRule.runOnIdle {
            assertEquals(ExhibitIds.REAPPEARING_PEN, openedExhibitId)
        }

        openSelector()
        composeRule
            .onNodeWithText("Current: Slightly Wrong")
            .assertIsDisplayed()
            .performClick()
        composeRule.runOnIdle {
            assertEquals(ExhibitIds.SLIGHTLY_WRONG, openedExhibitId)
        }
    }

    @Test
    fun lockedOptionIsVisibleButCannotBeSelected() {
        var openedExhibitId: String? = null
        setEntranceContent(
            visitStatuses = listOf(
                status(
                    exhibitId = ExhibitIds.REAPPEARING_PEN,
                    unlocked = true,
                    current = true
                ),
                status(exhibitId = ExhibitIds.SLIGHTLY_WRONG)
            ),
            onOpenExhibit = { openedExhibitId = it }
        )

        openSelector()
        composeRule
            .onNodeWithText("Locked: Slightly Wrong")
            .assertIsDisplayed()
            .assertIsNotEnabled()
        composeRule.runOnIdle {
            assertNull(openedExhibitId)
        }
    }

    @Test
    fun completedVisitShowsCompletionAndHidesResume() {
        setEntranceContent(
            visitStatuses = ExhibitCatalog.orderedExhibits.map { exhibit ->
                status(
                    exhibitId = exhibit.id,
                    completed = true,
                    unlocked = true
                )
            },
            onOpenExhibit = {}
        )

        composeRule
            .onNodeWithText("Museum visit complete. Every exhibit may be revisited.")
            .assertIsDisplayed()
        composeRule
            .onAllNodesWithText("Resume visit")
            .assertCountEquals(0)
    }

    private fun setEntranceContent(
        visitStatuses: List<ExhibitVisitStatus>,
        onOpenExhibit: (String) -> Unit
    ) {
        composeRule.setContent {
            MuseumGameTheme {
                MuseumEntranceContent(
                    exhibits = ExhibitCatalog.orderedExhibits,
                    visitStatuses = visitStatuses,
                    onResumeVisit = {},
                    onOpenExhibit = onOpenExhibit,
                    onRestartMuseum = {}
                )
            }
        }
    }

    private fun openSelector() {
        composeRule
            .onNodeWithText("Choose exhibit")
            .performScrollTo()
            .performClick()
    }

    private fun status(
        exhibitId: String,
        completed: Boolean = false,
        unlocked: Boolean = false,
        current: Boolean = false
    ) = ExhibitVisitStatus(
        exhibitId = exhibitId,
        completed = completed,
        unlocked = unlocked,
        current = current
    )
}
