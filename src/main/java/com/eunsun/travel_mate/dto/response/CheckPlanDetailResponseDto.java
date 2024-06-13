package com.eunsun.travel_mate.dto.response;

import com.eunsun.travel_mate.domain.PlanDetail;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckPlanDetailResponseDto {

  private Long planDetailId;
  private Long tourInfoId;
  private LocalTime startTime;
  private LocalTime endTime;
  private String memo;

  public static CheckPlanDetailResponseDto from(PlanDetail planDetail) {
    return CheckPlanDetailResponseDto.builder()
        .planDetailId(planDetail.getPlanDetailId())
        .tourInfoId(planDetail.getTourInfo().getTourInfoId())
        .startTime(planDetail.getStartTime())
        .endTime(planDetail.getEndTime())
        .memo(planDetail.getMemo())
        .build();
  }
}