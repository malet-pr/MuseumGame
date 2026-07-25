package com.example.museumgame.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.museumgame.game.ChaosPiece
import com.example.museumgame.game.CreativeChaosFeedback
import com.example.museumgame.game.CreativeChaosState
import com.example.museumgame.game.ExhibitProgress
import com.example.museumgame.ui.theme.MuseumGameTheme
import org.junit.Rule
import org.junit.Test

class CreativeChaosContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun availablePiecesAreVisibleAndIncompleteCombineShowsFeedback() {
        composeRule.setContent {
            var puzzleState by remember { mutableStateOf(CreativeChaosState()) }
            var feedback by remember {
                mutableStateOf<CreativeChaosFeedback?>(null)
            }

            MuseumGameTheme {
                CreativeChaosContent(
                    progress = ExhibitProgress(),
                    puzzleState = puzzleState,
                    feedback = feedback,
                    onTogglePiece = { piece ->
                        puzzleState = puzzleState.copy(
                            selectedPieces = puzzleState.selectedPieces + piece
                        )
                        feedback = null
                    },
                    onCombine = {
                        feedback = CreativeChaosFeedback.INCOMPLETE_SELECTION
                    },
                    onRestart = {},
                    onRestartMuseum = {},
                    onContinue = {},
                    isFinalExhibit = true,
                    onReturnToEntrance = {}
                )
            }
        }

        listOf("Grid", "Sketch", "Code", "Note").forEach { piece ->
            composeRule
                .onNodeWithText("$piece — Available")
                .performScrollTo()
                .assertIsDisplayed()
        }

        composeRule
            .onNodeWithText("Grid — Available")
            .performClick()
        composeRule
            .onNodeWithText("Grid — Selected")
            .performScrollTo()
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Combine fragments")
            .performScrollTo()
            .performClick()
        composeRule
            .onNodeWithText("Select two fragments to combine.")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Combinations: 0")
            .performScrollTo()
            .assertIsDisplayed()
    }
}
