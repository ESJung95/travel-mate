package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.domain.AreaCode;
import com.eunsun.travel_mate.service.AreaCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/area-code")
@Tag(name = "AreaCode", description = "지역 코드 API")

public class AreaCodeController {

  private final AreaCodeService areaCodeService;

  // 지역 코드 전체 조회
  @GetMapping
  @Operation(
      summary = "OpenApi에서 전체 정보 가져오기",
      description = "OpenApi에서 지역 코드 전체를 가져와 저장합니다.")
  @ApiResponse(responseCode = "200", description = "성공")
  public ResponseEntity<?> getAreaCodeFromApi() {
    List<AreaCode> areaCodes =  areaCodeService.getAreaCodeFromApi();
    return ResponseEntity.ok(areaCodes);
  }
}
