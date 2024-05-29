package com.eunsun.travel_mate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_social")
public class UserSocial extends Base {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userSocialId;

  @ManyToOne (fetch = FetchType.LAZY) // 지연 로딩
  @JoinColumn(name = "user_id", nullable = false)
  private User user; // FK

  @Column(nullable = false)
  private String socialProvider;

  @Column(nullable = false)
  private String socialId;

}
