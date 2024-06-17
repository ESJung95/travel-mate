package com.eunsun.travel_mate.domain.tourInfo;

import com.eunsun.travel_mate.domain.AreaCode;
import com.eunsun.travel_mate.domain.Base;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "tour_info")
public class TourInfo extends Base {

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

  public TourInfo update(TourInfo newTourInfo) {
    return TourInfo.builder()
        .tourInfoId(this.tourInfoId)
        .areaCode(newTourInfo.getAreaCode())
        .sigunguCode(newTourInfo.getSigunguCode())
        .title(newTourInfo.getTitle())
        .addr1(newTourInfo.getAddr1())
        .addr2(newTourInfo.getAddr2())
        .contentTypeId(newTourInfo.getContentTypeId())
        .contentId(newTourInfo.getContentId())
        .mapx(newTourInfo.getMapx())
        .mapy(newTourInfo.getMapy())
        .imageUrl1(newTourInfo.getImageUrl1())
        .imageUrl2(newTourInfo.getImageUrl2())
        .createdTime(this.createdTime)
        .modifiedTime(newTourInfo.getModifiedTime())
        .build();
  }
}
