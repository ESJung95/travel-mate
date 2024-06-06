package com.eunsun.travel_mate.dto.response;

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
public class UserResponseDto {

  private String email;
  private String name;
  private LocalDate birthdate;

  // UserResponseDto 생성
  public static UserResponseDto createUserResponse(String email, String name, LocalDate birthdate) {

    return UserResponseDto.builder()
        .email(email)
        .name(name)
        .birthdate(birthdate)
        .build();
  }
}
