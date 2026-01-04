package com.example.fileproc.application;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientIpResolverTest {

    private ClientIpResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new ClientIpResolver();
    }

    @Test
    void isValidIpAddress_withValidIpv4_returnsTrue() {
        assertTrue(resolver.isValidIpAddress("192.168.1.1"));
        assertTrue(resolver.isValidIpAddress("8.8.8.8"));
        assertTrue(resolver.isValidIpAddress("255.255.255.255"));
        assertTrue(resolver.isValidIpAddress("0.0.0.0"));
    }

    @Test
    void isValidIpAddress_withValidIpv6_returnsTrue() {
        assertTrue(resolver.isValidIpAddress("::1"));
        assertTrue(resolver.isValidIpAddress("2001:db8::1"));
        assertTrue(resolver.isValidIpAddress("fe80::1"));
        assertTrue(resolver.isValidIpAddress("2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
    }

    @Test
    void isValidIpAddress_withInvalidInput_returnsFalse() {
        assertFalse(resolver.isValidIpAddress(null));
        assertFalse(resolver.isValidIpAddress(""));
        assertFalse(resolver.isValidIpAddress("not-an-ip"));
        assertFalse(resolver.isValidIpAddress("256.1.1.1"));
        assertFalse(resolver.isValidIpAddress("192.168.1"));
        assertFalse(resolver.isValidIpAddress("example.com"));
    }

    @Test
    void resolve_withTrustProxyHeadersTrue_extractsFromXForwardedFor() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.50");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");

        String result = resolver.resolve(request, true);

        assertEquals("203.0.113.50", result);
    }

    @Test
    void resolve_withMultipleIpsInXForwardedFor_extractsFirstIp() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.50, 70.41.3.18, 150.172.238.178");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");

        String result = resolver.resolve(request, true);

        assertEquals("203.0.113.50", result);
    }

    @Test
    void resolve_withInvalidXForwardedFor_fallsBackToRemoteAddr() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("not-valid-ip");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");

        String result = resolver.resolve(request, true);

        assertEquals("10.0.0.1", result);
    }

    @Test
    void resolve_withEmptyXForwardedFor_fallsBackToRemoteAddr() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");

        String result = resolver.resolve(request, true);

        assertEquals("10.0.0.1", result);
    }

    @Test
    void resolve_withTrustProxyHeadersFalse_usesRemoteAddr() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.50");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");

        String result = resolver.resolve(request, false);

        assertEquals("10.0.0.1", result);
        verify(request, never()).getHeader("X-Forwarded-For");
    }

    @Test
    void validateAndNormalize_withValidIp_returnsOptionalWithIp() {
        Optional<String> result = resolver.validateAndNormalize("8.8.8.8");

        assertTrue(result.isPresent());
        assertEquals("8.8.8.8", result.get());
    }

    @Test
    void validateAndNormalize_withWhitespaceAroundIp_returnsTrimmedIp() {
        Optional<String> result = resolver.validateAndNormalize("  8.8.8.8  ");

        assertTrue(result.isPresent());
        assertEquals("8.8.8.8", result.get());
    }

    @Test
    void validateAndNormalize_withInvalidIp_returnsEmpty() {
        Optional<String> result = resolver.validateAndNormalize("invalid");

        assertTrue(result.isEmpty());
    }

    @Test
    void validateAndNormalize_withNull_returnsEmpty() {
        Optional<String> result = resolver.validateAndNormalize(null);

        assertTrue(result.isEmpty());
    }
}
