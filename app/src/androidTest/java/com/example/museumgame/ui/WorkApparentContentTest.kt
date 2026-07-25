package com.example.museumgame.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.game.WorkApparentFeedback
import com.example.museumgame.game.WorkApparentState
import com.example.museumgame.ui.theme.MuseumGameTheme
import org.junit.Rule
import org.junit.Test

class WorkApparentContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun traceControlsAreVisibleAndTappingOneUpdatesFeedbackAndState() {
        composeRule.setContent {
            var puzzleState by remember { mutableStateOf(WorkApparentState()) }
            var feedback by remember { mutableStateOf<WorkApparentFeedback?>(null) }

            MuseumGameTheme {
                WorkApparentContent(
                    progress = ExhibitProgress(
                        attempts = if (feedback == null) 0 else 1
                    ),
                    puzzleState = puzzleState,
                    feedback = feedback,
                    onTrace = { stage ->
                        puzzleState = puzzleState.copy(
                            tracedStages = puzzleState.tracedStages + stage
                        )
                        feedback = WorkApparentFeedback.TRACE_ADVANCED
                    },
                    onInterrupt = {},
                    onRestart = {},
                    onRestartMuseum = {},
                    onContinue = {},
                    isFinalExhibit = true,
                    onReturnToEntrance = {}
                )
            }
        }

        listOf(
            "Tasks received",
            "Tasks organized",
            "Plan and review",
            "Tasks rearranged",
            "Return to inbox"
        ).forEach { label ->
            composeRule
                .onNodeWithText(label)
                .performScrollTo()
                .assertIsDisplayed()
        }
        composeRule
            .onNodeWithText("Tasks received")
            .assert(hasStateDescription("Next step"))
            .performClick()

        composeRule
            .onNodeWithText("The paper trail advances. Keep following the arrows.")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Tasks received")
            .assert(hasStateDescription("Traced"))
    }
}
