package com.example.museumgame.game

enum class PenLocation {
    PAPERS,
    FADED_OUTLINE,
    EMPTY_DESK,
    FILING_CABINET
}

enum class PenInspectionFeedback {
    LOCKED,
    FIRST_LOCATION_EMPTY,
    SAME_LOCATION_STILL_EMPTY,
    PEN_REAPPEARED,
    LOCATION_EMPTY,
    PEN_FOUND,
    ALREADY_SOLVED
}

data class ReappearingPenState(
    val inspectedLocations: Set<PenLocation> = emptySet(),
    val targetLocation: PenLocation? = null,
    val penLocation: PenLocation? = null,
    val solved: Boolean = false
)

data class PenInspectionResult(
    val state: ReappearingPenState,
    val feedback: PenInspectionFeedback
)

class ReappearingPenPuzzle {
    var state = ReappearingPenState()
        private set

    fun inspect(location: PenLocation): PenInspectionResult {
        val feedback = when {
            state.solved -> PenInspectionFeedback.ALREADY_SOLVED

            state.targetLocation == null -> {
                state = state.copy(
                    inspectedLocations = setOf(location),
                    targetLocation = location
                )
                PenInspectionFeedback.FIRST_LOCATION_EMPTY
            }

            state.penLocation == location -> {
                state = state.copy(
                    inspectedLocations = state.inspectedLocations + location,
                    solved = true
                )
                PenInspectionFeedback.PEN_FOUND
            }

            state.penLocation == null && location == state.targetLocation -> {
                PenInspectionFeedback.SAME_LOCATION_STILL_EMPTY
            }

            state.penLocation == null -> {
                state = state.copy(
                    inspectedLocations = state.inspectedLocations + location,
                    penLocation = state.targetLocation
                )
                PenInspectionFeedback.PEN_REAPPEARED
            }

            else -> {
                state = state.copy(
                    inspectedLocations = state.inspectedLocations + location
                )
                PenInspectionFeedback.LOCATION_EMPTY
            }
        }

        return PenInspectionResult(state, feedback)
    }

    fun restart() {
        state = ReappearingPenState()
    }
}
