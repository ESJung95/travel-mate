package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.service.UserSocialService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/social")
@Tag(name = "아직 미구현", description = "소셜 로그인 기능")

public class UserSocialController {

  private final UserSocialService userSocialService;

  // 소셜 로그인
  @PostMapping("/login")
  public ResponseEntity<?> socialLogin() {

    return ResponseEntity.ok("소셜 로그인 성공");
  }

  // 소셜 계쩡 연동
  @PostMapping("/connect")
  public ResponseEntity<?> connectSocial() {

    return ResponseEntity.ok("소셜 계정 연결 성공");
  }

  // 소셜 계정 연동 해제
  @DeleteMapping("/disconnect/{socialProviderId}")
  public ResponseEntity<?> disconnectSocial() {

    return ResponseEntity.ok("소셜 계정 연동 해제");
  }
}
