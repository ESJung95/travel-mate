package com.eunsun.travel_mate.service;

import com.eunsun.travel_mate.domain.Plan;
import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.dto.request.CreatePlanRequest;
import com.eunsun.travel_mate.dto.request.UpdatePlanRequestDto;
import com.eunsun.travel_mate.dto.response.CheckPlanResponseDto;
import com.eunsun.travel_mate.dto.response.CreatePlanResponseDto;
import com.eunsun.travel_mate.dto.response.UpdatePlanResponseDto;
import com.eunsun.travel_mate.repository.jpa.PlanRepository;
import com.eunsun.travel_mate.repository.jpa.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class PlanService {

  private final UserRepository userRepository;
  private final PlanRepository planRepository;


  // 여행 일정표 생성
  public CreatePlanResponseDto createPlan(CreatePlanRequest createPlanRequest, String userId) {
    User user = userRepository.findById(Long.valueOf(userId))
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

    Plan plan = Plan.builder()
        .user(user)
        .title(createPlanRequest.getTitle())
        .startDate(createPlanRequest.getStartDate())
        .endDate(createPlanRequest.getEndDate())
        .build();

    planRepository.save(plan);
    CreatePlanResponseDto responseDto = CreatePlanResponseDto.from(plan);
    log.info("여행 일정 생성 성공");

    return responseDto;
  }


  // 여행 일정표 조회 - PlanDetail 같이 조회
  public CheckPlanResponseDto getPlan(Long planId, String userId) {
    Plan plan = planRepository.findById(planId)
        .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

    // 사용자 정보 확인
    if (!plan.getUser().getUserId().equals(Long.valueOf(userId))) {
      throw new IllegalArgumentException("접근 권한이 없습니다.");
    }

    CheckPlanResponseDto responseDto = CheckPlanResponseDto.from(plan);
    log.info("여행 일정 조회 성공");
    return responseDto;
  }

  // 여행 일정 수정
  @Transactional
  public UpdatePlanResponseDto updatePlan(Long planId, String userId, UpdatePlanRequestDto updatePlanRequestDto) {

    Plan plan = planRepository.findById(planId)
        .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

    // 사용자 정보 확인
    if (!plan.getUser().getUserId().equals(Long.valueOf(userId))) {
      throw new IllegalArgumentException("접근 권한이 없습니다.");
    }

    // 일정 정보 업데이트 - 부분 수정 가능
    if (updatePlanRequestDto.getTitle() != null) {
      plan.setTitle(updatePlanRequestDto.getTitle());
    }
    if (updatePlanRequestDto.getStartDate() != null) {
      plan.setStartDate(updatePlanRequestDto.getStartDate());
    }
    if (updatePlanRequestDto.getEndDate() != null) {
      plan.setEndDate(updatePlanRequestDto.getEndDate());
    }

    UpdatePlanResponseDto responseDto = UpdatePlanResponseDto.from(plan);
    log.info("여행 일정 정보 수정 성공");

    return responseDto;
  }

  // 일정표 삭제하기
  @Transactional
  public void deletePlan(Long planId, String userId) {
    Plan plan = planRepository.findById(planId)
        .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

    // 사용자 정보 확인
    if (!plan.getUser().getUserId().equals(Long.valueOf(userId))) {
      throw new IllegalArgumentException("접근 권한이 없습니다.");
    }

    planRepository.delete(plan);
    log.info("여행 일정 삭제 성공");

  }
}
