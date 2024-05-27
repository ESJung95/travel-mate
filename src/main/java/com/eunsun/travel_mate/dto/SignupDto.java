package com.eunsun.travel_mate.dto;

import com.eunsun.travel_mate.domain.User;
import java.time.LocalDate;
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
public class SignupDto {

  private String email;
  private String password;
  private String name;
  private LocalDate birthdate;
  private String verificationCode;


  public static User toEntity(SignupDto signupDto, String encodedPassword) {
    return User.builder()
        .email(signupDto.getEmail())
        .password(encodedPassword)
        .name(signupDto.getName())
        .birthdate(signupDto.getBirthdate())
        .build();
  }
}
