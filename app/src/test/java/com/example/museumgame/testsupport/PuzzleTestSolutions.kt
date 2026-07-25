package com.example.museumgame.testsupport

import com.example.museumgame.game.ProgressCategory
import com.example.museumgame.game.WorkApparentStage

internal val WORK_APPARENT_TRACE_SEQUENCE = listOf(
    WorkApparentStage.TASKS_RECEIVED,
    WorkApparentStage.TASKS_ORGANIZED,
    WorkApparentStage.PLAN_AND_REVIEW,
    WorkApparentStage.TASKS_REARRANGED,
    WorkApparentStage.RETURN_TO_INBOX
)

internal val SIMULATED_PROGRESS_CLASSIFICATIONS = listOf(
    ProgressCategory.ACTIVITY,
    ProgressCategory.OUTPUT,
    ProgressCategory.IMPACT,
    ProgressCategory.ACTIVITY,
    ProgressCategory.OUTPUT,
    ProgressCategory.IMPACT
)
