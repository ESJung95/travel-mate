package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.dto.request.FavoriteRequestDto;
import com.eunsun.travel_mate.dto.response.FavoriteResponseDto;
import com.eunsun.travel_mate.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/favorite")
public class FavoriteController {

  private final FavoriteService favoriteService;

  // 관심 여행지 추가
  @PostMapping
  public ResponseEntity<?> addFavorite(
      @RequestBody FavoriteRequestDto favoriteRequestDto,
      @AuthenticationPrincipal UserDetails userDetails) {

    try {
      String userId = userDetails.getUsername();
      FavoriteResponseDto favoriteResponse = favoriteService.addFavorite(userId, favoriteRequestDto);
      return ResponseEntity.ok(favoriteResponse);

    } catch (Exception e) {
      return ResponseEntity.badRequest().body("관심 여행지 추가 실패");
    }
  }

}
