package com.eunsun.travel_mate.domain.tourInfo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "tour_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourInfoDocument {

  @Id
  private Long id;

  @Field(type = FieldType.Keyword)
  private String areaCodeName;

  @Field(type = FieldType.Keyword)
  private String areaCode;

  @Field(type = FieldType.Keyword)
  private String sigunguCode;

  @Field(type = FieldType.Text)
  private String title;

  @Field(type = FieldType.Text)
  private String addr1;

  @Field(type = FieldType.Text)
  private String addr2;

  @Field(type = FieldType.Keyword)
  private String contentTypeId;

  @Field(type = FieldType.Keyword)
  private String contentId;

  @Field(type = FieldType.Double)
  private Double mapx;

  @Field(type = FieldType.Double)
  private Double mapy;

  @Field(type = FieldType.Text)
  private String imageUrl1;

  @Field(type = FieldType.Text)
  private String imageUrl2;

  @Field(type = FieldType.Text)
  private String createdTime;

  @Field(type = FieldType.Text)
  private String modifiedTime;

  @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
  private LocalDateTime createdAt;

  @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
  private LocalDateTime updatedAt;

  public static TourInfoDocument from(TourInfo tourInfo) {
    return TourInfoDocument.builder()
        .id(tourInfo.getTourInfoId())
        .areaCode(String.valueOf(tourInfo.getAreaCode()))
        .areaCodeName(tourInfo.getAreaCode().getName())
        .sigunguCode(String.valueOf(tourInfo.getSigunguCode()))
        .title(String.valueOf(tourInfo.getTitle()))
        .addr1(String.valueOf(tourInfo.getAddr1()))
        .addr2(String.valueOf(tourInfo.getAddr2()))
        .contentTypeId(String.valueOf(tourInfo.getContentTypeId()))
        .contentId(String.valueOf(tourInfo.getContentId()))
        .mapx(Double.valueOf(tourInfo.getMapx()))
        .mapy(Double.valueOf(tourInfo.getMapy()))
        .imageUrl1(String.valueOf(tourInfo.getImageUrl1()))
        .imageUrl2(String.valueOf(tourInfo.getImageUrl2()))
        .createdTime(String.valueOf(tourInfo.getCreatedTime()))
        .modifiedTime(String.valueOf(tourInfo.getModifiedTime()))
        .createdAt(tourInfo.getCreatedAt())
        .updatedAt(tourInfo.getUpdatedAt())
        .build();
  }
}