package com.opdev.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class HttpRequestLoggingFilter implements Filter {

    private static final String REQUEST_ID = "requestId";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String uuid = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID, uuid);
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        LOGGER.info(
                "Before request: {} {}, clientIP: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                servletRequest.getRemoteAddr()
        );

        filterChain.doFilter(servletRequest, servletResponse);

        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        LOGGER.info(
                "After request: {} {}, clientIP: {}, status: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                servletRequest.getRemoteAddr(),
                httpServletResponse.getStatus()
        );

        MDC.remove(REQUEST_ID);
    }
}
