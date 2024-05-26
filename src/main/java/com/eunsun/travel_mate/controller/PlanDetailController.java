package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.service.PlanDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/plan/{planId}/detail")
public class PlanDetailController {

  private final PlanDetailService planDetailService;

  // 여행 상세 일정 생성
  @PostMapping
  public ResponseEntity<?> createPlanDetail(@PathVariable Long planId) {

    return ResponseEntity.ok("여행 상세 일정 생성");

  }

  // 여행 상세 일정 조회
  @GetMapping("/{planDetailId}")
  public ResponseEntity<?> getPlanDetail(@PathVariable Long planId, @PathVariable Long planDetailId) {

    return ResponseEntity.ok("여행 상세 일정 조회");
  }

  // 여행 상세 일정 수정
  @PutMapping("/{planDetailId}")
  public ResponseEntity<?> updatePlanDetail(@PathVariable Long planId, @PathVariable Long planDetailId) {

    return ResponseEntity.ok("여행 상세 일정 수정");
  }

  // 여행 상세 일정 삭제
  @DeleteMapping("/{planDetailId}")
  public ResponseEntity<?> deletePlanDetail(@PathVariable Long planId, @PathVariable Long planDetailId) {

    return ResponseEntity.ok("여행 상세 일정 삭제");
  }
}
