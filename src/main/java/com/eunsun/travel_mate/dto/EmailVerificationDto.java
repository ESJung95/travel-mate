package com.eunsun.travel_mate.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationDto {

  private String email;
  private String verificationCode;

}
