package com.eunsun.travel_mate.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupDto {

  private String email;
  private String password;
  private String name;
  private LocalDate birthdate;
  private String verificationCode;


}
