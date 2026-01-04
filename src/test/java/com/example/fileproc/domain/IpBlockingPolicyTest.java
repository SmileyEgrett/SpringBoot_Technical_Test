package com.example.fileproc.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IpBlockingPolicyTest {

    private IpBlockingPolicy policy;

    @BeforeEach
    void setUp() {
        Set<String> blockedCountries = Set.of("CN", "ES", "US");
        Set<String> blockedIsps = Set.of("AWS", "GCP", "AZURE");
        policy = new IpBlockingPolicy(blockedCountries, blockedIsps);
    }

    @Test
    void evaluate_withAllowedCountryAndIsp_returnsAllow() {
        IpInfo ipInfo = new IpInfo("success", null, "United Kingdom", "GB", "BT", "8.8.8.8");

        BlockingResult result = policy.evaluate(ipInfo);

        assertFalse(result.blocked());
        assertNull(result.reason());
    }

    @Test
    void evaluate_withBlockedCountryUs_returnsDeny() {
        IpInfo ipInfo = new IpInfo("success", null, "United States", "US", "Comcast", "1.2.3.4");

        BlockingResult result = policy.evaluate(ipInfo);

        assertTrue(result.blocked());
        assertTrue(result.reason().contains("Blocked country"));
    }

    @Test
    void evaluate_withBlockedCountryChina_returnsDeny() {
        IpInfo ipInfo = new IpInfo("success", null, "China", "CN", "China Telecom", "1.2.3.4");

        BlockingResult result = policy.evaluate(ipInfo);

        assertTrue(result.blocked());
        assertTrue(result.reason().contains("Blocked country"));
    }

    @Test
    void evaluate_withBlockedCountrySpain_returnsDeny() {
        IpInfo ipInfo = new IpInfo("success", null, "Spain", "ES", "Telefonica", "1.2.3.4");

        BlockingResult result = policy.evaluate(ipInfo);

        assertTrue(result.blocked());
        assertTrue(result.reason().contains("Blocked country"));
    }

    @Test
    void evaluate_withAwsIsp_returnsDeny() {
        IpInfo ipInfo = new IpInfo("success", null, "Germany", "DE", "Amazon AWS", "1.2.3.4");

        BlockingResult result = policy.evaluate(ipInfo);

        assertTrue(result.blocked());
        assertTrue(result.reason().contains("Blocked ISP"));
    }

    @Test
    void evaluate_withGcpIsp_returnsDeny() {
        IpInfo ipInfo = new IpInfo("success", null, "Germany", "DE", "Google Cloud GCP", "1.2.3.4");

        BlockingResult result = policy.evaluate(ipInfo);

        assertTrue(result.blocked());
        assertTrue(result.reason().contains("Blocked ISP"));
    }

    @Test
    void evaluate_withAzureIsp_returnsDeny() {
        IpInfo ipInfo = new IpInfo("success", null, "Germany", "DE", "Microsoft Azure", "1.2.3.4");

        BlockingResult result = policy.evaluate(ipInfo);

        assertTrue(result.blocked());
        assertTrue(result.reason().contains("Blocked ISP"));
    }

    @Test
    void evaluate_withNullIpInfo_returnsDeny() {
        BlockingResult result = policy.evaluate(null);

        assertTrue(result.blocked());
        assertTrue(result.reason().contains("Unable to validate IP"));
    }

    @Test
    void evaluate_withFailedLookup_returnsDeny() {
        IpInfo ipInfo = new IpInfo("fail", "private range", null, null, null, "192.168.1.1");

        BlockingResult result = policy.evaluate(ipInfo);

        assertTrue(result.blocked());
        assertTrue(result.reason().contains("Unable to validate IP"));
    }
}
