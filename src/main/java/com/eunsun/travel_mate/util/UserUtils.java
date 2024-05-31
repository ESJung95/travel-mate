package com.eunsun.travel_mate.util;

import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.dto.request.SignupRequestDto;
import com.eunsun.travel_mate.dto.response.LoginResponseDto;
import com.eunsun.travel_mate.dto.response.SignupResponseDto;

public class UserUtils {

  // SignupRequestDto -> UserEntity
  public static User toEntity(SignupRequestDto signupRequestDto, String encodedPassword) {
    return User.builder()
        .email(signupRequestDto.getEmail())
        .password(encodedPassword)
        .name(signupRequestDto.getName())
        .birthdate(signupRequestDto.getBirthdate())
        .build();
  }

  // UserEntity -> SignupResponseDto
  public static SignupResponseDto toSignupResponseDto(User user) {
    SignupResponseDto signupResponseDto = new SignupResponseDto();
    signupResponseDto.setEmail(user.getEmail()); // 사용자 아이디 또는 이메일 설정
    signupResponseDto.setName(user.getName()); // 사용자 이름 설정
    return signupResponseDto;
  }

  // LoginResponseDto 생성
  public static LoginResponseDto createLoginResponse(String token, String name) {
    return LoginResponseDto.builder()
        .token(token)
        .name(name)
        .build();
  }

}
