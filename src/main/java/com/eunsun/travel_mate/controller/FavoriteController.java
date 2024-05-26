package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
  public ResponseEntity<?> addFavorite() {

    return ResponseEntity.ok("관심 여행지 추가 성공");
  }

  // 관심 여행지 목록 조회
  @GetMapping
  public ResponseEntity<?> getFavorites() {

    return ResponseEntity.ok("관심 여행지 목록 조회");
  }

  // 관심 여행지 삭제
  @DeleteMapping("/{favoriteId}")
  public ResponseEntity<?> deleteFavorite(@PathVariable Long favoriteId) {

    return ResponseEntity.ok("관심 여행지 삭제 성공");
  }

}
