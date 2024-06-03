package com.eunsun.travel_mate.dto.response;

import com.eunsun.travel_mate.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupResponseDto {

  private String email;
  private String name;

  // UserEntity -> SignupResponseDto
  public static SignupResponseDto toSignupResponseDto(User user) {
    SignupResponseDto signupResponseDto = new SignupResponseDto();
    signupResponseDto.setEmail(user.getEmail());
    signupResponseDto.setName(user.getName());
    return signupResponseDto;
  }
}