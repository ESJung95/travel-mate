package com.eunsun.travel_mate.dto.response;

import com.eunsun.travel_mate.domain.PlanDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePlanDetailResponseDto {
  private Long planDetailId;
  private String message;

  public static CreatePlanDetailResponseDto from(PlanDetail planDetail) {
    return CreatePlanDetailResponseDto.builder()
        .planDetailId(planDetail.getPlanDetailId())
        .message("여행 상제 일정표 생성 성공")
        .build();
  }
}
