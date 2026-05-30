package com.devops.app;

import com.devops.app.service.TaskService;
import com.devops.app.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Main entry point for the Java DevOps Demo Application.
 * Demonstrates Gradle-based build automation, dependency management,
 * and CI/CD pipeline integration.
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("==============================================");
        logger.info("  Java DevOps Project — Build Automation Demo");
        logger.info("==============================================");

        Application app = new Application();
        app.run();
    }

    public void run() {
        logger.info("Starting application...");

        TaskService taskService = new TaskService();

        // Seed some sample tasks
        taskService.addTask("Set up Gradle build scripts", "HIGH");
        taskService.addTask("Configure CI/CD pipeline with GitHub Actions", "HIGH");
        taskService.addTask("Write unit tests with JUnit 5", "MEDIUM");
        taskService.addTask("Enable JaCoCo code coverage reporting", "MEDIUM");
        taskService.addTask("Build and push Docker image", "LOW");
        taskService.addTask("Configure dependency caching", "LOW");

        List<Task> allTasks = taskService.getAllTasks();
        logger.info("Loaded {} DevOps tasks:", allTasks.size());
        allTasks.forEach(task ->
            logger.info("  [{}] {} — Priority: {}", task.getStatus(), task.getTitle(), task.getPriority())
        );

        // Complete a few tasks
        taskService.completeTask(allTasks.get(0).getId());
        taskService.completeTask(allTasks.get(1).getId());

        logger.info("\nCompleted tasks:");
        taskService.getCompletedTasks().forEach(task ->
            logger.info("  ✅ {}", task.getTitle())
        );

        logger.info("\nPending tasks:");
        taskService.getPendingTasks().forEach(task ->
            logger.info("  ⏳ {} [{}]", task.getTitle(), task.getPriority())
        );

        logger.info("\nApplication run complete. Build pipeline is healthy.");
    }
}
