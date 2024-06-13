package com.eunsun.travel_mate.dto.response;

import com.eunsun.travel_mate.domain.Plan;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePlanResponseDto {

  private Long planId;
  private String title;
  private LocalDate startDate;
  private LocalDate endDate;

  public static UpdatePlanResponseDto from(Plan plan) {
    return UpdatePlanResponseDto.builder()
        .planId(plan.getPlanId())
        .title(plan.getTitle())
        .startDate(plan.getStartDate())
        .endDate(plan.getEndDate())
        .build();
  }
}
