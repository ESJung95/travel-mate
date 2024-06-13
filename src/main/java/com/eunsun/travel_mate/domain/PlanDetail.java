package com.eunsun.travel_mate.domain;

import com.eunsun.travel_mate.domain.tourInfo.TourInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
  private LocalTime startTime;

  @Column(nullable = false)
  private LocalTime endTime;

  private String memo;

}
