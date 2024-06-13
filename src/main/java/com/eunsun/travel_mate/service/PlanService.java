package com.eunsun.travel_mate.service;

import com.eunsun.travel_mate.domain.Plan;
import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.dto.request.CreatePlanRequest;
import com.eunsun.travel_mate.dto.response.CreatePlanResponseDto;
import com.eunsun.travel_mate.repository.jpa.PlanRepository;
import com.eunsun.travel_mate.repository.jpa.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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


}
