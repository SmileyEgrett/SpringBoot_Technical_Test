package com.example.fileproc.application;

import com.example.fileproc.domain.IpInfo;
import com.example.fileproc.infrastructure.ip.IpApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IpInfoService {

    private static final Logger log = LoggerFactory.getLogger(IpInfoService.class);

    private final IpApiClient ipApiClient;
    private final ClientIpResolver clientIpResolver;

    public IpInfoService(IpApiClient ipApiClient, ClientIpResolver clientIpResolver) {
        this.ipApiClient = ipApiClient;
        this.clientIpResolver = clientIpResolver;
    }

    public Optional<IpInfo> lookup(String ip) {
        if (!clientIpResolver.isValidIpAddress(ip)) {
            log.warn("Invalid IP address format: {}", ip);
            return Optional.empty();
        }

        try {
            IpInfo info = ipApiClient.lookup(ip);
            if (info == null) {
                log.warn("IP lookup returned null response for {}", ip);
                return Optional.empty();
            }
            return Optional.of(info);
        } catch (Exception e) {
            log.warn("Failed to lookup IP info for {}: {}", ip, e.getMessage());
            return Optional.empty();
        }
    }
}
