package com.devops.app.repository;

import com.devops.app.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Task persistence operations.
 */
public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(String id);

    List<Task> findAll();

    List<Task> findByStatus(String status);

    List<Task> findByPriority(String priority);

    boolean deleteById(String id);

    int count();
}
