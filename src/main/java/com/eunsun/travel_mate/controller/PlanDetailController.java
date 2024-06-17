package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.dto.request.CreatePlanDetailRequestDto;
import com.eunsun.travel_mate.dto.request.UpdatePlanDetailRequestDto;
import com.eunsun.travel_mate.dto.response.CreatePlanDetailResponseDto;
import com.eunsun.travel_mate.dto.response.UpdatePlanDetailResponseDto;
import com.eunsun.travel_mate.service.PlanDetailService;
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
@Tag(name = "PlanDetail", description = "여행 상세 일정 관련 API")
public class PlanDetailController {

  private final PlanDetailService planDetailService;

  // 여행 상세 일정 생성
  @PostMapping
  @Operation(summary = "여행 상세 일정 생성", description = "새로운 여행 상세 일정을 생성합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "여행 상세 일정 생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
      @ApiResponse(responseCode = "404", description = "여행 일정을 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<?> createPlanDetail(
      @Parameter(description = "여행 일정 ID", example = "1") @PathVariable Long planId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "CreatePlanDetailExample",
                      value = "{\"tourInfoId\": 1, \"startTime\": \"10:00\", \"endTime\": \"14:00\", \"memo\": \"경복궁 관람\"}"
                  )
              }
          )
      ) @RequestBody CreatePlanDetailRequestDto createPlanDetailRequestDto,
      @AuthenticationPrincipal UserDetails userDetails) {

    String userId = userDetails.getUsername();
    CreatePlanDetailResponseDto createPlanDetailResponseDto = planDetailService.createPlanDetail(planId, createPlanDetailRequestDto, userId);
    return ResponseEntity.ok(createPlanDetailResponseDto);

  }

  // 여행 상세 일정 수정
  @PutMapping("/{planDetailId}")
  @Operation(summary = "여행 상세 일정 수정", description = "특정 여행 상세 일정을 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "여행 상세 일정 수정 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
      @ApiResponse(responseCode = "404", description = "여행 상세 일정을 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<?> updatePlanDetail(
      @Parameter(description = "여행 일정 ID", example = "1") @PathVariable Long planId,
      @Parameter(description = "여행 상세 일정 ID", example = "1") @PathVariable("planDetailId") Long planDetailId,
      @AuthenticationPrincipal UserDetails userDetails,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "UpdatePlanDetailExample",
                      value = "{\"tourInfoId\": 1, \"startTime\": \"11:00\", \"endTime\": \"15:00\", \"memo\": \"경복궁 관람 및 점심식사\"}"
                  )
              }
          )
      ) @RequestBody UpdatePlanDetailRequestDto updatePlanDetailRequestDto) {

    String userId = userDetails.getUsername();
    UpdatePlanDetailResponseDto updatePlanDetailResponse = planDetailService.updatePlanDetail(planId, planDetailId, userId, updatePlanDetailRequestDto);
    return ResponseEntity.ok(updatePlanDetailResponse);
  }

  // 여행 상세 일정 삭제
  @DeleteMapping("/{planDetailId}")
  @Operation(summary = "여행 상세 일정 삭제", description = "특정 여행 상세 일정을 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "여행 상세 일정 삭제 성공"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
      @ApiResponse(responseCode = "404", description = "여행 상세 일정을 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<?> deletePlanDetail(
      @Parameter(description = "여행 일정 ID", example = "1") @PathVariable Long planId,
      @Parameter(description = "여행 상세 일정 ID", example = "2") @PathVariable("planDetailId") Long planDetailId,
      @AuthenticationPrincipal UserDetails userDetails) {

    String userId = userDetails.getUsername();
    planDetailService.deletePlanDetail(planDetailId, planId, userId);
    return ResponseEntity.ok("여행 상세 일정 삭제 성공");
  }
}
