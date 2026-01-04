package com.example.fileproc.application;

import java.time.Instant;
import java.util.UUID;

public interface RequestAuditLogger {

    void log(UUID requestId, String requestUri, Instant requestTimestamp,
             int httpResponseCode, String requestIpAddress,
             String requestCountryCode, String requestIpProvider,
             long timeLapsedMs);
}
