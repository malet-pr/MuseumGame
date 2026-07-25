package com.example.museumgame.game

enum class WorkApparentStage {
    TASKS_RECEIVED,
    TASKS_ORGANIZED,
    PLAN_AND_REVIEW,
    TASKS_REARRANGED,
    RETURN_TO_INBOX
}

enum class WorkApparentInterruption {
    REORGANIZE_TASKS,
    UPDATE_EFFORT_METRICS,
    COMPLETE_ONE_TASK
}

enum class WorkApparentFeedback {
    LOCKED,
    TRACE_ADVANCED,
    WRONG_NEXT_STAGE,
    LOOP_TRACED,
    TRACE_ALREADY_COMPLETE,
    INTERRUPT_TOO_EARLY,
    LOOP_CONTINUES,
    PUZZLE_SOLVED,
    ALREADY_SOLVED
}

data class WorkApparentState(
    val tracedStages: List<WorkApparentStage> = emptyList(),
    val solved: Boolean = false
) {
    val loopTraced: Boolean
        get() = tracedStages.size == WorkApparentStage.entries.size

    val nextStage: WorkApparentStage?
        get() = WorkApparentStage.entries.getOrNull(tracedStages.size)
}

data class WorkApparentResult(
    val state: WorkApparentState,
    val feedback: WorkApparentFeedback
)

class WorkApparentPuzzle {
    var state = WorkApparentState()
        private set

    fun trace(stage: WorkApparentStage): WorkApparentResult {
        val feedback = when {
            state.solved -> WorkApparentFeedback.ALREADY_SOLVED
            state.loopTraced -> WorkApparentFeedback.TRACE_ALREADY_COMPLETE
            stage != state.nextStage -> WorkApparentFeedback.WRONG_NEXT_STAGE
            else -> {
                state = state.copy(tracedStages = state.tracedStages + stage)
                if (state.loopTraced) {
                    WorkApparentFeedback.LOOP_TRACED
                } else {
                    WorkApparentFeedback.TRACE_ADVANCED
                }
            }
        }

        return WorkApparentResult(state, feedback)
    }

    fun interrupt(interruption: WorkApparentInterruption): WorkApparentResult {
        val feedback = when {
            state.solved -> WorkApparentFeedback.ALREADY_SOLVED
            !state.loopTraced -> WorkApparentFeedback.INTERRUPT_TOO_EARLY
            interruption == WorkApparentInterruption.COMPLETE_ONE_TASK -> {
                state = state.copy(solved = true)
                WorkApparentFeedback.PUZZLE_SOLVED
            }

            else -> WorkApparentFeedback.LOOP_CONTINUES
        }

        return WorkApparentResult(state, feedback)
    }

    fun restart() {
        state = WorkApparentState()
    }
}
