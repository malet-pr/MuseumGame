package com.example.museumgame.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CreativeChaosPuzzleTest {

    @Test
    fun initialAvailablePiecesAreTheFourRawFragments() {
        val puzzle = CreativeChaosPuzzle()

        assertEquals(
            setOf(
                ChaosPiece.GRID,
                ChaosPiece.SKETCH,
                ChaosPiece.CODE,
                ChaosPiece.NOTE
            ),
            puzzle.state.availablePieces
        )
        assertTrue(puzzle.state.generatedPieces.isEmpty())
        assertTrue(puzzle.state.consumedPieces.isEmpty())
    }

    @Test
    fun completionIsDerivedOnlyFromTheCurrentStep() {
        assertFalse(
            CreativeChaosState(step = CreativeChaosStep.FORM_PATTERN).solved
        )
        assertFalse(
            CreativeChaosState(step = CreativeChaosStep.ADD_MOTION).solved
        )
        assertFalse(
            CreativeChaosState(step = CreativeChaosStep.ADD_MEANING).solved
        )
        assertTrue(
            CreativeChaosState(step = CreativeChaosStep.COMPLETE).solved
        )
    }

    @Test
    fun completedStateConsumesOnlyTheExplicitGamePieces() {
        val completedState = CreativeChaosState(
            step = CreativeChaosStep.COMPLETE
        )

        assertEquals(
            setOf(
                ChaosPiece.GRID,
                ChaosPiece.SKETCH,
                ChaosPiece.CODE,
                ChaosPiece.NOTE,
                ChaosPiece.PATTERN,
                ChaosPiece.MOTION
            ),
            completedState.consumedPieces
        )
    }

    @Test
    fun selectingAThirdPieceIsRejectedWithoutChangingThePair() {
        val puzzle = CreativeChaosPuzzle()
        puzzle.toggle(ChaosPiece.GRID)
        puzzle.toggle(ChaosPiece.SKETCH)

        val result = puzzle.toggle(ChaosPiece.CODE)

        assertEquals(
            setOf(ChaosPiece.GRID, ChaosPiece.SKETCH),
            puzzle.state.selectedPieces
        )
        assertEquals(
            CreativeChaosFeedback.TOO_MANY_SELECTED,
            result.feedback
        )
    }

    @Test
    fun incompleteCombineDoesNotClearTheSelection() {
        val puzzle = CreativeChaosPuzzle()
        puzzle.toggle(ChaosPiece.GRID)

        val result = puzzle.combine()

        assertEquals(setOf(ChaosPiece.GRID), puzzle.state.selectedPieces)
        assertEquals(
            CreativeChaosFeedback.INCOMPLETE_SELECTION,
            result.feedback
        )
    }

    @Test
    fun completeWrongPairClearsSelectionWithoutAdvancing() {
        val puzzle = CreativeChaosPuzzle()
        puzzle.toggle(ChaosPiece.GRID)
        puzzle.toggle(ChaosPiece.CODE)

        val result = puzzle.combine()

        assertEquals(CreativeChaosStep.FORM_PATTERN, puzzle.state.step)
        assertTrue(puzzle.state.selectedPieces.isEmpty())
        assertEquals(CreativeChaosFeedback.WRONG_PAIR, result.feedback)
    }

    @Test
    fun recipesAreOrderInsensitiveAndGeneratePatternMotionThenMeaning() {
        val puzzle = CreativeChaosPuzzle()

        puzzle.toggle(ChaosPiece.SKETCH)
        puzzle.toggle(ChaosPiece.GRID)
        val pattern = puzzle.combine()

        assertEquals(CreativeChaosStep.ADD_MOTION, puzzle.state.step)
        assertEquals(setOf(ChaosPiece.PATTERN), puzzle.state.generatedPieces)
        assertEquals(
            CreativeChaosFeedback.PATTERN_CREATED,
            pattern.feedback
        )

        puzzle.toggle(ChaosPiece.CODE)
        puzzle.toggle(ChaosPiece.PATTERN)
        val motion = puzzle.combine()

        assertEquals(CreativeChaosStep.ADD_MEANING, puzzle.state.step)
        assertEquals(
            setOf(ChaosPiece.PATTERN, ChaosPiece.MOTION),
            puzzle.state.generatedPieces
        )
        assertEquals(CreativeChaosFeedback.MOTION_CREATED, motion.feedback)

        puzzle.toggle(ChaosPiece.NOTE)
        puzzle.toggle(ChaosPiece.MOTION)
        val meaning = puzzle.combine()

        assertEquals(CreativeChaosStep.COMPLETE, puzzle.state.step)
        assertTrue(puzzle.state.solved)
        assertEquals(
            CreativeChaosFeedback.PUZZLE_SOLVED,
            meaning.feedback
        )
    }

    @Test
    fun acceptedSelectionClearsPreviousFeedback() {
        val puzzle = CreativeChaosPuzzle()
        puzzle.toggle(ChaosPiece.GRID)
        puzzle.combine()

        val result = puzzle.toggle(ChaosPiece.SKETCH)

        assertNull(result.feedback)
    }

    @Test
    fun actionAfterSolvedReturnsExplicitFeedbackWithoutChangingState() {
        val puzzle = solvedPuzzle()
        val solvedState = puzzle.state

        val result = puzzle.toggle(ChaosPiece.GRID)

        assertEquals(solvedState, puzzle.state)
        assertEquals(
            CreativeChaosFeedback.ALREADY_SOLVED,
            result.feedback
        )
    }

    @Test
    fun restartClearsSelectionGeneratedPiecesAndSolvedState() {
        val puzzle = solvedPuzzle()

        puzzle.restart()

        assertEquals(CreativeChaosState(), puzzle.state)
        assertFalse(puzzle.state.solved)
    }

    private fun solvedPuzzle() = CreativeChaosPuzzle().also { puzzle ->
        listOf(
            setOf(ChaosPiece.GRID, ChaosPiece.SKETCH),
            setOf(ChaosPiece.PATTERN, ChaosPiece.CODE),
            setOf(ChaosPiece.MOTION, ChaosPiece.NOTE)
        ).forEach { recipe ->
            recipe.forEach(puzzle::toggle)
            puzzle.combine()
        }
    }
}
