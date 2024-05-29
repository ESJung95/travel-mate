package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.dto.EmailVerificationDto;
import com.eunsun.travel_mate.dto.SignupDto;
import com.eunsun.travel_mate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

  private final UserService userService;

  // 이메일 중복 확인
  @PostMapping("/check-email")
  public ResponseEntity<?> checkEmailDuplicated(@RequestBody SignupDto signupDto) {
    log.info("[{}] 사용자의 이메일 중복 확인 요청", signupDto.getEmail());

    try {
      userService.checkEmailDuplicated(signupDto.getEmail());
      return ResponseEntity.ok("사용 가능한 이메일입니다.");

    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  // 메일로 인증 코드 전송
  @PostMapping("/send-verification-code")
  public ResponseEntity<?> sendVerificationCode(
      @RequestBody SignupDto signupDto,
      HttpServletRequest request) {
    log.info("[{}] 사용자의 메일로 인증 코드 전송 요청", signupDto.getEmail());

    userService.sendVerificationCode(signupDto.getEmail(), request);
    return ResponseEntity.ok("이메일로 인증 코드 전송!");
  }


  // 이메일 인증 코드 확인
  @PostMapping("/verify-email")
  public ResponseEntity<?> verifyEmail(
      @RequestBody EmailVerificationDto emailVerificationDto,
      HttpServletRequest request) {

    if (userService.verifyEmailCode(emailVerificationDto.getVerificationCode(), request)) {
      return ResponseEntity.ok("이메일 인증 성공");
    } else {
      return ResponseEntity.badRequest().body("유효하지 않은 인증 코드");
    }
  }

  // 회원 가입
  @PostMapping("/signup")
  public ResponseEntity<?> signup(
      @Valid @RequestBody SignupDto signupDto,
      BindingResult result,
      HttpServletRequest request) {
    log.info("[{}] 사용자의 회원가입 요청", signupDto.getEmail());

    // 유효성 검사 처리
    if (result.hasErrors()) {
      return ResponseEntity.badRequest().body(result.getAllErrors());
    }

    try {
      // 회원 정보 저장
      User createdUser = userService.signup(signupDto, request);

      request.getSession().invalidate(); // 세션 정보 초기화
      return ResponseEntity.ok(createdUser);
    } catch (Exception e) {
      log.error("회원 가입 실패 : " + e.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 가입 실패");
    }
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
