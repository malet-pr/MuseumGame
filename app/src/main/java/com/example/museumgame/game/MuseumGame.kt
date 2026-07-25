package com.example.museumgame.game

import com.example.museumgame.model.Exhibit
import com.example.museumgame.model.ExhibitCatalog
import com.example.museumgame.model.ExhibitIds

class MuseumGame {
    private val reappearingPenPuzzle = ReappearingPenPuzzle()
    private val slightlyWrongPuzzle = SlightlyWrongPuzzle()

    val exhibits = ExhibitCatalog.orderedExhibits
    val orderedExhibitIds = exhibits.map(Exhibit::id)

    var reappearingPenProgress = ExhibitProgress()
        private set

    var slightlyWrongProgress = ExhibitProgress()
        private set

    val reappearingPenState: ReappearingPenState
        get() = reappearingPenPuzzle.state

    val slightlyWrongState: SlightlyWrongState
        get() = slightlyWrongPuzzle.state

    fun isCompleted(exhibitId: String): Boolean = when (exhibitId) {
        ExhibitIds.REAPPEARING_PEN -> reappearingPenState.solved
        ExhibitIds.SLIGHTLY_WRONG -> slightlyWrongState.solved
        else -> false
    }

    fun firstUnfinishedExhibitId(): String? =
        orderedExhibitIds.firstOrNull { !isCompleted(it) }

    fun isUnlocked(exhibitId: String): Boolean {
        val index = orderedExhibitIds.indexOf(exhibitId)
        if (index < 0) return false
        return orderedExhibitIds.take(index).all(::isCompleted)
    }

    fun nextExhibitId(after: String): String? {
        val index = orderedExhibitIds.indexOf(after)
        if (index < 0) return null
        return orderedExhibitIds.getOrNull(index + 1)
    }

    fun visitStatuses(): List<ExhibitVisitStatus> {
        val currentExhibitId = firstUnfinishedExhibitId()
        return orderedExhibitIds.map { exhibitId ->
            ExhibitVisitStatus(
                exhibitId = exhibitId,
                completed = isCompleted(exhibitId),
                unlocked = isUnlocked(exhibitId),
                current = exhibitId == currentExhibitId
            )
        }
    }

    fun inspectReappearingPen(location: PenLocation): PenInspectionResult {
        if (reappearingPenState.solved) {
            return PenInspectionResult(
                state = reappearingPenPuzzle.state,
                feedback = PenInspectionFeedback.ALREADY_SOLVED
            )
        }

        val result = reappearingPenPuzzle.inspect(location)
        reappearingPenProgress = ExhibitProgress(
            attempts = reappearingPenProgress.attempts + 1
        )
        return result
    }

    fun answerSlightlyWrong(detail: SlightlyWrongDetail): SlightlyWrongResult {
        if (slightlyWrongState.solved) {
            return SlightlyWrongResult(
                state = slightlyWrongPuzzle.state,
                feedback = SlightlyWrongFeedback.ALREADY_SOLVED
            )
        }
        if (!isUnlocked(ExhibitIds.SLIGHTLY_WRONG)) {
            return SlightlyWrongResult(
                state = slightlyWrongPuzzle.state,
                feedback = SlightlyWrongFeedback.LOCKED
            )
        }

        val result = slightlyWrongPuzzle.answer(detail)
        slightlyWrongProgress = ExhibitProgress(
            attempts = slightlyWrongProgress.attempts + 1
        )
        return result
    }

    fun restartExhibit(exhibitId: String) {
        val restartIndex = orderedExhibitIds.indexOf(exhibitId)
        if (restartIndex < 0) return

        orderedExhibitIds.drop(restartIndex).forEach { id ->
            when (id) {
                ExhibitIds.REAPPEARING_PEN -> {
                    reappearingPenProgress = ExhibitProgress()
                    reappearingPenPuzzle.restart()
                }

                ExhibitIds.SLIGHTLY_WRONG -> {
                    slightlyWrongProgress = ExhibitProgress()
                    slightlyWrongPuzzle.restart()
                }
            }
        }
    }

    fun restartMuseum() {
        orderedExhibitIds.firstOrNull()?.let(::restartExhibit)
    }
}
