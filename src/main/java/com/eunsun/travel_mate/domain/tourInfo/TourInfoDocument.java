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
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

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

  @GeoPointField
  private GeoPoint location;

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
        .areaCode(tourInfo.getAreaCode().getCode())
        .sigunguCode(tourInfo.getSigunguCode())
        .title(tourInfo.getTitle())
        .addr1(tourInfo.getAddr1())
        .addr2(tourInfo.getAddr2())
        .contentTypeId(tourInfo.getContentTypeId())
        .contentId(tourInfo.getContentId())
        .location(new GeoPoint(Double.parseDouble(tourInfo.getMapy()), Double.parseDouble(tourInfo.getMapx())))
        .createdTime(tourInfo.getCreatedTime())
        .modifiedTime(tourInfo.getModifiedTime())
        .createdAt(tourInfo.getCreatedAt())
        .updatedAt(tourInfo.getUpdatedAt())
        .build();
  }

}