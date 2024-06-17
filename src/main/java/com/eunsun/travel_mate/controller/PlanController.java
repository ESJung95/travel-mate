package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.dto.request.CreatePlanRequest;
import com.eunsun.travel_mate.dto.request.UpdatePlanRequestDto;
import com.eunsun.travel_mate.dto.response.CheckPlanResponseDto;
import com.eunsun.travel_mate.dto.response.CreatePlanResponseDto;
import com.eunsun.travel_mate.dto.response.UpdatePlanResponseDto;
import com.eunsun.travel_mate.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Plan", description = "여행 일정 관련 API")
public class PlanController {

  private final PlanService planService;

  // 여행 일정 생성
  @PostMapping
  @Operation(summary = "여행 일정 생성", description = "새로운 여행 일정을 생성합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "여행 일정 생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<?> createPlan(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "CreatePlanExample",
                      value = "{\"title\": \"부산 여행\", \"startDate\": \"2024-07-01\", \"endDate\": \"2024-07-05\"}"
                  )
              }
          )
      ) @RequestBody CreatePlanRequest createPlanRequest,
      @AuthenticationPrincipal UserDetails userDetails) {

    String userId = userDetails.getUsername();
    CreatePlanResponseDto createPlanId = planService.createPlan(createPlanRequest, userId);

    return ResponseEntity.ok(createPlanId);
  }

  // 여행 일정 조회
  @GetMapping("{planId}")
  @Operation(summary = "여행 일정 조회", description = "특정 여행 일정을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "여행 일정 조회 성공"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
      @ApiResponse(responseCode = "404", description = "여행 일정을 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<?> getPlan(
      @Parameter(description = "여행 일정 ID", example = "1") @PathVariable Long planId,
      @AuthenticationPrincipal UserDetails userDetails) {

    String userId = userDetails.getUsername();
    CheckPlanResponseDto planResponseDto = planService.getPlan(planId, userId);
    return ResponseEntity.ok(planResponseDto);
  }

  // 여행 일정 수정
  @PutMapping("{planId}")
  @Operation(summary = "여행 일정 수정", description = "특정 여행 일정을 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "여행 일정 수정 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
      @ApiResponse(responseCode = "404", description = "여행 일정을 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<?> updatePlan(
      @Parameter(description = "여행 일정 ID", example = "1") @PathVariable Long planId,
      @AuthenticationPrincipal UserDetails userDetails,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "UpdatePlanExample",
                      value = "{\"title\": \"제주도 여행 (수정)\", \"startDate\": \"2024-07-02\", \"endDate\": \"2024-07-07\"}"
                  )
              }
          )
      ) @RequestBody UpdatePlanRequestDto updatePlanRequestDto) {

    String userId = userDetails.getUsername();
    UpdatePlanResponseDto updatePlanResponse = planService.updatePlan(planId, userId, updatePlanRequestDto);
    return ResponseEntity.ok(updatePlanResponse);
  }

  // 여행 일정 삭제
  @DeleteMapping("{planId}")
  @Operation(summary = "여행 일정 삭제", description = "특정 여행 일정을 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "여행 일정 삭제 성공"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
      @ApiResponse(responseCode = "404", description = "여행 일정을 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<?> deletePlan(
      @Parameter(description = "여행 일정 ID", example = "1") @PathVariable Long planId,
      @AuthenticationPrincipal UserDetails userDetails) {

    String userId = userDetails.getUsername();
    planService.deletePlan(planId, userId);
    return ResponseEntity.ok("여행 일정 삭제 성공");
  }

}
