package com.eunsun.travel_mate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "plan_detail")
public class PlanDetail extends Base {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long planDetailId;

  @ManyToOne
  @JoinColumn(name = "plan_id", nullable = false)
  private Plan plan;

  @ManyToOne
  @JoinColumn(name = "tour_info_id", nullable = false)
  private TourInfo tourInfo;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  private LocalDate endDate;

  @Column(nullable = false)
  private LocalTime startTime;

  @Column(nullable = false)
  private LocalTime endTime;

  private String memo;

}
