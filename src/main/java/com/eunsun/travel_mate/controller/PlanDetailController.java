package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.dto.request.CreatePlanDetailRequestDto;
import com.eunsun.travel_mate.dto.request.UpdatePlanDetailRequestDto;
import com.eunsun.travel_mate.dto.response.CreatePlanDetailResponseDto;
import com.eunsun.travel_mate.dto.response.UpdatePlanDetailResponseDto;
import com.eunsun.travel_mate.service.PlanDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  public ResponseEntity<?> createPlanDetail(
      @PathVariable Long planId,
      @RequestBody CreatePlanDetailRequestDto createPlanDetailRequestDto,
      @AuthenticationPrincipal UserDetails userDetails) {

    String userId = userDetails.getUsername();
    CreatePlanDetailResponseDto createPlanDetailResponseDto = planDetailService.createPlanDetail(planId, createPlanDetailRequestDto, userId);
    return ResponseEntity.ok(createPlanDetailResponseDto);

  }

  // 여행 상세 일정 수정
  @PutMapping("/{planDetailId}")
  public ResponseEntity<?> updatePlanDetail(
      @PathVariable Long planId,
      @PathVariable("planDetailId") Long planDetailId,
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestBody UpdatePlanDetailRequestDto updatePlanDetailRequestDto) {

    String userId = userDetails.getUsername();
    UpdatePlanDetailResponseDto updatePlanDetailResponse = planDetailService.updatePlanDetail(planId, planDetailId, userId, updatePlanDetailRequestDto);
    return ResponseEntity.ok(updatePlanDetailResponse);
  }

  // 여행 상세 일정 삭제
  @DeleteMapping("/{planDetailId}")
  public ResponseEntity<?> deletePlanDetail(
      @PathVariable Long planId,
      @PathVariable("planDetailId") Long planDetailId,
      @AuthenticationPrincipal UserDetails userDetails) {

    String userId = userDetails.getUsername();
    planDetailService.deletePlanDetail(planDetailId, planId, userId);
    return ResponseEntity.ok("여행 상세 일정 삭제 성공");
  }
}
