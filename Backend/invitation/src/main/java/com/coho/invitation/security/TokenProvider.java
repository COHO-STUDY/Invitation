package com.coho.invitation.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class TokenProvider {
    private final String secretKey;
    private final long expireHours;
    private final String issuer;

    public TokenProvider(
            @Value("${secret-key}") String secretKey,
            @Value("${expire-hours}") long expireHours,
            @Value("${issuer}") String issuer) {
        this.secretKey = secretKey;
        this.expireHours = expireHours;
        this.issuer = issuer;
    }

    /* 토큰 생성하기 */
    public String createToken(String memberSpec) {
        return Jwts.builder()
                .signWith(new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName()))   // HS512 알고리즘을 사용하여 비밀키로 서명
                .setSubject(memberSpec)      // JWT 토큰 제목
                .setIssuer(issuer)      // JWT 토큰 발급자
                .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))    // JWT 토큰 발급 시간
                .setExpiration(Date.from(Instant.now().plus(expireHours, ChronoUnit.HOURS)))    // JWT 토큰 만료 시간
                .compact();     // JWT 토큰 생성
    }

    /* 토큰 정보로 사용자 권한 확인 */
    public String validateTokenAndGetSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
