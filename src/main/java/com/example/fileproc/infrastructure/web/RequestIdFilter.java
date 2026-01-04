package com.example.fileproc.infrastructure.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID_ATTR = "requestId";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        UUID requestId = UUID.randomUUID();
        request.setAttribute(REQUEST_ID_ATTR, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId.toString());
        filterChain.doFilter(request, response);
    }

    public static UUID getRequestId(HttpServletRequest request) {
        Object val = request.getAttribute(REQUEST_ID_ATTR);
        return (val instanceof UUID uuid) ? uuid : null;
    }
}
