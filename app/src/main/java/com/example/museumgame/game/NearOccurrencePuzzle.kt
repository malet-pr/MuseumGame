package com.example.museumgame.game

enum class NearOccurrenceStage {
    SETTLED,
    SHIFTING,
    AT_THRESHOLD
}

data class NearOccurrenceState(
    val stage: NearOccurrenceStage = NearOccurrenceStage.SETTLED,
    val solved: Boolean = false
)

enum class NearOccurrenceFeedback {
    LOCKED,
    SHIFTING,
    AT_THRESHOLD,
    TOO_SOON,
    SPILL_RESET,
    PUZZLE_SOLVED,
    ALREADY_SOLVED
}

data class NearOccurrenceResult(
    val state: NearOccurrenceState,
    val feedback: NearOccurrenceFeedback
)

class NearOccurrencePuzzle {
    var state = NearOccurrenceState()
        private set

    fun advance(): NearOccurrenceResult {
        if (state.solved) {
            return NearOccurrenceResult(
                state = state,
                feedback = NearOccurrenceFeedback.ALREADY_SOLVED
            )
        }

        val feedback = when (state.stage) {
            NearOccurrenceStage.SETTLED -> {
                state = state.copy(stage = NearOccurrenceStage.SHIFTING)
                NearOccurrenceFeedback.SHIFTING
            }

            NearOccurrenceStage.SHIFTING -> {
                state = state.copy(stage = NearOccurrenceStage.AT_THRESHOLD)
                NearOccurrenceFeedback.AT_THRESHOLD
            }

            NearOccurrenceStage.AT_THRESHOLD -> {
                state = NearOccurrenceState()
                NearOccurrenceFeedback.SPILL_RESET
            }
        }

        return NearOccurrenceResult(state = state, feedback = feedback)
    }

    fun preserve(): NearOccurrenceResult {
        if (state.solved) {
            return NearOccurrenceResult(
                state = state,
                feedback = NearOccurrenceFeedback.ALREADY_SOLVED
            )
        }

        val feedback = if (state.stage == NearOccurrenceStage.AT_THRESHOLD) {
            state = state.copy(solved = true)
            NearOccurrenceFeedback.PUZZLE_SOLVED
        } else {
            NearOccurrenceFeedback.TOO_SOON
        }

        return NearOccurrenceResult(state = state, feedback = feedback)
    }

    fun restart() {
        state = NearOccurrenceState()
    }
}
