package com.example.fileproc.api;

import java.util.List;

public record ErrorResponse(
        String requestId,
        String error,
        String reason,
        List<String> details
) {
    public ErrorResponse(String requestId, String error, String reason) {
        this(requestId, error, reason, null);
    }
}
