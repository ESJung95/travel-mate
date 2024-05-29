package com.eunsun.travel_mate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "area_code")
public class AreaCode extends Base {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long areaCodeId;

  @Column(unique = true, nullable = false)
  private String code;

  @Column(nullable = false)
  private String name;

}
