package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.service.AreaCodeService;
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
public class AreaCodeController {

  private final AreaCodeService areaCodeService;

  // 지역 코드 전체 조회
  @GetMapping
  public ResponseEntity<?> getAllAreaCodes() {

    return ResponseEntity.ok("지역 코드 전체 조회 성공");
  }

}
