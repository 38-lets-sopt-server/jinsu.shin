package org.sopt.global.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.exception.ErrorCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String JWT_ERROR_CODE_ATTR = "jwt-error-code";

    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final CustomAuthenticationEntryPoint entryPoint;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<String> tokenOpt = BearerTokenResolver.resolve(request);
        if (tokenOpt.isPresent()) {
            try {
                JwtService.TokenPayload payload = jwtService.parse(tokenOpt.get());
                if (tokenBlacklistService.isBlacklisted(payload.jti())) {
                    rejectAsUnauthorized(request, response, ErrorCode.BLACKLISTED_TOKEN);
                    return;
                }
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        String.valueOf(payload.userId()), null, Collections.emptyList());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (TokenExpiredException e) {
                rejectAsUnauthorized(request, response, ErrorCode.ACCESS_TOKEN_EXPIRED);
                return;
            } catch (BusinessException | JWTVerificationException e) {
                rejectAsUnauthorized(request, response, ErrorCode.INVALID_TOKEN);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void rejectAsUnauthorized(
            HttpServletRequest request,
            HttpServletResponse response,
            ErrorCode errorCode
    ) throws IOException, ServletException {
        request.setAttribute(JWT_ERROR_CODE_ATTR, errorCode);
        entryPoint.commence(request, response, new BadCredentialsException(errorCode.name()));
    }
}
