package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.domain.tourInfo.TourInfoDocument;
import com.eunsun.travel_mate.service.TourInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tour")
public class TourInfoController {

  private final TourInfoService tourInfoService;

  // OpenApi 에서 전체 정보 가져오기
  @GetMapping
  public ResponseEntity<?> getTourInfoFromApi() {
    tourInfoService.getTourInfoFromApi();
    return ResponseEntity.ok("성공");
  }

  // 제목으로 검색
  @GetMapping("/search/title")
  public ResponseEntity<Page<TourInfoDocument>> searchByTitle(
      @RequestParam String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    Page<TourInfoDocument> searchResults = tourInfoService.searchByTitle(keyword, pageable);
    return ResponseEntity.ok(searchResults);
  }

  // 주소로 검색
  @GetMapping("/search/address")
  public ResponseEntity<Page<TourInfoDocument>> searchByAddress(
      @RequestParam String addr,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    Page<TourInfoDocument> searchResults = tourInfoService.searchByAddress(addr, pageable);
    return ResponseEntity.ok(searchResults);
  }


  // 내 위치 기반으로 가까운 여행지 검색
  @GetMapping("/search/mylocation")
  public ResponseEntity<Page<TourInfoDocument>> searchByMyLocation(
      @RequestParam double lat,
      @RequestParam double lon,
      @RequestParam(defaultValue = "5") double distance,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<TourInfoDocument> searchResults = tourInfoService.searchByLocation(lat, lon, distance, pageable);
    return ResponseEntity.ok(searchResults);

  }

}
