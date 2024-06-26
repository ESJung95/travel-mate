package com.eunsun.travel_mate.dto.response;

import com.eunsun.travel_mate.domain.PlanDetail;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePlanDetailResponseDto {

  private Long tourInfoId;
  private String tourInfoName;

  @JsonFormat(pattern = "HH:mm")
  private LocalTime startTime;

  @JsonFormat(pattern = "HH:mm")
  private LocalTime endTime;

  private String memo;

  public static UpdatePlanDetailResponseDto from(PlanDetail planDetail) {
    return UpdatePlanDetailResponseDto.builder()
        .tourInfoId(planDetail.getTourInfo().getTourInfoId())
        .tourInfoName(planDetail.getTourInfo().getTitle())
        .startTime(planDetail.getStartTime())
        .endTime(planDetail.getEndTime())
        .memo(planDetail.getMemo())
        .build();
  }
}
