package com.example.fileproc.application;

import com.google.common.net.InetAddresses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientIpResolver {

    public String resolve(HttpServletRequest request, boolean trustProxyHeaders) {
        if (trustProxyHeaders) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                String candidate = xForwardedFor.split(",")[0].trim();
                if (isValidIpAddress(candidate)) {
                    return candidate;
                }
            }
        }
        return request.getRemoteAddr();
    }

    public boolean isValidIpAddress(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return InetAddresses.isInetAddress(ip);
    }

    public Optional<String> validateAndNormalize(String ip) {
        if (ip == null) {
            return Optional.empty();
        }
        String trimmed = ip.trim();
        if (isValidIpAddress(trimmed)) {
            return Optional.of(trimmed);
        }
        return Optional.empty();
    }
}
