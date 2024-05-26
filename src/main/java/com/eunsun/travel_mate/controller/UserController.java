package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

  private final UserService userService;

  // 회원가입
  @PostMapping("/signup")
  public ResponseEntity<?> signup() {

    return ResponseEntity.ok("회원가입 성공");
  }

  // 로그인
  @PostMapping("/login")
  public ResponseEntity<?> login() {

    return ResponseEntity.ok("로그인 성공");
  }

  // 로그 아웃
  @PostMapping("/logout")
  public ResponseEntity<?> logout() {

    return ResponseEntity.ok("로그아웃 성공");
  }
  // 회원 정보 조회
  @GetMapping("/{userId}")
  public ResponseEntity<?> gotUser() {

    return ResponseEntity.ok("회원 정보 조회 성공");
  }
  // 회원 정보 수정
  @PutMapping("/{userId}")
  public ResponseEntity<?> updateUser() {

    return ResponseEntity.ok("회원 정보 수정");
  }

  // 회원 정보 삭제
  @DeleteMapping("/{userId}")
  public ResponseEntity<?> deleteUser() {

    return ResponseEntity.ok("회원 탈퇴 성공");
  }
}
