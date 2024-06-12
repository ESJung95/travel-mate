package com.eunsun.travel_mate.dto.response;

import com.eunsun.travel_mate.domain.Favorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponseDto {

  private Long tourInfoId;
  private String title;
  private String message;

  public static FavoriteResponseDto from(Favorite favorite) {
    return FavoriteResponseDto.builder()
        .tourInfoId(favorite.getTourInfoId().getTourInfoId())
        .title(favorite.getTourInfoId().getTitle())
        .message("관심 여행지가 성공적으로 저장되었습니다.")
        .build();
  }
}
