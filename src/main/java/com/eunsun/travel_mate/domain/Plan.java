package com.eunsun.travel_mate.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "plan")
public class Plan extends Base {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long planId;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "plan", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<PlanDetail> planDetails = new ArrayList<>();

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private LocalDate startDate; // 여행 시작 날짜

  @Column(nullable = false)
  private LocalDate endDate; // 여행 끝나는 날짜

}
