package com.example.museumgame.game

import com.example.museumgame.model.Exhibit
import com.example.museumgame.model.ExhibitCatalog
import com.example.museumgame.model.ExhibitIds

class MuseumGame {
    private val reappearingPenPuzzle = ReappearingPenPuzzle()
    private val slightlyWrongPuzzle = SlightlyWrongPuzzle()
    private val workApparentPuzzle = WorkApparentPuzzle()
    private val simulatedProgressPuzzle = SimulatedProgressPuzzle()

    val exhibits = ExhibitCatalog.orderedExhibits
    val orderedExhibitIds = exhibits.map(Exhibit::id)
    private val attemptsByExhibitId = orderedExhibitIds
        .associateWith { 0 }
        .toMutableMap()

    var reappearingPenFeedback: PenInspectionFeedback? = null
        private set

    var slightlyWrongFeedback: SlightlyWrongFeedback? = null
        private set

    var workApparentFeedback: WorkApparentFeedback? = null
        private set

    var simulatedProgressFeedback: SimulatedProgressFeedback? = null
        private set

    val reappearingPenProgress: ExhibitProgress
        get() = progressFor(ExhibitIds.REAPPEARING_PEN)

    val slightlyWrongProgress: ExhibitProgress
        get() = progressFor(ExhibitIds.SLIGHTLY_WRONG)

    val workApparentProgress: ExhibitProgress
        get() = progressFor(ExhibitIds.WORK_APPARENT)

    val simulatedProgressProgress: ExhibitProgress
        get() = progressFor(ExhibitIds.SIMULATED_PROGRESS)

    val reappearingPenState: ReappearingPenState
        get() = reappearingPenPuzzle.state

    val slightlyWrongState: SlightlyWrongState
        get() = slightlyWrongPuzzle.state

    val workApparentState: WorkApparentState
        get() = workApparentPuzzle.state

    val simulatedProgressState: SimulatedProgressState
        get() = simulatedProgressPuzzle.state

    fun isCompleted(exhibitId: String): Boolean = when (exhibitId) {
        ExhibitIds.REAPPEARING_PEN -> reappearingPenState.solved
        ExhibitIds.SLIGHTLY_WRONG -> slightlyWrongState.solved
        ExhibitIds.WORK_APPARENT -> workApparentState.solved
        ExhibitIds.SIMULATED_PROGRESS -> simulatedProgressState.solved
        else -> error("No completion rule mapped for exhibit ID: $exhibitId")
    }

    fun progressFor(exhibitId: String): ExhibitProgress =
        ExhibitProgress(
            attempts = attemptsByExhibitId[exhibitId]
                ?: error("No attempt counter mapped for exhibit ID: $exhibitId")
        )

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
        val result = if (reappearingPenState.solved) {
            PenInspectionResult(
                state = reappearingPenPuzzle.state,
                feedback = PenInspectionFeedback.ALREADY_SOLVED
            )
        } else {
            recordAttempt(ExhibitIds.REAPPEARING_PEN)
            reappearingPenPuzzle.inspect(location)
        }

        reappearingPenFeedback = result.feedback
        return result
    }

    fun answerSlightlyWrong(detail: SlightlyWrongDetail): SlightlyWrongResult {
        val result = if (slightlyWrongState.solved) {
            SlightlyWrongResult(
                state = slightlyWrongPuzzle.state,
                feedback = SlightlyWrongFeedback.ALREADY_SOLVED
            )
        } else if (!isUnlocked(ExhibitIds.SLIGHTLY_WRONG)) {
            SlightlyWrongResult(
                state = slightlyWrongPuzzle.state,
                feedback = SlightlyWrongFeedback.LOCKED
            )
        } else {
            recordAttempt(ExhibitIds.SLIGHTLY_WRONG)
            slightlyWrongPuzzle.answer(detail)
        }

        slightlyWrongFeedback = result.feedback
        return result
    }

    fun traceWorkApparent(stage: WorkApparentStage): WorkApparentResult {
        val result = when {
            workApparentState.solved -> WorkApparentResult(
                state = workApparentPuzzle.state,
                feedback = WorkApparentFeedback.ALREADY_SOLVED
            )

            !isUnlocked(ExhibitIds.WORK_APPARENT) -> WorkApparentResult(
                state = workApparentPuzzle.state,
                feedback = WorkApparentFeedback.LOCKED
            )

            else -> {
                recordAttempt(ExhibitIds.WORK_APPARENT)
                workApparentPuzzle.trace(stage)
            }
        }

        workApparentFeedback = result.feedback
        return result
    }

    fun interruptWorkApparent(
        interruption: WorkApparentInterruption
    ): WorkApparentResult {
        val result = when {
            workApparentState.solved -> WorkApparentResult(
                state = workApparentPuzzle.state,
                feedback = WorkApparentFeedback.ALREADY_SOLVED
            )

            !isUnlocked(ExhibitIds.WORK_APPARENT) -> WorkApparentResult(
                state = workApparentPuzzle.state,
                feedback = WorkApparentFeedback.LOCKED
            )

            else -> {
                recordAttempt(ExhibitIds.WORK_APPARENT)
                workApparentPuzzle.interrupt(interruption)
            }
        }

        workApparentFeedback = result.feedback
        return result
    }

    fun classifySimulatedProgress(
        category: ProgressCategory
    ): SimulatedProgressResult {
        val result = when {
            simulatedProgressState.solved -> SimulatedProgressResult(
                state = simulatedProgressPuzzle.state,
                feedback = SimulatedProgressFeedback.ALREADY_SOLVED
            )

            !isUnlocked(ExhibitIds.SIMULATED_PROGRESS) -> SimulatedProgressResult(
                state = simulatedProgressPuzzle.state,
                feedback = SimulatedProgressFeedback.LOCKED
            )

            else -> {
                recordAttempt(ExhibitIds.SIMULATED_PROGRESS)
                simulatedProgressPuzzle.classify(category)
            }
        }

        simulatedProgressFeedback = result.feedback
        return result
    }

    fun restartExhibit(exhibitId: String) {
        val restartIndex = orderedExhibitIds.indexOf(exhibitId)
        if (restartIndex < 0) return

        orderedExhibitIds.drop(restartIndex).forEach { id ->
            attemptsByExhibitId[id] = 0
            resetPuzzle(id)
        }
    }

    fun restartMuseum() {
        orderedExhibitIds.firstOrNull()?.let(::restartExhibit)
    }

    private fun recordAttempt(exhibitId: String) {
        attemptsByExhibitId[exhibitId] =
            (attemptsByExhibitId[exhibitId]
                ?: error("No attempt counter mapped for exhibit ID: $exhibitId")) + 1
    }

    private fun resetPuzzle(exhibitId: String) {
        when (exhibitId) {
            ExhibitIds.REAPPEARING_PEN -> {
                reappearingPenPuzzle.restart()
                reappearingPenFeedback = null
            }

            ExhibitIds.SLIGHTLY_WRONG -> {
                slightlyWrongPuzzle.restart()
                slightlyWrongFeedback = null
            }

            ExhibitIds.WORK_APPARENT -> {
                workApparentPuzzle.restart()
                workApparentFeedback = null
            }

            ExhibitIds.SIMULATED_PROGRESS -> {
                simulatedProgressPuzzle.restart()
                simulatedProgressFeedback = null
            }

            else -> error("No restart rule mapped for exhibit ID: $exhibitId")
        }
    }
}
