package org.sopt.global.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

public final class BearerTokenResolver {

    private static final String BEARER_PREFIX = "Bearer ";

    private BearerTokenResolver() {
    }

    public static Optional<String> resolve(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }
        return Optional.of(header.substring(BEARER_PREFIX.length()).trim());
    }
}
