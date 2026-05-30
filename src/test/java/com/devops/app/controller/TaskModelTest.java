package com.devops.app.controller;

import com.devops.app.model.Task;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link Task} model.
 */
public class TaskModelTest {

    @Test
    public void newTask_isInitiallyPending() {
        Task task = new Task("Fix bug", "HIGH");
        assertEquals("PENDING", task.getStatus());
        assertFalse(task.isCompleted());
        assertNull(task.getCompletedAt());
    }

    @Test
    public void markComplete_setsStatusAndTimestamp() {
        Task task = new Task("Deploy", "MEDIUM");
        task.markComplete();

        assertEquals("DONE", task.getStatus());
        assertTrue(task.isCompleted());
        assertNotNull(task.getCompletedAt());
    }

    @Test
    public void toString_containsKeyInfo() {
        Task task = new Task("Sample", "LOW");
        String str = task.toString();
        assertTrue(str.contains("Sample"));
        assertTrue(str.contains("LOW"));
        assertTrue(str.contains("PENDING"));
    }

    @Test
    public void eachTask_hasUniqueId() {
        Task t1 = new Task("Task 1", "HIGH");
        Task t2 = new Task("Task 2", "HIGH");
        assertNotEquals(t1.getId(), t2.getId());
    }
}
