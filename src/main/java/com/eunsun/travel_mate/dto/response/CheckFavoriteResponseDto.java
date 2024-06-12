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
public class CheckFavoriteResponseDto {

  private Long favoriteId;
  private Long tourInfoId;
  private String title;

  public static CheckFavoriteResponseDto from(Favorite favorite) {
    return CheckFavoriteResponseDto.builder()
        .favoriteId(favorite.getFavoriteId())
        .tourInfoId(favorite.getTourInfoId().getTourInfoId())
        .title(favorite.getTourInfoId().getTitle())
        .build();
  }
}
