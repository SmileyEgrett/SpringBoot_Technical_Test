package com.example.fileproc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties(prefix = "ip-blocking")
public class IpBlockingProperties {

    private boolean enabled = true;
    private Set<String> blockedCountries = Set.of();
    private Set<String> blockedIsps = Set.of();
    private boolean trustProxyHeaders = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getBlockedCountries() {
        return blockedCountries;
    }

    public void setBlockedCountries(Set<String> blockedCountries) {
        this.blockedCountries = Set.copyOf(blockedCountries.stream()
                .map(String::toUpperCase)
                .toList());
    }

    public Set<String> getBlockedIsps() {
        return blockedIsps;
    }

    public void setBlockedIsps(Set<String> blockedIsps) {
        this.blockedIsps = Set.copyOf(blockedIsps.stream()
                .map(String::toUpperCase)
                .toList());
    }

    public boolean isTrustProxyHeaders() {
        return trustProxyHeaders;
    }

    public void setTrustProxyHeaders(boolean trustProxyHeaders) {
        this.trustProxyHeaders = trustProxyHeaders;
    }
}
