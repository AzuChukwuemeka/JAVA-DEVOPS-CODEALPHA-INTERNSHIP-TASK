package com.devops.app.config;

/**
 * Central configuration constants for the application.
 * In a production system these would be loaded from environment variables
 * or a config server (e.g., Spring Config, Consul).
 */
public final class AppConfig {

    private AppConfig() {}   // utility class

    public static final String APP_NAME    = "java-devops-project";
    public static final String APP_VERSION = "1.0.0";
    public static final String LOG_LEVEL   = "INFO";

    // Priority values
    public static final String PRIORITY_HIGH   = "HIGH";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_LOW    = "LOW";

    // Status values
    public static final String STATUS_PENDING     = "PENDING";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_DONE        = "DONE";
}
