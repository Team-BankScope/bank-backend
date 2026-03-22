package dev.gmpark.bankbackend.results;

public enum TaskResult implements Result {
    SUCCESS,
    FAILURE,
    FAILURE_TASK_IN_PROGRESS,
    FAILURE_SESSION
}
