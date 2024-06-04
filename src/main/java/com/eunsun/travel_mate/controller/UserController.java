package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.dto.request.EmailVerificationRequestDto;
import com.eunsun.travel_mate.dto.request.LoginRequestDto;
import com.eunsun.travel_mate.dto.request.SignupRequestDto;
import com.eunsun.travel_mate.dto.request.TokenBlacklistRequestDto;
import com.eunsun.travel_mate.dto.request.UserNameUpdateRequestDto;
import com.eunsun.travel_mate.dto.request.UserPasswordUpdateRequestDto;
import com.eunsun.travel_mate.dto.response.LoginResponseDto;
import com.eunsun.travel_mate.dto.response.SignupResponseDto;
import com.eunsun.travel_mate.dto.response.UserNameUpdateResponseDto;
import com.eunsun.travel_mate.dto.response.UserResponseDto;
import com.eunsun.travel_mate.exception.UserNotFoundException;
import com.eunsun.travel_mate.security.JwtTokenProvider;
import com.eunsun.travel_mate.service.TokenBlacklistService;
import com.eunsun.travel_mate.service.UserService;
import com.eunsun.travel_mate.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
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
@RequestMapping("/user")
public class UserController {

  private final UserService userService;
  private final TokenBlacklistService tokenBlacklistService;
  private final JwtTokenProvider jwtTokenProvider;

  // 이메일 중복 확인
  @PostMapping("/check-email")
  public ResponseEntity<?> checkEmailDuplicated(@RequestBody SignupRequestDto signupRequestDto) {
    log.info("[{}] 사용자의 이메일 중복 확인 요청", signupRequestDto.getEmail());

    boolean isDuplicated = userService.checkEmailDuplicated(signupRequestDto.getEmail());

    if (isDuplicated) {
      return ResponseEntity.badRequest().body("이미 사용 중인 이메일입니다.");
    } else {
      return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }
  }

  // 메일로 인증 코드 전송
  @PostMapping("/send-verification-code")
  public ResponseEntity<?> sendVerificationCode(
      @RequestBody SignupRequestDto signupRequestDto,
      HttpServletRequest request) {
    log.info("[{}] 사용자의 메일로 인증 코드 전송 요청", signupRequestDto.getEmail());

    String verificationCode = userService.generateVerificationCode();
    boolean isSendEmail = userService.sendVerificationCode(signupRequestDto.getEmail(), verificationCode);

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
      @RequestBody EmailVerificationRequestDto emailVerificationRequestDto,
      HttpServletRequest request) {

    String inputVerificationCode = emailVerificationRequestDto.getVerificationCode();
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
      @Valid @RequestBody SignupRequestDto signupRequestDto,
      BindingResult result,
      HttpServletRequest request) {
    log.info("[{}] 사용자의 회원가입 요청", signupRequestDto.getEmail());

    // 유효성 검사 처리
    if (result.hasErrors()) {
      return ResponseEntity.badRequest().body(result.getAllErrors());
    }

    try {

      // 회원 정보 저장
      SignupResponseDto createdUser = userService.signup(signupRequestDto);

      request.getSession().invalidate(); // 세션 정보 초기화
      return ResponseEntity.ok(createdUser);
    } catch (Exception e) {
      log.error("회원 가입 실패 : " + e.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 가입 실패 : " + e.getMessage());
    }
  }

  // 로그인
  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto,
      BindingResult result) {

    // 유효성 검사 처리
    if (result.hasErrors()) {
      return ResponseEntity.badRequest().body(result.getAllErrors());
    }

    LoginResponseDto loginResponseDto = userService.loginUser(loginRequestDto.getEmail(), loginRequestDto.getPassword());
    return ResponseEntity.ok(loginResponseDto);
  }

  // 로그 아웃
  @PostMapping("/logout")
  public ResponseEntity<?> logout(Authentication authentication) {

    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자");
      }

      // JWT 토큰 문자열 추출
      String token = jwtTokenProvider.getToken(authentication);

      // 토큰에서 만료 시간 추출
      LocalDateTime expiredTime = jwtTokenProvider.getExpiredTime(token);

      TokenBlacklistRequestDto tokenBlacklistRequestDto = new TokenBlacklistRequestDto(token, expiredTime);

      // 블랙리스트에 추가
      tokenBlacklistService.addToBlacklist(tokenBlacklistRequestDto);
      return ResponseEntity.ok("로그아웃 성공");

    } catch (Exception e) {

      log.error("로그아웃 처리 중 오류 발생", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그아웃 처리 중 오류가 발생했습니다.");
    }
  }

  // 회원 정보 조회
  @GetMapping("/{userId}")
  public ResponseEntity<?> getUser(@PathVariable Long userId) {

    try {
      UserResponseDto userResponseDto = userService.getUserById(userId);
      return ResponseEntity.ok(userResponseDto);
    } catch (UserNotFoundException e) {
      log.info("사용자 정보 조회 실패 : {}", userId, e);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
    }
  }

  // 회원 정보 수정 - 이름
  @PutMapping("/{userId}/name")
  public ResponseEntity<?> updateUserName(@PathVariable Long userId,
      @RequestBody @Valid UserNameUpdateRequestDto userNameUpdateRequestDto) {

    try {
      UserNameUpdateResponseDto userNameUpdateResponseDto = userService.updateUserName(userId,
          userNameUpdateRequestDto);
      return ResponseEntity.ok(userNameUpdateResponseDto);

    } catch (UserNotFoundException e) { // 사용자 조회 실패
      log.info("사용자 정보 조회 실패 : {}", userId, e);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");

    } catch (Exception e) { // 업데이트 실패
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 이름 변경 중 오류가 발생했습니다.");
    }
  }

  // 회원 정보 수정 - 비밀번호
  @PutMapping("/{userId}/password")
  public ResponseEntity<?> updateUserPassword(@PathVariable Long userId,
      @RequestBody @Valid UserPasswordUpdateRequestDto userPasswordUpdateRequestDto) {

    try {
      userService.updateUserPassword(userId, userPasswordUpdateRequestDto);
      return ResponseEntity.ok("회원 정보 수정 완료");

    } catch (UserNotFoundException e) { // 사용자 조회 실패
      log.info("사용자 정보 조회 실패 : {}", userId, e);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");

    } catch (BadCredentialsException e) { // 현재 비밀번호 인증 실패
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());

    } catch (IllegalArgumentException e) { // 새로운 비밀번호 확인 실패, 변경할 비빌번호는 현재 비밀번호와 다른지
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

    } catch (Exception e) { // 업데이트 실패
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 변경 중 오류가 발생했습니다.");
    }
  }

  // 회원 정보 삭제
  @DeleteMapping("/{userId}")
  public ResponseEntity<?> deleteUser(@PathVariable Long userId) {

    try {
      userService.deleteUser(userId);
      return ResponseEntity.ok("회원 정보 삭제 성공");

    } catch (UserNotFoundException e) { // 사용자 조회 실패
      log.info("사용자 정보 조회 실패 : {}", userId, e);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");

    } catch (Exception e) { // 삭제 실패
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 삭제 중 오류가 발생했습니다.");
    }
  }

}
