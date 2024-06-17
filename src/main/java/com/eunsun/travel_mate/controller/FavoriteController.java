package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.dto.request.CreateFavoriteRequestDto;
import com.eunsun.travel_mate.dto.response.CheckFavoriteResponseDto;
import com.eunsun.travel_mate.dto.response.CreateFavoriteResponseDto;
import com.eunsun.travel_mate.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/favorite")
@Tag(name = "Favorite", description = "관심 여행지 관련 API")
public class FavoriteController {

  private final FavoriteService favoriteService;

  // 관심 여행지 추가
  @PostMapping
  @Operation(summary = "관심 여행지 추가", description = "새로운 관심 여행지를 추가합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "관심 여행지 추가 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<?> addFavorite(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "AddFavoriteExample",
                      value = "{\"tourInfoId\": 1}"
                  )
              }
          )
      ) @RequestBody CreateFavoriteRequestDto createFavoriteRequestDto,
      @AuthenticationPrincipal UserDetails userDetails) {

    try {
      String userId = userDetails.getUsername();
      CreateFavoriteResponseDto favoriteResponse = favoriteService.addFavorite(userId,
          createFavoriteRequestDto);
      return ResponseEntity.ok(favoriteResponse);

    } catch (Exception e) {
      log.error("관심 여행지 추가 실패: {}", e.getMessage());
      return ResponseEntity.badRequest().body("관심 여행지 추가 실패" );
    }
  }

  // 관심 여행지 목록 전체 조회
  @GetMapping
  @Operation(summary = "관심 여행지 목록 조회", description = "사용자의 관심 여행지 목록을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "관심 여행지 목록 조회 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<?> getFavorites(@AuthenticationPrincipal UserDetails userDetails) {

    try {
      String userId = userDetails.getUsername();
      List<CheckFavoriteResponseDto> allFavorites = favoriteService.getFavoritesByUsername(userId);
      return ResponseEntity.ok().body(allFavorites);

    } catch (Exception e) {
      log.error("관심 여행지 목록 조회 실패: {}", e.getMessage());
      return ResponseEntity.badRequest().body("관심 여행지 목록 조회 실패");
    }
  }

    // 관심 여행지 삭제
  @DeleteMapping("/{favoriteId}")
  @Operation(summary = "관심 여행지 삭제", description = "특정 관심 여행지를 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "관심 여행지 삭제 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<?> deleteFavorite(
      @Parameter(description = "관심 여행지 ID", example = "1") @PathVariable Long favoriteId,
      @AuthenticationPrincipal UserDetails userDetails) {

    try {
      String userId = userDetails.getUsername();
      favoriteService.deleteFavorite(favoriteId, userId);
      return ResponseEntity.ok().body("관심 여행지 삭제 성공");

    } catch (Exception e) {
      log.error("관심 여행지 삭제 실패: {}", e.getMessage());
      return ResponseEntity.badRequest().body("관심 여행지 삭제 실패");
    }
  }

}
