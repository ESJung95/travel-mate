package com.eunsun.travel_mate.service;

import com.eunsun.travel_mate.domain.Plan;
import com.eunsun.travel_mate.domain.PlanDetail;
import com.eunsun.travel_mate.domain.tourInfo.TourInfo;
import com.eunsun.travel_mate.dto.request.CreatePlanDetailRequestDto;
import com.eunsun.travel_mate.dto.response.CreatePlanDetailResponseDto;
import com.eunsun.travel_mate.repository.jpa.PlanDetailRepository;
import com.eunsun.travel_mate.repository.jpa.PlanRepository;
import com.eunsun.travel_mate.repository.jpa.TourInfoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class PlanDetailService {

  private final TourInfoRepository tourInfoRepository;
  private final PlanRepository planRepository;
  private final PlanDetailRepository planDetailRepository;

  // 여행 상세 일정 생성
  public CreatePlanDetailResponseDto createPlanDetail(Long planId, CreatePlanDetailRequestDto createPlanDetailRequestDto, String userId) {

    Plan plan = planRepository.findById(planId)
        .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

    // 사용자 정보 확인
    if (!plan.getUser().getUserId().equals(Long.valueOf(userId))) {
      throw new IllegalArgumentException("접근 권한이 없습니다.");
    }

    TourInfo tourInfo = tourInfoRepository.findById(createPlanDetailRequestDto.getTourInfoId())
        .orElseThrow(() -> new IllegalArgumentException("여행지 정보를 찾을 수 없습니다."));

    PlanDetail planDetail = PlanDetail.builder()
        .plan(plan)
        .tourInfo(tourInfo)
        .startTime(createPlanDetailRequestDto.getStartTime())
        .endTime(createPlanDetailRequestDto.getEndTime())
        .memo(createPlanDetailRequestDto.getMemo())
        .build();

    planDetailRepository.save(planDetail);
    CreatePlanDetailResponseDto responseDto = CreatePlanDetailResponseDto.from(planDetail);
    log.info("여행 상세 일정표 생성 성공");

    return responseDto;

  }
}
