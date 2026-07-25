package com.example.museumgame.game

enum class ChaosPiece {
    GRID,
    SKETCH,
    CODE,
    NOTE,
    PATTERN,
    MOTION
}

enum class CreativeChaosStep {
    FORM_PATTERN,
    ADD_MOTION,
    ADD_MEANING,
    COMPLETE
}

enum class CreativeChaosFeedback {
    LOCKED,
    TOO_MANY_SELECTED,
    INCOMPLETE_SELECTION,
    WRONG_PAIR,
    PATTERN_CREATED,
    MOTION_CREATED,
    PUZZLE_SOLVED,
    ALREADY_SOLVED
}

data class CreativeChaosState(
    val step: CreativeChaosStep = CreativeChaosStep.FORM_PATTERN,
    val selectedPieces: Set<ChaosPiece> = emptySet(),
    val solved: Boolean = false
) {
    val availablePieces: Set<ChaosPiece>
        get() = when (step) {
            CreativeChaosStep.FORM_PATTERN -> setOf(
                ChaosPiece.GRID,
                ChaosPiece.SKETCH,
                ChaosPiece.CODE,
                ChaosPiece.NOTE
            )

            CreativeChaosStep.ADD_MOTION -> setOf(
                ChaosPiece.PATTERN,
                ChaosPiece.CODE,
                ChaosPiece.NOTE
            )

            CreativeChaosStep.ADD_MEANING -> setOf(
                ChaosPiece.MOTION,
                ChaosPiece.NOTE
            )

            CreativeChaosStep.COMPLETE -> emptySet()
        }

    val generatedPieces: Set<ChaosPiece>
        get() = when (step) {
            CreativeChaosStep.FORM_PATTERN -> emptySet()
            CreativeChaosStep.ADD_MOTION -> setOf(ChaosPiece.PATTERN)
            CreativeChaosStep.ADD_MEANING,
            CreativeChaosStep.COMPLETE -> setOf(
                ChaosPiece.PATTERN,
                ChaosPiece.MOTION
            )
        }

    val consumedPieces: Set<ChaosPiece>
        get() = when (step) {
            CreativeChaosStep.FORM_PATTERN -> emptySet()
            CreativeChaosStep.ADD_MOTION -> setOf(
                ChaosPiece.GRID,
                ChaosPiece.SKETCH
            )

            CreativeChaosStep.ADD_MEANING -> setOf(
                ChaosPiece.GRID,
                ChaosPiece.SKETCH,
                ChaosPiece.PATTERN,
                ChaosPiece.CODE
            )

            CreativeChaosStep.COMPLETE -> ChaosPiece.entries.toSet()
        }
}

data class CreativeChaosResult(
    val state: CreativeChaosState,
    val feedback: CreativeChaosFeedback?
)

class CreativeChaosPuzzle {
    var state = CreativeChaosState()
        private set

    fun toggle(piece: ChaosPiece): CreativeChaosResult {
        if (state.solved) {
            return result(CreativeChaosFeedback.ALREADY_SOLVED)
        }
        if (piece !in state.availablePieces) {
            return result(feedback = null)
        }

        val selectedPieces = when {
            piece in state.selectedPieces -> state.selectedPieces - piece
            state.selectedPieces.size == MAX_SELECTED_PIECES ->
                return result(CreativeChaosFeedback.TOO_MANY_SELECTED)

            else -> state.selectedPieces + piece
        }

        state = state.copy(selectedPieces = selectedPieces)
        return result(feedback = null)
    }

    fun combine(): CreativeChaosResult {
        if (state.solved) {
            return result(CreativeChaosFeedback.ALREADY_SOLVED)
        }
        if (state.selectedPieces.size < MAX_SELECTED_PIECES) {
            return result(CreativeChaosFeedback.INCOMPLETE_SELECTION)
        }

        val recipe = recipeFor(state.step)
        if (state.selectedPieces != recipe.pieces) {
            state = state.copy(selectedPieces = emptySet())
            return result(CreativeChaosFeedback.WRONG_PAIR)
        }

        state = state.copy(
            step = recipe.nextStep,
            selectedPieces = emptySet(),
            solved = recipe.nextStep == CreativeChaosStep.COMPLETE
        )
        return result(recipe.feedback)
    }

    fun restart() {
        state = CreativeChaosState()
    }

    private fun result(
        feedback: CreativeChaosFeedback?
    ) = CreativeChaosResult(state = state, feedback = feedback)

    private fun recipeFor(step: CreativeChaosStep): CreativeChaosRecipe =
        when (step) {
            CreativeChaosStep.FORM_PATTERN -> CreativeChaosRecipe(
                pieces = setOf(ChaosPiece.GRID, ChaosPiece.SKETCH),
                nextStep = CreativeChaosStep.ADD_MOTION,
                feedback = CreativeChaosFeedback.PATTERN_CREATED
            )

            CreativeChaosStep.ADD_MOTION -> CreativeChaosRecipe(
                pieces = setOf(ChaosPiece.PATTERN, ChaosPiece.CODE),
                nextStep = CreativeChaosStep.ADD_MEANING,
                feedback = CreativeChaosFeedback.MOTION_CREATED
            )

            CreativeChaosStep.ADD_MEANING -> CreativeChaosRecipe(
                pieces = setOf(ChaosPiece.MOTION, ChaosPiece.NOTE),
                nextStep = CreativeChaosStep.COMPLETE,
                feedback = CreativeChaosFeedback.PUZZLE_SOLVED
            )

            CreativeChaosStep.COMPLETE -> error("A completed puzzle has no recipe")
        }

    private data class CreativeChaosRecipe(
        val pieces: Set<ChaosPiece>,
        val nextStep: CreativeChaosStep,
        val feedback: CreativeChaosFeedback
    )

    private companion object {
        const val MAX_SELECTED_PIECES = 2
    }
}
