package com.devops.app.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a DevOps task in the task management system.
 */
public class Task {

    private final String id;
    private String title;
    private String priority;   // HIGH, MEDIUM, LOW
    private String status;     // PENDING, IN_PROGRESS, DONE
    private final LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public Task(String title, String priority) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.title = title;
        this.priority = priority;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setStatus(String status) { this.status = status; }

    public void markComplete() {
        this.status = "DONE";
        this.completedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return "DONE".equals(this.status);
    }

    @Override
    public String toString() {
        return String.format("Task{id='%s', title='%s', priority='%s', status='%s'}",
                id, title, priority, status);
    }
}
