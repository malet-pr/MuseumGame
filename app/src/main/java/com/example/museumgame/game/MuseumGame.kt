package com.example.museumgame.game

import com.example.museumgame.model.Exhibit

class MuseumGame(
    val exhibits: List<Exhibit>
) {
    private val reappearingPenPuzzle = ReappearingPenPuzzle()
    private val slightlyWrongPuzzle = SlightlyWrongPuzzle()

    var reappearingPenProgress = ExhibitProgress()
        private set

    var slightlyWrongProgress = ExhibitProgress()
        private set

    val reappearingPenState: ReappearingPenState
        get() = reappearingPenPuzzle.state

    val slightlyWrongState: SlightlyWrongState
        get() = slightlyWrongPuzzle.state

    fun inspectReappearingPen(location: PenLocation): PenInspectionResult {
        if (reappearingPenProgress.solved) {
            return PenInspectionResult(
                state = reappearingPenPuzzle.state,
                feedback = PenInspectionFeedback.ALREADY_SOLVED
            )
        }

        val result = reappearingPenPuzzle.inspect(location)
        reappearingPenProgress = ExhibitProgress(
            attempts = reappearingPenProgress.attempts + 1,
            solved = result.state.solved
        )
        return result
    }

    fun answerSlightlyWrong(detail: SlightlyWrongDetail): SlightlyWrongResult {
        if (slightlyWrongProgress.solved) {
            return SlightlyWrongResult(
                state = slightlyWrongPuzzle.state,
                feedback = SlightlyWrongFeedback.ALREADY_SOLVED
            )
        }

        val result = slightlyWrongPuzzle.answer(detail)
        slightlyWrongProgress = ExhibitProgress(
            attempts = slightlyWrongProgress.attempts + 1,
            solved = result.state.solved
        )
        return result
    }

    fun restartReappearingPen() {
        reappearingPenProgress = ExhibitProgress()
        reappearingPenPuzzle.restart()
    }

    fun restartSlightlyWrong() {
        slightlyWrongProgress = ExhibitProgress()
        slightlyWrongPuzzle.restart()
    }
}
