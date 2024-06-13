package com.eunsun.travel_mate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eunsun.travel_mate.domain.Plan;
import com.eunsun.travel_mate.domain.PlanDetail;
import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.domain.tourInfo.TourInfo;
import com.eunsun.travel_mate.dto.request.CreatePlanDetailRequestDto;
import com.eunsun.travel_mate.dto.request.UpdatePlanDetailRequestDto;
import com.eunsun.travel_mate.dto.response.CreatePlanDetailResponseDto;
import com.eunsun.travel_mate.dto.response.UpdatePlanDetailResponseDto;
import com.eunsun.travel_mate.repository.jpa.PlanDetailRepository;
import com.eunsun.travel_mate.repository.jpa.PlanRepository;
import com.eunsun.travel_mate.repository.jpa.TourInfoRepository;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlanDetailServiceTest {

  @InjectMocks
  private PlanDetailService planDetailService;

  @Mock
  private TourInfoRepository tourInfoRepository;

  @Mock
  private PlanRepository planRepository;

  @Mock
  private PlanDetailRepository planDetailRepository;

  private Plan plan;
  private TourInfo tourInfo;
  private PlanDetail planDetail;

  @BeforeEach
  void setUp() {
    User user = User.builder().userId(1L).build();
    plan = Plan.builder().planId(1L).user(user).build();
    tourInfo = TourInfo.builder().tourInfoId(1L).build();
    planDetail = PlanDetail.builder().planDetailId(1L).plan(plan).tourInfo(tourInfo).build();
  }

  @Test
  @DisplayName("상세 일정 생성 테스트")
  void createPlanDetail() {
    // given
    CreatePlanDetailRequestDto requestDto = new CreatePlanDetailRequestDto();
    requestDto.setTourInfoId(1L);
    requestDto.setStartTime(LocalTime.of(10, 0));
    requestDto.setEndTime(LocalTime.of(12, 0));
    requestDto.setMemo("테스트 메모");

    when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
    when(tourInfoRepository.findById(1L)).thenReturn(Optional.of(tourInfo));
    when(planDetailRepository.save(any(PlanDetail.class))).thenReturn(planDetail);

    // when
    CreatePlanDetailResponseDto responseDto = planDetailService.createPlanDetail(1L, requestDto, "1");

    // then
    assertNotNull(responseDto);
    assertEquals(1L, responseDto.getPlanDetailId());
    verify(planDetailRepository).save(any(PlanDetail.class));
  }

  @Test
  @DisplayName("상세 일정 수정 테스트")
  void updatePlanDetail() {
    // given
    UpdatePlanDetailRequestDto requestDto = new UpdatePlanDetailRequestDto();
    requestDto.setTourInfoId(2L);
    requestDto.setStartTime(LocalTime.of(11, 0));
    requestDto.setEndTime(LocalTime.of(13, 0));
    requestDto.setMemo("수정된 메모");

    TourInfo updatedTourInfo = TourInfo.builder().tourInfoId(2L).build();

    when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
    when(planDetailRepository.findById(1L)).thenReturn(Optional.of(planDetail));
    when(tourInfoRepository.findById(2L)).thenReturn(Optional.of(updatedTourInfo));

    // when
    UpdatePlanDetailResponseDto responseDto = planDetailService.updatePlanDetail(1L, 1L, "1", requestDto);

    // then
    assertNotNull(responseDto);
    assertEquals(2L, responseDto.getTourInfoId());
    assertEquals(LocalTime.of(11, 0), responseDto.getStartTime());
    assertEquals(LocalTime.of(13, 0), responseDto.getEndTime());
    assertEquals("수정된 메모", responseDto.getMemo());
  }

  @Test
  @DisplayName("상세 일정 삭제 테스트")
  void deletePlanDetail() {
    // given
    List<PlanDetail> planDetails = new ArrayList<>();
    planDetails.add(planDetail);

    when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
    when(planDetailRepository.findById(1L)).thenReturn(Optional.of(planDetail));
    plan.setPlanDetails(planDetails);

    // when
    planDetailService.deletePlanDetail(1L, 1L, "1");

    // then
    verify(planDetailRepository).delete(planDetail);
  }

}