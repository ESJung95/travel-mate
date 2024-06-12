package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.dto.request.CreateFavoriteRequestDto;
import com.eunsun.travel_mate.dto.response.CheckFavoriteResponseDto;
import com.eunsun.travel_mate.dto.response.CreateFavoriteResponseDto;
import com.eunsun.travel_mate.service.FavoriteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
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
      @RequestBody CreateFavoriteRequestDto createFavoriteRequestDto,
      @AuthenticationPrincipal UserDetails userDetails) {

    try {
      String userId = userDetails.getUsername();
      CreateFavoriteResponseDto favoriteResponse = favoriteService.addFavorite(userId,
          createFavoriteRequestDto);
      return ResponseEntity.ok(favoriteResponse);

    } catch (Exception e) {
      return ResponseEntity.badRequest().body("관심 여행지 추가 실패");
    }
  }

  // 관심 여행지 목록 전체 조회
  @GetMapping
  public ResponseEntity<?> getFavorites(@AuthenticationPrincipal UserDetails userDetails) {

    try {
      String userId = userDetails.getUsername();
      List<CheckFavoriteResponseDto> allFavorites = favoriteService.getFavoritesByUsername(userId);
      return ResponseEntity.ok().body(allFavorites);

    } catch (Exception e) {
      return ResponseEntity.badRequest().body("관심 여행지 목록 조회 실패");
    }
  }

}
