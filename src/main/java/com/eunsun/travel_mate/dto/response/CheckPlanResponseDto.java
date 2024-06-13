package com.eunsun.travel_mate.dto.response;

import com.eunsun.travel_mate.domain.Plan;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckPlanResponseDto {

  private Long planId;
  private String title;
  private LocalDate startDate;
  private LocalDate endDate;
  private List<CheckPlanDetailResponseDto> planDetails;

  public static CheckPlanResponseDto from(Plan plan) {
    List<CheckPlanDetailResponseDto> checkPlanDetailResponseDto = plan.getPlanDetails().stream()
        .map(CheckPlanDetailResponseDto::from)
        .sorted(Comparator.comparing(CheckPlanDetailResponseDto::getStartTime))
        .collect(Collectors.toList());

    return CheckPlanResponseDto.builder()
        .planId(plan.getPlanId())
        .title(plan.getTitle())
        .startDate(plan.getStartDate())
        .endDate(plan.getEndDate())
        .planDetails(checkPlanDetailResponseDto)
        .build();
  }
}