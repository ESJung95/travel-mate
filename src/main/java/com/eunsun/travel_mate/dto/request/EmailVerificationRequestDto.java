package com.eunsun.travel_mate.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationRequestDto {

  private String email;
  private String verificationCode;

}
