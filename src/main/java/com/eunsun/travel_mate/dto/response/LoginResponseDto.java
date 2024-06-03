package com.eunsun.travel_mate.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

  private String token;
  private Long userId;
  private String name;
  private String email;
  private String role;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime loginTime; // 로그인 시간

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime tokenExpiryTime; // 토큰 만료 시간


  // LoginResponseDto 생성
  public static LoginResponseDto createLoginResponse(
      String token,
      Long userId, String name, String email, String role,
      LocalDateTime loginTime, LocalDateTime tokenExpiryTime) {

    return LoginResponseDto.builder()
        .token(token)
        .userId(userId)
        .name(name)
        .email(email)
        .role(role)
        .loginTime(loginTime)
        .tokenExpiryTime(tokenExpiryTime)
        .build();
  }
}


