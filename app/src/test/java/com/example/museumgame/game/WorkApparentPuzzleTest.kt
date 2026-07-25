package com.example.museumgame.game

import com.example.museumgame.testsupport.WORK_APPARENT_TRACE_SEQUENCE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkApparentPuzzleTest {

    @Test
    fun receivedThroughReturnToInboxExposesLoopBeforeIntervention() {
        val puzzle = WorkApparentPuzzle()

        assertEquals(WorkApparentStage.TASKS_RECEIVED, puzzle.state.nextStage)

        puzzle.trace(WorkApparentStage.TASKS_RECEIVED)
        assertEquals(WorkApparentStage.TASKS_ORGANIZED, puzzle.state.nextStage)

        puzzle.trace(WorkApparentStage.TASKS_ORGANIZED)
        assertEquals(WorkApparentStage.PLAN_AND_REVIEW, puzzle.state.nextStage)

        puzzle.trace(WorkApparentStage.PLAN_AND_REVIEW)
        assertEquals(WorkApparentStage.TASKS_REARRANGED, puzzle.state.nextStage)

        puzzle.trace(WorkApparentStage.TASKS_REARRANGED)
        assertEquals(WorkApparentStage.RETURN_TO_INBOX, puzzle.state.nextStage)

        val loopResult = puzzle.trace(WorkApparentStage.RETURN_TO_INBOX)

        assertTrue(puzzle.state.loopTraced)
        assertFalse(puzzle.state.solved)
        assertEquals(WorkApparentFeedback.LOOP_TRACED, loopResult.feedback)

        val interventionResult = puzzle.interrupt(
            WorkApparentInterruption.COMPLETE_ONE_TASK
        )

        assertTrue(puzzle.state.solved)
        assertEquals(
            WorkApparentFeedback.PUZZLE_SOLVED,
            interventionResult.feedback
        )
    }

    @Test
    fun enumDeclarationOrderMatchesTheIntentionalNarrativeSequence() {
        assertEquals(
            WORK_APPARENT_TRACE_SEQUENCE,
            WorkApparentStage.entries.toList()
        )
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
        WORK_APPARENT_TRACE_SEQUENCE.forEach(puzzle::trace)
    }
}
