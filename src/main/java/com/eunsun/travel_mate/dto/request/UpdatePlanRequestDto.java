package com.eunsun.travel_mate.dto.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePlanRequestDto {

  private String title;
  private LocalDate startDate;
  private LocalDate endDate;

}
