package com.example.museumgame.game

enum class SlightlyWrongDetail {
    CLOCK,
    BOOKSHELF,
    GLOBE,
    ORRERY
}

enum class SlightlyWrongClue {
    WRONG_ORDER,
    INCOMPLETE_NAMES,
    FRAGMENTED_PLACES
}

enum class SlightlyWrongFeedback {
    INCORRECT,
    CORRECT_NEXT_CLUE,
    PUZZLE_SOLVED,
    ALREADY_SOLVED
}

data class SlightlyWrongState(
    val currentClue: SlightlyWrongClue? = SlightlyWrongClue.WRONG_ORDER,
    val completedClues: Set<SlightlyWrongClue> = emptySet(),
    val solved: Boolean = false
)

data class SlightlyWrongResult(
    val state: SlightlyWrongState,
    val feedback: SlightlyWrongFeedback
)

class SlightlyWrongPuzzle {
    var state = SlightlyWrongState()
        private set

    fun answer(detail: SlightlyWrongDetail): SlightlyWrongResult {
        if (state.solved) {
            return SlightlyWrongResult(
                state = state,
                feedback = SlightlyWrongFeedback.ALREADY_SOLVED
            )
        }

        val clue = requireNotNull(state.currentClue)
        if (detail != correctAnswerFor(clue)) {
            return SlightlyWrongResult(
                state = state,
                feedback = SlightlyWrongFeedback.INCORRECT
            )
        }

        val completedClues = state.completedClues + clue
        val nextClue = nextClueAfter(clue)
        state = state.copy(
            currentClue = nextClue,
            completedClues = completedClues,
            solved = nextClue == null
        )

        return SlightlyWrongResult(
            state = state,
            feedback = if (state.solved) {
                SlightlyWrongFeedback.PUZZLE_SOLVED
            } else {
                SlightlyWrongFeedback.CORRECT_NEXT_CLUE
            }
        )
    }

    fun restart() {
        state = SlightlyWrongState()
    }

    private fun correctAnswerFor(clue: SlightlyWrongClue): SlightlyWrongDetail = when (clue) {
        SlightlyWrongClue.WRONG_ORDER -> SlightlyWrongDetail.CLOCK
        SlightlyWrongClue.INCOMPLETE_NAMES -> SlightlyWrongDetail.BOOKSHELF
        SlightlyWrongClue.FRAGMENTED_PLACES -> SlightlyWrongDetail.GLOBE
    }

    private fun nextClueAfter(clue: SlightlyWrongClue): SlightlyWrongClue? = when (clue) {
        SlightlyWrongClue.WRONG_ORDER -> SlightlyWrongClue.INCOMPLETE_NAMES
        SlightlyWrongClue.INCOMPLETE_NAMES -> SlightlyWrongClue.FRAGMENTED_PLACES
        SlightlyWrongClue.FRAGMENTED_PLACES -> null
    }
}
