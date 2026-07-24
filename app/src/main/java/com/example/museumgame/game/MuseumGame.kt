package com.example.museumgame.game;

import com.example.museumgame.model.Exhibit;

class MuseumGame(
        val exhibits:List<Exhibit>
) {
    private val reappearingPenPuzzle = ReappearingPenPuzzle()

    var attempts: Int = 0
    private set

    var solved: Boolean = false
    private set

    val reappearingPenState: ReappearingPenState
        get() = reappearingPenPuzzle.state

    fun inspect(exhibit: Exhibit): String {
        if (solved) {
            return "The room is already solved."
        }

        attempts++

        if (exhibit.isAnomaly) {
            solved = true
        }

        return exhibit.description
    }

    fun inspectReappearingPen(location: PenLocation): PenInspectionResult {
        if (solved) {
            return PenInspectionResult(
                state = reappearingPenPuzzle.state,
                feedback = PenInspectionFeedback.ALREADY_SOLVED
            )
        }

        attempts++
        val result = reappearingPenPuzzle.inspect(location)
        solved = result.state.solved
        return result
    }

    fun restart() {
        attempts = 0
        solved = false
        reappearingPenPuzzle.restart()
    }
}
