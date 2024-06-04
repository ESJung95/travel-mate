package com.eunsun.travel_mate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FindUserEmailResponseDto {

  private String email;
  private String name;


  // FindUserEmailResponseDto 생성
  public static FindUserEmailResponseDto createFindUserEmailResponse(String name, String email) {

    return FindUserEmailResponseDto.builder()
        .email(email)
        .name(name)
        .build();
  }

}
