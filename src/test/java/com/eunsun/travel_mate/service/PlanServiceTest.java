package com.eunsun.travel_mate.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eunsun.travel_mate.domain.Plan;
import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.dto.request.CreatePlanRequest;
import com.eunsun.travel_mate.dto.request.UpdatePlanRequestDto;
import com.eunsun.travel_mate.dto.response.CheckPlanResponseDto;
import com.eunsun.travel_mate.dto.response.CreatePlanResponseDto;
import com.eunsun.travel_mate.dto.response.UpdatePlanResponseDto;
import com.eunsun.travel_mate.repository.jpa.PlanRepository;
import com.eunsun.travel_mate.repository.jpa.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PlanServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PlanRepository planRepository;

  @InjectMocks
  private PlanService planService;

  @Test
  @DisplayName("일정표 생성 테스트")
  void createPlan() {
    // given
    CreatePlanRequest createPlanRequest = new CreatePlanRequest();
    createPlanRequest.setTitle("제주도 여행");
    createPlanRequest.setStartDate(LocalDate.of(2024, 7, 1));
    createPlanRequest.setEndDate(LocalDate.of(2024, 7, 10));

    User mockUser = mock(User.class);
    when(mockUser.getUserId()).thenReturn(1L);

    // when
    when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(mockUser));

    CreatePlanResponseDto response = planService.createPlan(createPlanRequest, "1");

    // then
    assertNotNull(response);
    assertEquals("여행 일정표 생성 성공", response.getMessage());
    verify(planRepository, times(1)).save(any(Plan.class));
  }

  @Test
  @DisplayName("일정표 조회 테스트")
  void getPlan() {
    // given
    User mockUser = mock(User.class);
    when(mockUser.getUserId()).thenReturn(1L);

    Plan mockPlan = mock(Plan.class);
    when(mockPlan.getPlanId()).thenReturn(1L);
    when(mockPlan.getTitle()).thenReturn("제주도 여행");
    when(mockPlan.getUser()).thenReturn(mockUser);

    // when
    when(planRepository.findById(any())).thenReturn(Optional.of(mockPlan));

    CheckPlanResponseDto response = planService.getPlan(1L, "1");

    // then
    assertNotNull(response);
    assertEquals("제주도 여행", response.getTitle());
    verify(planRepository, times(1)).findById(any());
  }

  @Test
  @DisplayName("일정표 수정 테스트")
  void updatePlan() {
    // given
    User mockUser = mock(User.class);
    when(mockUser.getUserId()).thenReturn(1L);

    Plan mockPlan = mock(Plan.class);
    when(mockPlan.getPlanId()).thenReturn(1L);
    when(mockPlan.getUser()).thenReturn(mockUser);
    when(mockPlan.getTitle()).thenReturn("제주도 여행");
    when(mockPlan.getStartDate()).thenReturn(LocalDate.of(2024, 7, 1));
    when(mockPlan.getEndDate()).thenReturn(LocalDate.of(2024, 7, 10));

    UpdatePlanRequestDto updatePlanRequestDto = new UpdatePlanRequestDto();
    updatePlanRequestDto.setTitle("부산 여행");
    updatePlanRequestDto.setStartDate(LocalDate.of(2024, 7, 5));
    updatePlanRequestDto.setEndDate(LocalDate.of(2024, 7, 15));

    // when
    when(planRepository.findById(any(Long.class))).thenReturn(Optional.of(mockPlan));
    when(mockPlan.getTitle()).thenReturn("부산 여행");
    when(mockPlan.getStartDate()).thenReturn(LocalDate.of(2024, 7, 5));
    when(mockPlan.getEndDate()).thenReturn(LocalDate.of(2024, 7, 15));

    UpdatePlanResponseDto response = planService.updatePlan(1L, "1", updatePlanRequestDto);

    // then
    assertNotNull(response);
    assertEquals("부산 여행", response.getTitle());
    assertEquals(LocalDate.of(2024, 7, 5), response.getStartDate());
    assertEquals(LocalDate.of(2024, 7, 15), response.getEndDate());
    verify(planRepository, times(1)).findById(any(Long.class));
    verify(mockPlan).setTitle("부산 여행");
    verify(mockPlan).setStartDate(LocalDate.of(2024, 7, 5));
    verify(mockPlan).setEndDate(LocalDate.of(2024, 7, 15));
  }

  @Test
  @DisplayName("일정표 삭제 테스트")
  void deletePlan() {
    // given
    User mockUser = mock(User.class);
    when(mockUser.getUserId()).thenReturn(1L);

    Plan mockPlan = mock(Plan.class);
    when(mockPlan.getUser()).thenReturn(mockUser);

    // when
    when(planRepository.findById(any())).thenReturn(Optional.of(mockPlan));

    // then
    assertDoesNotThrow(() -> planService.deletePlan(1L, "1"));
    verify(planRepository, times(1)).delete(any(Plan.class));
  }

  @Test
  @DisplayName("인증 실패 테스트")
  void authenticationFailureTest() {
    // given
    User mockUser = mock(User.class);
    when(mockUser.getUserId()).thenReturn(2L); // 다른 사용자 ID로 설정

    Plan mockPlan = mock(Plan.class);
    when(mockPlan.getUser()).thenReturn(mockUser);

    // when
    when(planRepository.findById(any())).thenReturn(Optional.of(mockPlan));

    // then
    assertThrows(IllegalArgumentException.class, () -> planService.deletePlan(1L, "1"));
  }

  @Test
  @DisplayName("잘못된 입력값 테스트")
  void invalidInputTest() {
    // given
    CreatePlanRequest createPlanRequest = new CreatePlanRequest();
    createPlanRequest.setTitle(null); // 잘못된 입력값

    User mockUser = mock(User.class);
    when(mockUser.getUserId()).thenReturn(1L);

    // when
    when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(mockUser));

    // then
    assertThrows(IllegalArgumentException.class, () -> planService.createPlan(createPlanRequest, "1"));
  }

  @Test
  @DisplayName("리포지토리 예외 테스트")
  void repositoryExceptionTest() {
    // given
    CreatePlanRequest createPlanRequest = new CreatePlanRequest();
    createPlanRequest.setTitle("강원도 여행");
    createPlanRequest.setStartDate(LocalDate.of(2024, 7, 1));
    createPlanRequest.setEndDate(LocalDate.of(2024, 7, 10));

    // when
    when(userRepository.findById(any(Long.class))).thenThrow(new RuntimeException("Repository Exception"));

    // then
    assertThrows(RuntimeException.class, () -> planService.createPlan(createPlanRequest, "1"));
  }
}