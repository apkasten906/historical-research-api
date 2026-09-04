package com.historicalrpg.research.exception;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        Map<String, String> validationErrors) {

    public static ApiErrorResponse of(int status, String error) {
        return new ApiErrorResponse(Instant.now(), status, error, Map.of());
    }

    public static ApiErrorResponse validation(Map<String, String> validationErrors) {
        return new ApiErrorResponse(Instant.now(), 400, "Validation failed", validationErrors);
    }
}
