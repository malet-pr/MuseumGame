package com.example.museumgame.viewmodel

import com.example.museumgame.game.PenLocation
import com.example.museumgame.game.SlightlyWrongDetail
import com.example.museumgame.game.WorkApparentInterruption
import com.example.museumgame.model.ExhibitIds
import com.example.museumgame.testsupport.SIMULATED_PROGRESS_CLASSIFICATIONS
import com.example.museumgame.testsupport.WORK_APPARENT_TRACE_SEQUENCE

internal fun MuseumGameViewModel.solveThrough(targetExhibitId: String) {
    val orderedExhibitIds = uiState.visitStatuses.map { it.exhibitId }
    val targetIndex = orderedExhibitIds.indexOf(targetExhibitId)
    require(targetIndex >= 0) {
        "Target exhibit is not in the catalog: $targetExhibitId"
    }

    orderedExhibitIds.take(targetIndex + 1).forEach { exhibitId ->
        val completed = uiState.visitStatuses
            .first { it.exhibitId == exhibitId }
            .completed
        if (!completed) {
            openExhibit(exhibitId)
            when (exhibitId) {
                ExhibitIds.REAPPEARING_PEN -> {
                    inspectReappearingPen(PenLocation.PAPERS)
                    inspectReappearingPen(PenLocation.EMPTY_DESK)
                    inspectReappearingPen(PenLocation.PAPERS)
                }

                ExhibitIds.SLIGHTLY_WRONG -> {
                    answerSlightlyWrong(SlightlyWrongDetail.CLOCK)
                    answerSlightlyWrong(SlightlyWrongDetail.BOOKSHELF)
                    answerSlightlyWrong(SlightlyWrongDetail.GLOBE)
                }

                ExhibitIds.WORK_APPARENT -> {
                    WORK_APPARENT_TRACE_SEQUENCE.forEach(::traceWorkApparent)
                    interruptWorkApparent(WorkApparentInterruption.COMPLETE_ONE_TASK)
                }

                ExhibitIds.SIMULATED_PROGRESS -> {
                    SIMULATED_PROGRESS_CLASSIFICATIONS.forEach(
                        ::classifySimulatedProgress
                    )
                }

                ExhibitIds.NEAR_OCCURRENCE -> {
                    advanceNearOccurrence()
                    advanceNearOccurrence()
                    preserveNearOccurrence()
                }

                else -> error("No ViewModel test solver mapped for exhibit ID: $exhibitId")
            }
        }
    }
}
