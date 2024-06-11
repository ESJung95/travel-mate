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
        .areaCode(tourInfo.getAreaCode().getCode())
        .sigunguCode(tourInfo.getSigunguCode())
        .title(tourInfo.getTitle())
        .addr1(tourInfo.getAddr1())
        .addr2(tourInfo.getAddr2())
        .contentTypeId(tourInfo.getContentTypeId())
        .contentId(tourInfo.getContentId())
        .mapx(Double.parseDouble(tourInfo.getMapx()))
        .mapy(Double.parseDouble(tourInfo.getMapy()))
        .imageUrl1(tourInfo.getImageUrl1())
        .imageUrl2(tourInfo.getImageUrl2())
        .createdTime(tourInfo.getCreatedTime())
        .modifiedTime(tourInfo.getModifiedTime())
        .build();
  }
}