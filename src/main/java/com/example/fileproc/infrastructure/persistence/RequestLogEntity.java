package com.example.fileproc.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "request_log")
public class RequestLogEntity {

    @Id
    @Column(name = "request_id")
    private UUID requestId;

    @Column(name = "request_uri")
    private String requestUri;

    @Column(name = "request_timestamp")
    private Instant requestTimestamp;

    @Column(name = "http_response_code")
    private int httpResponseCode;

    @Column(name = "request_ip_address")
    private String requestIpAddress;

    @Column(name = "request_country_code")
    private String requestCountryCode;

    @Column(name = "request_ip_provider")
    private String requestIpProvider;

    @Column(name = "time_lapsed_ms")
    private long timeLapsedMs;

    public RequestLogEntity() {
    }

    public RequestLogEntity(UUID requestId, String requestUri, Instant requestTimestamp,
                            int httpResponseCode, String requestIpAddress,
                            String requestCountryCode, String requestIpProvider,
                            long timeLapsedMs) {
        this.requestId = requestId;
        this.requestUri = requestUri;
        this.requestTimestamp = requestTimestamp;
        this.httpResponseCode = httpResponseCode;
        this.requestIpAddress = requestIpAddress;
        this.requestCountryCode = requestCountryCode;
        this.requestIpProvider = requestIpProvider;
        this.timeLapsedMs = timeLapsedMs;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public Instant getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(Instant requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    public void setHttpResponseCode(int httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
    }

    public String getRequestIpAddress() {
        return requestIpAddress;
    }

    public void setRequestIpAddress(String requestIpAddress) {
        this.requestIpAddress = requestIpAddress;
    }

    public String getRequestCountryCode() {
        return requestCountryCode;
    }

    public void setRequestCountryCode(String requestCountryCode) {
        this.requestCountryCode = requestCountryCode;
    }

    public String getRequestIpProvider() {
        return requestIpProvider;
    }

    public void setRequestIpProvider(String requestIpProvider) {
        this.requestIpProvider = requestIpProvider;
    }

    public long getTimeLapsedMs() {
        return timeLapsedMs;
    }

    public void setTimeLapsedMs(long timeLapsedMs) {
        this.timeLapsedMs = timeLapsedMs;
    }
}
