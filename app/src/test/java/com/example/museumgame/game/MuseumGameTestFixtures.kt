package com.example.museumgame.game

import com.example.museumgame.model.ExhibitIds

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
                    WorkApparentStage.entries.forEach(::traceWorkApparent)
                    interruptWorkApparent(WorkApparentInterruption.COMPLETE_ONE_TASK)
                }

                ExhibitIds.SIMULATED_PROGRESS -> {
                    listOf(
                        ProgressCategory.ACTIVITY,
                        ProgressCategory.OUTPUT,
                        ProgressCategory.IMPACT,
                        ProgressCategory.ACTIVITY,
                        ProgressCategory.OUTPUT,
                        ProgressCategory.IMPACT
                    ).forEach(::classifySimulatedProgress)
                }

                else -> error("No test solver mapped for exhibit ID: $exhibitId")
            }
        }
    }
}
