package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.domain.tourInfo.TourInfoDocument;
import com.eunsun.travel_mate.service.TourInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tour")
@Tag(name = "TourInfo", description = "여행 정보 관련 API")
public class TourInfoController {

  private final TourInfoService tourInfoService;

  // OpenApi 에서 전체 정보 가져오기
  @GetMapping
  @Operation(
      summary = "OpenApi에서 전체 정보 가져오기",
      description = "OpenApi에서 전체 여행 정보를 가져와 저장합니다.")
  @ApiResponse(responseCode = "200", description = "성공")
  public ResponseEntity<?> getTourInfoFromApi() {
    tourInfoService.getTourInfoFromApi();
    return ResponseEntity.ok("성공");
  }

  // 제목으로 검색
  @GetMapping("/search/title")
  @Operation(
      summary = "제목으로 검색",
      description = "여행지 제목으로 검색합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "검색 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<Page<TourInfoDocument>> searchByTitle(
      @Parameter(description = "검색 키워드", example = "카페") @RequestParam String keyword,
      @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") int page,
      @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    Page<TourInfoDocument> searchResults = tourInfoService.searchByTitle(keyword, pageable);
    return ResponseEntity.ok(searchResults);
  }

  // 주소로 검색
  @GetMapping("/search/address")
  @Operation(
      summary = "주소로 검색",
      description = "여행지 주소로 검색합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "검색 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<Page<TourInfoDocument>> searchByAddress(
      @Parameter(description = "검색할 주소", example = "부산") @RequestParam String addr,
      @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") int page,
      @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    Page<TourInfoDocument> searchResults = tourInfoService.searchByAddress(addr, pageable);
    return ResponseEntity.ok(searchResults);
  }


  // 내 위치 기반으로 가까운 여행지 검색
  @GetMapping("/search/mylocation")
  @Operation(
      summary = "내 위치 기반으로 가까운 여행지 검색",
      description = "내 위치를 기반으로 가까운 여행지를 검색합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "검색 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<Page<TourInfoDocument>> searchByMyLocation(
      @Parameter(description = "위도", example = "37.5663") @RequestParam double lat,
      @Parameter(description = "경도", example = "126.9997") @RequestParam double lon,
      @Parameter(description = "검색 반경 (km)") @RequestParam(defaultValue = "5") double distance,
      @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") int page,
      @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<TourInfoDocument> searchResults = tourInfoService.searchByLocation(lat, lon, distance,
        pageable);
    return ResponseEntity.ok(searchResults);

  }

  // 원하는 위치 기반으로 가까운 여행지 검색
  @GetMapping("/search/location")
  @Operation(summary = "원하는 위치 기반으로 가까운 여행지 검색", description = "원하는 위치를 기반으로 가까운 여행지를 검색합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "검색 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<?> searchByLocation(
      @Parameter(description = "검색할 주소", example = "강남") @RequestParam String address,
      @Parameter(description = "검색 반경 (km)") @RequestParam(defaultValue = "5") double distance,
      @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") int page,
      @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size
  ) {

    // 주소를 좌표로 변환
    GeoPoint location = tourInfoService.convertToLocation(address);

    if (location == null) {
      return ResponseEntity.badRequest().body("주소를 좌표로 변환할 수 없습니다.");
    }

    Pageable pageable = PageRequest.of(page, size);
    Page<TourInfoDocument> searchResults = tourInfoService.searchByLocation(
        location.getLat(), location.getLon(), distance, pageable);

    return ResponseEntity.ok(searchResults);

  }
}
