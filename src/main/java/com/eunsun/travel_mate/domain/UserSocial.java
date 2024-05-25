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
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_social")
public class UserSocial {

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

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
