package dev.gmpark.bankbackend.results;

public enum TransactionResult {
    SUCCESS,
    FAILURE,
    FAILURE_INVALID_ACCOUNT,
    FAILURE_INVALID_TO_ACCOUNT,
    FAILURE_INVALID_PASSWORD,
    FAILURE_INSUFFICIENT_BALANCE
}
