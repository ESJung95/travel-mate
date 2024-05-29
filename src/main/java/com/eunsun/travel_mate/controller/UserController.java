package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.dto.EmailVerificationDto;
import com.eunsun.travel_mate.dto.SignupDto;
import com.eunsun.travel_mate.service.UserService;
import com.eunsun.travel_mate.util.SessionUtil;
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

    boolean isDuplicated = userService.checkEmailDuplicated(signupDto.getEmail());

    if (isDuplicated) {
      return ResponseEntity.badRequest().body("이미 사용 중인 이메일입니다.");
    } else {
      return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }
  }

  // 메일로 인증 코드 전송
  @PostMapping("/send-verification-code")
  public ResponseEntity<?> sendVerificationCode(
      @RequestBody SignupDto signupDto,
      HttpServletRequest request) {
    log.info("[{}] 사용자의 메일로 인증 코드 전송 요청", signupDto.getEmail());

    String verificationCode = userService.generateVerificationCode();
    boolean isSendEmail = userService.sendVerificationCode(signupDto.getEmail(), verificationCode);

    if (isSendEmail) {
      SessionUtil.setVerificationCode(request, verificationCode); // 세션에 저장
      return ResponseEntity.ok("인증 코드 전송 완료");
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증 코드 전송 실패");
    }
  }


  // 이메일 인증 코드 확인
  @PostMapping("/verify-email")
  public ResponseEntity<?> verifyEmail(
      @RequestBody EmailVerificationDto emailVerificationDto,
      HttpServletRequest request) {

    String inputVerificationCode = emailVerificationDto.getVerificationCode();
    String storedVerificationCode = SessionUtil.getVerificationCode(request);

    boolean isVerifyCode = userService.verifyEmailCode(inputVerificationCode, storedVerificationCode);

    if (isVerifyCode) {
      SessionUtil.setEmailVerified(request, true);
      return ResponseEntity.ok("이메일 인증 성공");

    } else {
      return ResponseEntity.badRequest().body("유효하지 않은 인증코드");
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
      User createdUser = userService.signup(signupDto);

      request.getSession().invalidate(); // 세션 정보 초기화
      return ResponseEntity.ok(createdUser);
    } catch (Exception e) {
      log.error("회원 가입 실패 : " + e.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 가입 실패 : " + e.getMessage());
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
  public ResponseEntity<?> getUser() {

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
