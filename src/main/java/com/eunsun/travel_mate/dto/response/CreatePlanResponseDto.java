package com.eunsun.travel_mate.dto.response;

import com.eunsun.travel_mate.domain.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePlanResponseDto {

  private Long planId;
  private String message;

  public static CreatePlanResponseDto from(Plan plan) {
    return CreatePlanResponseDto.builder()
        .planId(plan.getPlanId())
        .message("여행 일정표 생성 성공")
        .build();
  }
}
