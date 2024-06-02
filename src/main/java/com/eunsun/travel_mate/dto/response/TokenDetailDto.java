package com.eunsun.travel_mate.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenDetailDto {

  private String token;
  private LocalDateTime loginTime;
  private LocalDateTime tokenExpiryTime;

}

