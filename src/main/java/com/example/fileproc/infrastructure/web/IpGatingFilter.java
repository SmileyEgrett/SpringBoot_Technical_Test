package com.example.fileproc.infrastructure.web;

import com.example.fileproc.api.ErrorResponse;
import com.example.fileproc.application.ClientIpResolver;
import com.example.fileproc.application.IpInfoService;
import com.example.fileproc.application.RequestAuditLogger;
import com.example.fileproc.config.IpBlockingProperties;
import com.example.fileproc.domain.BlockingResult;
import com.example.fileproc.domain.IpBlockingPolicy;
import com.example.fileproc.domain.IpInfo;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class IpGatingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(IpGatingFilter.class);
    private static final String OUTCOME_PATH = "/api/v1/outcome-file";

    private final ClientIpResolver clientIpResolver;
    private final IpInfoService ipInfoService;
    private final IpBlockingPolicy ipBlockingPolicy;
    private final RequestAuditLogger requestAuditLogger;
    private final ObjectMapper objectMapper;
    private final IpBlockingProperties ipBlockingProperties;

    public IpGatingFilter(ClientIpResolver clientIpResolver,
                          IpInfoService ipInfoService,
                          IpBlockingPolicy ipBlockingPolicy,
                          RequestAuditLogger requestAuditLogger,
                          ObjectMapper objectMapper,
                          IpBlockingProperties ipBlockingProperties) {
        this.clientIpResolver = clientIpResolver;
        this.ipInfoService = ipInfoService;
        this.ipBlockingPolicy = ipBlockingPolicy;
        this.requestAuditLogger = requestAuditLogger;
        this.objectMapper = objectMapper;
        this.ipBlockingProperties = ipBlockingProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !(OUTCOME_PATH.equals(request.getRequestURI())
                && "POST".equalsIgnoreCase(request.getMethod()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        UUID requestId = RequestIdFilter.getRequestId(request);
        if (requestId == null) {
            requestId = UUID.randomUUID();
        }

        Instant requestTimestamp = Instant.now();
        long startNanos = System.nanoTime();

        String clientIp = clientIpResolver.resolve(request, ipBlockingProperties.isTrustProxyHeaders());
        int responseCode = HttpStatus.OK.value();
        String countryCode = null;
        String ipProvider = null;

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            if (!ipBlockingProperties.isEnabled()) {
                filterChain.doFilter(request, responseWrapper);
                responseCode = responseWrapper.getStatus();
                return;
            }

            Optional<IpInfo> ipInfoOpt = ipInfoService.lookup(clientIp);
            if (ipInfoOpt.isPresent()) {
                IpInfo ipInfo = ipInfoOpt.get();
                countryCode = ipInfo.countryCode();
                ipProvider = ipInfo.isp();
            }

            BlockingResult result = ipBlockingPolicy.evaluate(ipInfoOpt.orElse(null));

            if (result.blocked()) {
                responseCode = HttpStatus.FORBIDDEN.value();
                writeForbiddenResponse(responseWrapper, requestId, result.reason());
            } else {
                filterChain.doFilter(request, responseWrapper);
                responseCode = responseWrapper.getStatus();
            }
        } catch (Exception e) {
            log.error("Error processing request", e);
            responseCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            throw e;
        } finally {
            long timeLapsedMs = (System.nanoTime() - startNanos) / 1_000_000;
            requestAuditLogger.log(requestId, getFullRequestUri(request), requestTimestamp,
                    responseCode, clientIp, countryCode, ipProvider, timeLapsedMs);
            responseWrapper.copyBodyToResponse();
        }
    }

    private String getFullRequestUri(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder(request.getRequestURL());
        String qs = request.getQueryString();
        if (qs != null && !qs.isBlank()) {
            sb.append('?').append(qs);
        }
        return sb.toString();
    }

    private void writeForbiddenResponse(HttpServletResponse response, UUID requestId, String reason) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorBody = new ErrorResponse(
                requestId.toString(),
                "FORBIDDEN",
                reason
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}
