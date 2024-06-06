package com.eunsun.travel_mate.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNameUpdateRequestDto {

  @NotBlank(message = "이름은 필수 입력 값 입니다.")
  private String name;
}
