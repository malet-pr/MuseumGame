package com.example.museumgame.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkApparentPuzzleTest {

    @Test
    fun tracingStagesInOrderRevealsTheLoopWithoutSolvingIt() {
        val puzzle = WorkApparentPuzzle()

        WorkApparentStage.entries.forEach(puzzle::trace)

        assertEquals(WorkApparentStage.entries, puzzle.state.tracedStages)
        assertTrue(puzzle.state.loopTraced)
        assertFalse(puzzle.state.solved)
    }

    @Test
    fun wrongStageDoesNotEraseOrAdvanceTheTrace() {
        val puzzle = WorkApparentPuzzle()
        puzzle.trace(WorkApparentStage.TASKS_RECEIVED)

        val result = puzzle.trace(WorkApparentStage.TASKS_REARRANGED)

        assertEquals(
            listOf(WorkApparentStage.TASKS_RECEIVED),
            puzzle.state.tracedStages
        )
        assertEquals(WorkApparentFeedback.WRONG_NEXT_STAGE, result.feedback)
    }

    @Test
    fun interruptionBeforeFullTraceDoesNotSolve() {
        val puzzle = WorkApparentPuzzle()

        val result = puzzle.interrupt(WorkApparentInterruption.COMPLETE_ONE_TASK)

        assertFalse(puzzle.state.solved)
        assertEquals(WorkApparentFeedback.INTERRUPT_TOO_EARLY, result.feedback)
    }

    @Test
    fun activityOnlyInterventionsLeaveTheLoopRunning() {
        val puzzle = tracedPuzzle()

        val reorganize = puzzle.interrupt(WorkApparentInterruption.REORGANIZE_TASKS)
        val metrics = puzzle.interrupt(WorkApparentInterruption.UPDATE_EFFORT_METRICS)

        assertFalse(puzzle.state.solved)
        assertEquals(WorkApparentFeedback.LOOP_CONTINUES, reorganize.feedback)
        assertEquals(WorkApparentFeedback.LOOP_CONTINUES, metrics.feedback)
    }

    @Test
    fun completingOneTaskBreaksTheTracedLoop() {
        val puzzle = tracedPuzzle()

        val result = puzzle.interrupt(WorkApparentInterruption.COMPLETE_ONE_TASK)

        assertTrue(puzzle.state.solved)
        assertEquals(WorkApparentFeedback.PUZZLE_SOLVED, result.feedback)
    }

    @Test
    fun restartClearsTraceAndSolvedState() {
        val puzzle = tracedPuzzle()
        puzzle.interrupt(WorkApparentInterruption.COMPLETE_ONE_TASK)

        puzzle.restart()

        assertEquals(WorkApparentState(), puzzle.state)
    }

    private fun tracedPuzzle() = WorkApparentPuzzle().also { puzzle ->
        WorkApparentStage.entries.forEach(puzzle::trace)
    }
}
