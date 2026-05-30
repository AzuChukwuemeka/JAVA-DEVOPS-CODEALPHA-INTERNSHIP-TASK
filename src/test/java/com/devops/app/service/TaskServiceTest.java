package com.devops.app.service;

import com.devops.app.model.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link TaskService}.
 * Covers task creation, completion, updates, deletion, and error handling.
 */
public class TaskServiceTest {

    private TaskService taskService;

    @Before
    public void setUp() {
        taskService = new TaskService();
    }

    // ─── addTask ───────────────────────────────────────────────────────────────

    @Test
    public void addTask_createsTaskWithCorrectFields() {
        Task task = taskService.addTask("Deploy to production", "HIGH");

        assertNotNull(task);
        assertEquals("Deploy to production", task.getTitle());
        assertEquals("HIGH", task.getPriority());
        assertEquals("PENDING", task.getStatus());
        assertNotNull(task.getId());
        assertNotNull(task.getCreatedAt());
    }

    @Test
    public void addTask_taskIsRetrievableById() {
        Task task = taskService.addTask("Write tests", "MEDIUM");
        Optional<Task> found = taskService.findById(task.getId());

        assertTrue(found.isPresent());
        assertEquals(task.getId(), found.get().getId());
    }

    @Test
    public void addTask_acceptsCaseInsensitivePriority() {
        assertNotNull(taskService.addTask("Task A", "high"));
        assertNotNull(taskService.addTask("Task B", "medium"));
        assertNotNull(taskService.addTask("Task C", "low"));
        assertNotNull(taskService.addTask("Task D", "HIGH"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTask_throwsOnBlankTitle() {
        taskService.addTask("   ", "HIGH");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTask_throwsOnNullTitle() {
        taskService.addTask(null, "HIGH");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTask_throwsOnInvalidPriority() {
        taskService.addTask("Task", "URGENT");
    }

    // ─── completeTask ──────────────────────────────────────────────────────────

    @Test
    public void completeTask_setsStatusToDoneAndRecordsTimestamp() {
        Task task = taskService.addTask("Run CI pipeline", "HIGH");
        taskService.completeTask(task.getId());

        Optional<Task> updated = taskService.findById(task.getId());
        assertTrue(updated.isPresent());
        assertEquals("DONE", updated.get().getStatus());
        assertNotNull(updated.get().getCompletedAt());
    }

    @Test(expected = IllegalStateException.class)
    public void completeTask_throwsIfAlreadyCompleted() {
        Task task = taskService.addTask("Already done", "LOW");
        taskService.completeTask(task.getId());
        taskService.completeTask(task.getId());  // should throw
    }

    @Test(expected = IllegalArgumentException.class)
    public void completeTask_throwsIfNotFound() {
        taskService.completeTask("NONEXISTENT_ID");
    }

    // ─── updatePriority ────────────────────────────────────────────────────────

    @Test
    public void updatePriority_changesPrioritySuccessfully() {
        Task task = taskService.addTask("Refactor code", "LOW");
        taskService.updatePriority(task.getId(), "HIGH");

        Optional<Task> updated = taskService.findById(task.getId());
        assertTrue(updated.isPresent());
        assertEquals("HIGH", updated.get().getPriority());
    }

    // ─── deleteTask ────────────────────────────────────────────────────────────

    @Test
    public void deleteTask_removesTaskFromRepository() {
        Task task = taskService.addTask("Temporary task", "LOW");
        taskService.deleteTask(task.getId());
        assertFalse(taskService.findById(task.getId()).isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteTask_throwsIfNotFound() {
        taskService.deleteTask("FAKE_ID");
    }

    // ─── query methods ─────────────────────────────────────────────────────────

    @Test
    public void getPendingTasks_returnsOnlyPendingTasks() {
        taskService.addTask("Task A", "HIGH");
        taskService.addTask("Task B", "LOW");
        Task toComplete = taskService.addTask("Task C", "MEDIUM");
        taskService.completeTask(toComplete.getId());

        List<Task> pending = taskService.getPendingTasks();
        assertEquals(2, pending.size());
        for (Task t : pending) {
            assertEquals("PENDING", t.getStatus());
        }
    }

    @Test
    public void getCompletedTasks_returnsOnlyCompletedTasks() {
        Task t1 = taskService.addTask("Done A", "HIGH");
        taskService.addTask("Not done", "LOW");
        taskService.completeTask(t1.getId());

        List<Task> completed = taskService.getCompletedTasks();
        assertEquals(1, completed.size());
        assertEquals("DONE", completed.get(0).getStatus());
    }

    @Test
    public void getTasksByPriority_filtersCorrectly() {
        taskService.addTask("High task 1", "HIGH");
        taskService.addTask("High task 2", "HIGH");
        taskService.addTask("Low task", "LOW");

        List<Task> highTasks = taskService.getTasksByPriority("HIGH");
        assertEquals(2, highTasks.size());
    }

    @Test
    public void getTotalCount_returnsCorrectCount() {
        assertEquals(0, taskService.getTotalCount());
        taskService.addTask("T1", "HIGH");
        taskService.addTask("T2", "LOW");
        assertEquals(2, taskService.getTotalCount());
    }
}
