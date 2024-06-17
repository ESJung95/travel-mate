package com.eunsun.travel_mate.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // JWT 인증을 사용하는 경우 - csrf 보호 비활성화
        .csrf(AbstractHttpConfigurer::disable)

        // cors 설정 비활성화
        .cors(AbstractHttpConfigurer::disable)

        // httpBasic 비활성화
        .httpBasic(AbstractHttpConfigurer::disable)

        // session 설정
        .sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        )
        .authorizeHttpRequests(authHttpRequest -> authHttpRequest
            // 인증 없이 허용
            .requestMatchers("/swagger-ui/index.html").permitAll()
            .requestMatchers("/user/signup", "/user/check-email", "/user/verify-email", "/user/send-verification-code").permitAll()
            .requestMatchers("/user/login", "/user/logout", "/user/find/email", "/user/find/password").permitAll()
            .requestMatchers("/area-code", "/tour").permitAll()
            .requestMatchers("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**").permitAll()

            // 나머지는 인증 필요
            .anyRequest().authenticated())

        // JWT 필터 추가
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

}

