package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.dto.request.CreatePlanRequest;
import com.eunsun.travel_mate.dto.response.CheckPlanResponseDto;
import com.eunsun.travel_mate.dto.response.CreatePlanResponseDto;
import com.eunsun.travel_mate.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {

  private final PlanService planService;

  // 여행 일정 생성
  @PostMapping
  public ResponseEntity<?> createPlan(
      @RequestBody CreatePlanRequest createPlanRequest,
      @AuthenticationPrincipal UserDetails userDetails) {

    String userId = userDetails.getUsername();
    CreatePlanResponseDto createPlanId = planService.createPlan(createPlanRequest, userId);

    return ResponseEntity.ok(createPlanId);
  }

  // 여행 일정 조회
  @GetMapping("{planId}")
  public ResponseEntity<?> getPlan(
      @PathVariable Long planId,
      @AuthenticationPrincipal UserDetails userDetails) {

    String userId = userDetails.getUsername();
    CheckPlanResponseDto planResponseDto = planService.getPlan(planId, userId);
    return ResponseEntity.ok(planResponseDto);
  }

  // 여행 일정 수정
  @PutMapping("{planId}")
  public ResponseEntity<?> updatePlan(@PathVariable Long planId) {

    return ResponseEntity.ok("여행 일정 수정 성공");
  }

  // 여행 일정 삭제
  @DeleteMapping("{planId}")
  public ResponseEntity<?> deletePlan(@PathVariable Long planId) {

    return ResponseEntity.ok("여행 일정 삭제 성공");
  }

}
