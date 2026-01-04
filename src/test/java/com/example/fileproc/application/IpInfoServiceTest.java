package com.example.fileproc.application;

import com.example.fileproc.domain.IpInfo;
import com.example.fileproc.infrastructure.ip.IpApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IpInfoServiceTest {

    private IpApiClient ipApiClient;
    private ClientIpResolver clientIpResolver;
    private IpInfoService service;

    @BeforeEach
    void setUp() {
        ipApiClient = mock(IpApiClient.class);
        clientIpResolver = mock(ClientIpResolver.class);
        service = new IpInfoService(ipApiClient, clientIpResolver);
    }

    @Test
    void lookup_withValidIpAndSuccessfulResponse_returnsIpInfo() {
        String ip = "8.8.8.8";
        IpInfo expectedInfo = new IpInfo("success", null, "United States", "US", "Google", ip);

        when(clientIpResolver.isValidIpAddress(ip)).thenReturn(true);
        when(ipApiClient.lookup(ip)).thenReturn(expectedInfo);

        Optional<IpInfo> result = service.lookup(ip);

        assertTrue(result.isPresent());
        assertEquals(expectedInfo, result.get());
    }

    @Test
    void lookup_withValidIpAndFailResponse_stillReturnsIpInfo() {
        String ip = "192.168.1.1";
        IpInfo failInfo = new IpInfo("fail", "private range", null, null, null, ip);

        when(clientIpResolver.isValidIpAddress(ip)).thenReturn(true);
        when(ipApiClient.lookup(ip)).thenReturn(failInfo);

        Optional<IpInfo> result = service.lookup(ip);

        assertTrue(result.isPresent());
        assertEquals("fail", result.get().status());
    }

    @Test
    void lookup_withInvalidIp_returnsEmpty() {
        String ip = "not-an-ip";

        when(clientIpResolver.isValidIpAddress(ip)).thenReturn(false);

        Optional<IpInfo> result = service.lookup(ip);

        assertTrue(result.isEmpty());
        verify(ipApiClient, never()).lookup(any());
    }

    @Test
    void lookup_withNullResponseFromClient_returnsEmpty() {
        String ip = "8.8.8.8";

        when(clientIpResolver.isValidIpAddress(ip)).thenReturn(true);
        when(ipApiClient.lookup(ip)).thenReturn(null);

        Optional<IpInfo> result = service.lookup(ip);

        assertTrue(result.isEmpty());
    }

    @Test
    void lookup_withClientException_returnsEmpty() {
        String ip = "8.8.8.8";

        when(clientIpResolver.isValidIpAddress(ip)).thenReturn(true);
        when(ipApiClient.lookup(ip)).thenThrow(new RuntimeException("Connection timeout"));

        Optional<IpInfo> result = service.lookup(ip);

        assertTrue(result.isEmpty());
    }
}
