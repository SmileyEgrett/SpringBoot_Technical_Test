package com.example.fileproc.infrastructure.persistence;

import com.example.fileproc.application.RequestAuditLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class JpaRequestAuditLogger implements RequestAuditLogger {

    private static final Logger log = LoggerFactory.getLogger(JpaRequestAuditLogger.class);

    private final RequestLogRepository requestLogRepository;

    public JpaRequestAuditLogger(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    @Override
    public void log(UUID requestId, String requestUri, Instant requestTimestamp,
                    int httpResponseCode, String requestIpAddress,
                    String requestCountryCode, String requestIpProvider,
                    long timeLapsedMs) {
        try {
            RequestLogEntity entity = new RequestLogEntity(
                    requestId, requestUri, requestTimestamp, httpResponseCode,
                    requestIpAddress, requestCountryCode, requestIpProvider, timeLapsedMs
            );
            requestLogRepository.save(entity);
        } catch (Exception e) {
            log.warn("Failed to save request log: {}", e.getMessage());
        }
    }
}
