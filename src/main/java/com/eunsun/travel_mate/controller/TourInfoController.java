package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.domain.tourInfo.TourInfoDocument;
import com.eunsun.travel_mate.service.tourinfo.TourInfoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  // 여행 정보 전체 조회
  @GetMapping
  public ResponseEntity<?> getTourInfoFromApi()  {
    tourInfoService.getTourInfoFromApi();
    return ResponseEntity.ok("성공");
  }

  // 제목으로 검색
  @GetMapping("/search/title")
  public ResponseEntity<List<TourInfoDocument>> searchByTitle(@RequestParam String keyword) {
    List<TourInfoDocument> searchResults = tourInfoService.searchByTitle(keyword);
    return ResponseEntity.ok(searchResults);
  }

  // 주소로 검색
  @GetMapping("/search/address")
  public ResponseEntity<List<TourInfoDocument>> searchByAddress(@RequestParam String addr) {
    List<TourInfoDocument> searchResults = tourInfoService.searchByAddress(addr);
    return ResponseEntity.ok(searchResults);
  }

}
