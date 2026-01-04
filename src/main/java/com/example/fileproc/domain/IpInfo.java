package com.example.fileproc.domain;

public record IpInfo(
        String status,
        String message,
        String country,
        String countryCode,
        String isp,
        String query
) {
    public boolean isSuccess() {
        return "success".equals(status);
    }
}
