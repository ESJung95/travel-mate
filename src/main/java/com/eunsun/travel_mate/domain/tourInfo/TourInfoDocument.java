package com.eunsun.travel_mate.domain.tourInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "tour_info")
@Getter
@Setter
@NoArgsConstructor
public class TourInfoDocument {

  @Id
  private String id;

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

  @Field(type = FieldType.Date)
  private String createdTime;

  @Field(type = FieldType.Date)
  private String modifiedTime;

}
