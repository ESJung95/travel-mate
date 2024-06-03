package com.eunsun.travel_mate.dto.request;

import com.eunsun.travel_mate.domain.TokenBlacklist;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenBlacklistRequestDto {
  private String token;
  private LocalDateTime expiredTime;

  // TokenBlacklistRequestDto -> Entity 변환
  public static TokenBlacklist toEntity(String token, LocalDateTime expiredTime) {
    return TokenBlacklist.builder()
        .token(token)
        .expiredTime(expiredTime)
        .build();
  }
}
