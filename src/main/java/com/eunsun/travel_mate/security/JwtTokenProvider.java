package com.eunsun.travel_mate.security;

import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.dto.response.TokenDetailDto;
import com.eunsun.travel_mate.repository.UserRepository;
import com.eunsun.travel_mate.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

  private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1H
  private static final String KEY_ROLE = "role";

  @Value("${jwt.secret.key}")
  private String secretKeyString;

  private Key secretKey;

  private final UserRepository userRepository;
  private final TokenBlacklistService tokenBlacklistService;

  // secretKey : String -> Key
  @PostConstruct
  public void init() {
    byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
    secretKey = new SecretKeySpec(keyBytes, "HmacSHA512");
  }

  // 토큰 생성
  public TokenDetailDto generateToken(long id, String role) {
    Claims claims = Jwts.claims().setSubject(String.valueOf(id));
    claims.put(KEY_ROLE, role);

    // id 값을 JWT ID로 설정
    claims.setId(String.valueOf(id));

    Date now = new Date();
    Date expiredTime = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expiredTime)
        .signWith(secretKey, SignatureAlgorithm.HS512)
        .compact();

    LocalDateTime loginTime = LocalDateTime.ofInstant(now.toInstant(), ZoneId.systemDefault());
    LocalDateTime tokenExpiryTime = LocalDateTime.ofInstant(expiredTime.toInstant(), ZoneId.systemDefault());

    return new TokenDetailDto(token, loginTime, tokenExpiryTime);
  }

  // JWT 유효성 검사
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);

      // 블랙리스트에 토큰이 존재하는지 확인
      return !tokenBlacklistService.isTokenBlacklisted(token);

    } catch (Exception e) {
      log.error("JWT 토큰 유효성 없음", e);
    }
    return false;
  }

  // JWT 토큰에서 인증 정보 -> Authentication 로 변환
  public Authentication getAuthentication(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();

    String userId = claims.getSubject();
    User user = userRepository.findById(Long.parseLong(userId))
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

    return new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
  }

  // JWT 토큰에서 JWT ID 추출
  public String extractJwtId(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token)
          .getBody();

      String jwtId = claims.getId();
      if (jwtId == null) {
        log.error("JWT ID is null");
        throw new IllegalArgumentException("JWT ID is null");
      }

      return jwtId;
    } catch (io.jsonwebtoken.JwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
      throw e;
    }
  }

  // JWT 토큰에서 만료 시간 추출
  public LocalDateTime getExpiredTime(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token)
          .getBody();

      Date expiration = claims.getExpiration();
      return LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
    } catch (io.jsonwebtoken.JwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
      throw e;
    }
  }
}