package com.donut.swaipe.global.exception.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ErrorTrackingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		    throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            MDC.put("errorId", UUID.randomUUID().toString());
            MDC.put("path", ((HttpServletRequest) request).getRequestURI());
            log.error("Error occurred in filter chain", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}