package com.eunsun.travel_mate.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FindUserEmailRequestDto {

  @NotBlank(message = "이름은 필수 입력 값 입니다.")
  private String name;

  @NotNull(message = "생년월일은 필수 입력 값 입니다.")
  @Past(message = "생년월일을 확인해주세요.")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate birthdate;

}
