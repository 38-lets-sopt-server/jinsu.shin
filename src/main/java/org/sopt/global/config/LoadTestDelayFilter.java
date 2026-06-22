package org.sopt.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Profile("loadtest")
public class LoadTestDelayFilter extends OncePerRequestFilter {

    private static final String TARGET_PREFIX = "/api/v1/posts";

    private final long delayMillis;

    public LoadTestDelayFilter(@Value("${loadtest.delay-ms:100}") long delayMillis) {
        this.delayMillis = delayMillis;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getRequestURI().startsWith(TARGET_PREFIX)) {
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        filterChain.doFilter(request, response);
    }
}
