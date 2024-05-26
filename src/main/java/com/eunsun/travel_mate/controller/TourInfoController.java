package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.service.TourInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tour")
public class TourInfoController {

  private final TourInfoService tourInfoService;

  // 위치 정보를 기준으로 여행 정보 조회 -> 현재 위치, 설정 주소
  @GetMapping("/location")
  public ResponseEntity<?> getTourInfoByLocation(
      @RequestParam double latitude,
      @RequestParam double longitude,
    @RequestParam(required = false) String address) {

    return ResponseEntity.ok("위치 정보로 여행 정보 조회 성공");
  }

  // 키워드로 여행 정보 조회
  @GetMapping("/keyword")
  public ResponseEntity<?> getTourInfoByKeyword(@RequestParam String keyword) {

    return ResponseEntity.ok("키워드로 여행 정보 조회 성공");
  }

  // 관광지 상세 정보 조회 -> contentId
  @GetMapping("/{contentId}")
  public ResponseEntity<?> getTourInfoDetail(@PathVariable Long contentId) {

    return ResponseEntity.ok("관광지 상세 정보 조회 성공");
  }
}
