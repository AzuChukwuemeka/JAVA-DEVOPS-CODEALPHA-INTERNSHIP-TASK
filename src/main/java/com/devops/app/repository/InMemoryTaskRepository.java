package com.devops.app.repository;

import com.devops.app.model.Task;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of TaskRepository.
 * Simulates a database layer for demonstration purposes.
 */
public class InMemoryTaskRepository implements TaskRepository {

    private final Map<String, Task> store = new ConcurrentHashMap<>();

    @Override
    public Task save(Task task) {
        store.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Task> findByStatus(String status) {
        return store.values().stream()
                .filter(t -> t.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByPriority(String priority) {
        return store.values().stream()
                .filter(t -> t.getPriority().equalsIgnoreCase(priority))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(String id) {
        return store.remove(id) != null;
    }

    @Override
    public int count() {
        return store.size();
    }
}
