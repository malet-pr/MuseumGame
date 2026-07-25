package com.example.museumgame.game

enum class SimulatedProgressSignal {
    ALIGNMENT_MEETINGS,
    WORKFLOW_GUIDE,
    FASTER_SETUP,
    CODE_REVIEW_TIME,
    AUTOMATION_SCRIPTS,
    FEWER_SUPPORT_REQUESTS
}

enum class ProgressCategory {
    ACTIVITY,
    OUTPUT,
    IMPACT
}

enum class SimulatedProgressFeedback {
    LOCKED,
    CORRECT_ACTIVITY,
    CORRECT_OUTPUT,
    CORRECT_IMPACT,
    INCORRECT_ACTIVITY,
    INCORRECT_OUTPUT,
    INCORRECT_IMPACT,
    PUZZLE_SOLVED,
    ALREADY_SOLVED
}

data class SimulatedProgressState(
    val classifiedSignals: List<SimulatedProgressSignal> = emptyList()
) {
    val currentSignal: SimulatedProgressSignal?
        get() = SimulatedProgressSignal.entries.getOrNull(classifiedSignals.size)

    val solved: Boolean
        get() = classifiedSignals.size == SimulatedProgressSignal.entries.size
}

data class SimulatedProgressResult(
    val state: SimulatedProgressState,
    val feedback: SimulatedProgressFeedback
)

class SimulatedProgressPuzzle {
    var state = SimulatedProgressState()
        private set

    fun classify(category: ProgressCategory): SimulatedProgressResult {
        if (state.solved) {
            return SimulatedProgressResult(
                state = state,
                feedback = SimulatedProgressFeedback.ALREADY_SOLVED
            )
        }

        val signal = requireNotNull(state.currentSignal)
        val correctCategory = correctCategoryFor(signal)
        if (category != correctCategory) {
            return SimulatedProgressResult(
                state = state,
                feedback = incorrectFeedbackFor(correctCategory)
            )
        }

        state = state.copy(classifiedSignals = state.classifiedSignals + signal)
        return SimulatedProgressResult(
            state = state,
            feedback = if (state.solved) {
                SimulatedProgressFeedback.PUZZLE_SOLVED
            } else {
                correctFeedbackFor(correctCategory)
            }
        )
    }

    fun restart() {
        state = SimulatedProgressState()
    }

    private fun correctCategoryFor(
        signal: SimulatedProgressSignal
    ): ProgressCategory = when (signal) {
        SimulatedProgressSignal.ALIGNMENT_MEETINGS -> ProgressCategory.ACTIVITY
        SimulatedProgressSignal.WORKFLOW_GUIDE -> ProgressCategory.OUTPUT
        SimulatedProgressSignal.FASTER_SETUP -> ProgressCategory.IMPACT
        SimulatedProgressSignal.CODE_REVIEW_TIME -> ProgressCategory.ACTIVITY
        SimulatedProgressSignal.AUTOMATION_SCRIPTS -> ProgressCategory.OUTPUT
        SimulatedProgressSignal.FEWER_SUPPORT_REQUESTS -> ProgressCategory.IMPACT
    }

    private fun correctFeedbackFor(
        category: ProgressCategory
    ): SimulatedProgressFeedback = when (category) {
        ProgressCategory.ACTIVITY -> SimulatedProgressFeedback.CORRECT_ACTIVITY
        ProgressCategory.OUTPUT -> SimulatedProgressFeedback.CORRECT_OUTPUT
        ProgressCategory.IMPACT -> SimulatedProgressFeedback.CORRECT_IMPACT
    }

    private fun incorrectFeedbackFor(
        correctCategory: ProgressCategory
    ): SimulatedProgressFeedback = when (correctCategory) {
        ProgressCategory.ACTIVITY -> SimulatedProgressFeedback.INCORRECT_ACTIVITY
        ProgressCategory.OUTPUT -> SimulatedProgressFeedback.INCORRECT_OUTPUT
        ProgressCategory.IMPACT -> SimulatedProgressFeedback.INCORRECT_IMPACT
    }
}
