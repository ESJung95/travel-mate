package com.eunsun.travel_mate.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  // 클라이언트로부터 전송된 JWT 토큰을 검증하고, 해당 토큰이 유효한 경우에만 인증을 허용

  public static final String TOKEN_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  // 필터가 실제로 HTTP 요청을 필터링하는데 사용
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    log.info("JwtAuthenticationFilter - doFilterInternal 호출");
    String token = resolveToken(request);

    if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
      Authentication authentication = jwtTokenProvider.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }

  // HTTP 요청에서 JWT 토큰을 추출하는 역할
  private String resolveToken(HttpServletRequest request) {
    String token = request.getHeader(TOKEN_HEADER);

    if (!StringUtils.hasText(token) || !token.startsWith(TOKEN_PREFIX)) {
      return null;
    }

    return token.substring(TOKEN_PREFIX.length());
  }
}