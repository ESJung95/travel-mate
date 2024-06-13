package com.eunsun.travel_mate.dto.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreatePlanRequest {

  private String title;

  // yyyy-MM-dd 형식으로
  private LocalDate startDate;
  private LocalDate endDate;

}
