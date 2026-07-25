package com.example.museumgame.game

import com.example.museumgame.model.ExhibitIds
import com.example.museumgame.testsupport.SIMULATED_PROGRESS_CLASSIFICATIONS
import com.example.museumgame.testsupport.WORK_APPARENT_TRACE_SEQUENCE

internal fun MuseumGame.solveThrough(targetExhibitId: String) {
    val targetIndex = orderedExhibitIds.indexOf(targetExhibitId)
    require(targetIndex >= 0) {
        "Target exhibit is not in the catalog: $targetExhibitId"
    }

    orderedExhibitIds.take(targetIndex + 1).forEach { exhibitId ->
        if (!isCompleted(exhibitId)) {
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

                else -> error("No test solver mapped for exhibit ID: $exhibitId")
            }
        }
    }
}
