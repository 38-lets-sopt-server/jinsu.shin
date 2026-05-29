package org.sopt.global.security;
import org.sopt.global.exception.BusinessException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.sopt.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final long accessTokenExpiresInSeconds;
    private final long refreshTokenExpiresInSeconds;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-expires-in-seconds:1800}") long accessTokenExpiresInSeconds,
            @Value("${security.jwt.refresh-token-expires-in-seconds:1209600}") long refreshTokenExpiresInSeconds
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.accessTokenExpiresInSeconds = accessTokenExpiresInSeconds;
        this.refreshTokenExpiresInSeconds = refreshTokenExpiresInSeconds;
    }

    public String generateAccessToken(Long userId, String email) {
        Instant now = Instant.now();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(String.valueOf(userId))
                .withClaim("email", email)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(accessTokenExpiresInSeconds)))
                .sign(algorithm);
    }

    public String generateRefreshToken(Long userId) {
        Instant now = Instant.now();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(String.valueOf(userId))
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(refreshTokenExpiresInSeconds)))
                .sign(algorithm);
    }

    public Long verifyAndGetUserId(String token) {
        if (token == null || token.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
        try {
            return Long.parseLong(jwt.getSubject());
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    // 인증 필터용: 한 번의 검증/디코드로 userId 와 jti 를 함께 추출한다.
    // 모든 인증 요청이 지나가는 핫패스이므로 토큰을 두 번 검증하지 않는다.
    public TokenPayload parse(String token) {
        if (token == null || token.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
        try {
            return new TokenPayload(Long.parseLong(jwt.getSubject()), jwt.getId());
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    public record TokenPayload(Long userId, String jti) {
    }

    // 로그아웃 시 블랙리스트 등록에 필요한 jti 와 잔여 TTL 을 한 번의 검증/디코드로 함께 추출한다.
    public BlacklistInfo getBlacklistInfo(String token) {
        DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
        long remaining = (jwt.getExpiresAt().getTime() - System.currentTimeMillis()) / 1000;
        return new BlacklistInfo(jwt.getId(), Math.max(0, remaining));
    }

    public record BlacklistInfo(String jti, long ttlSeconds) {
    }

    public long getRefreshTokenExpiresInSeconds() {
        return refreshTokenExpiresInSeconds;
    }
}
