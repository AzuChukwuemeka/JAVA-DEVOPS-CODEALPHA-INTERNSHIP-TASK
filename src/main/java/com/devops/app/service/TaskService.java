package com.devops.app.service;

import com.devops.app.model.Task;
import com.devops.app.repository.InMemoryTaskRepository;
import com.devops.app.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Task management.
 * Contains all business logic for creating, updating, and querying tasks.
 */
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository repository;

    public TaskService() {
        this.repository = new InMemoryTaskRepository();
    }

    // Constructor injection for testability
    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates and persists a new task.
     */
    public Task addTask(String title, String priority) {
        validatePriority(priority);
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title must not be blank");
        }
        Task task = new Task(title.trim(), priority.toUpperCase());
        repository.save(task);
        logger.debug("Added task: {}", task);
        return task;
    }

    /**
     * Marks a task as completed by its ID.
     */
    public Task completeTask(String id) {
        Task task = findOrThrow(id);
        if (task.isCompleted()) {
            throw new IllegalStateException("Task " + id + " is already completed");
        }
        task.markComplete();
        repository.save(task);
        logger.info("Task completed: {}", task.getTitle());
        return task;
    }

    /**
     * Updates the priority of a task.
     */
    public Task updatePriority(String id, String newPriority) {
        validatePriority(newPriority);
        Task task = findOrThrow(id);
        task.setPriority(newPriority.toUpperCase());
        repository.save(task);
        return task;
    }

    /**
     * Deletes a task by ID.
     */
    public boolean deleteTask(String id) {
        findOrThrow(id);
        return repository.deleteById(id);
    }

    public Optional<Task> findById(String id) {
        return repository.findById(id);
    }

    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    public List<Task> getPendingTasks() {
        return repository.findByStatus("PENDING");
    }

    public List<Task> getCompletedTasks() {
        return repository.findByStatus("DONE");
    }

    public List<Task> getTasksByPriority(String priority) {
        validatePriority(priority);
        return repository.findByPriority(priority.toUpperCase());
    }

    public int getTotalCount() {
        return repository.count();
    }

    // --- Private helpers ---

    private Task findOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
    }

    private void validatePriority(String priority) {
        if (priority == null || !List.of("HIGH", "MEDIUM", "LOW").contains(priority.toUpperCase())) {
            throw new IllegalArgumentException("Priority must be HIGH, MEDIUM, or LOW. Got: " + priority);
        }
    }
}
