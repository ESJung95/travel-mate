package com.eunsun.travel_mate.domain.tourInfo;

import com.eunsun.travel_mate.domain.AreaCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tour_info")
public class TourInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long tourInfoId;

  @ManyToOne
  @JoinColumn(name = "area_code_id")
  private AreaCode areaCode;

  @Column(nullable = false)
  private String sigunguCode;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String addr1;

  private String addr2;

  @Column(nullable = false)
  private String contentTypeId;

  @Column(nullable = false)
  private String contentId;

  @Column(nullable = false)
  private String mapx;

  @Column(nullable = false)
  private String mapy;

  private String imageUrl1;

  private String imageUrl2;

  @Column(nullable = false)
  private String createdTime;

  @Column(nullable = false)
  private String modifiedTime;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

}
