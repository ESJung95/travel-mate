package com.eunsun.travel_mate.controller;

import com.eunsun.travel_mate.dto.request.EmailVerificationRequestDto;
import com.eunsun.travel_mate.dto.request.FindPasswordRequestDto;
import com.eunsun.travel_mate.dto.request.FindUserEmailRequestDto;
import com.eunsun.travel_mate.dto.request.LoginRequestDto;
import com.eunsun.travel_mate.dto.request.SignupRequestDto;
import com.eunsun.travel_mate.dto.request.TokenBlacklistRequestDto;
import com.eunsun.travel_mate.dto.request.UserNameUpdateRequestDto;
import com.eunsun.travel_mate.dto.request.UserPasswordUpdateRequestDto;
import com.eunsun.travel_mate.dto.response.FindUserEmailResponseDto;
import com.eunsun.travel_mate.dto.response.LoginResponseDto;
import com.eunsun.travel_mate.dto.response.SignupResponseDto;
import com.eunsun.travel_mate.dto.response.UserNameUpdateResponseDto;
import com.eunsun.travel_mate.dto.response.UserResponseDto;
import com.eunsun.travel_mate.exception.UserNotFoundException;
import com.eunsun.travel_mate.security.JwtTokenProvider;
import com.eunsun.travel_mate.service.TokenBlacklistService;
import com.eunsun.travel_mate.service.UserService;
import com.eunsun.travel_mate.util.SessionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
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
@Tag(name = "User", description = "사용자 관련 API")
public class UserController {

  private final UserService userService;
  private final TokenBlacklistService tokenBlacklistService;
  private final JwtTokenProvider jwtTokenProvider;

  // 이메일 중복 확인
  @PostMapping("/check-email")
  @Operation(
      summary = "이메일 중복 확인",
      description = "사용자의 이메일 중복 여부를 확인합니다.")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "사용 가능한 이메일"),
      @ApiResponse(responseCode = "400", description = "이미 사용 중인 이메일")
  })
  public ResponseEntity<?> checkEmailDuplicated(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "EmailCheckExample",
                      value = "{\"email\": \"example@naver.com\"}"
                  )
              }
          )
      ) @RequestBody SignupRequestDto signupRequestDto) {
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
  @Operation(
      summary = "인증 코드 전송",
      description = "사용자의 이메일로 인증 코드를 전송합니다.")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "인증 코드 전송 완료"),
      @ApiResponse(responseCode = "500", description = "인증 코드 전송 실패")
  })

  public ResponseEntity<?> sendVerificationCode(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "VerificationCodeExample",
                      value = "{\"email\": \"example@naver.com\"}"
                  )
              }
          )
      ) @RequestBody SignupRequestDto signupRequestDto,
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
  @Operation(
      summary = "이메일 인증 코드 확인",
      description = "사용자가 입력한 이메일 인증 코드를 확인합니다.")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "이메일 인증 성공"),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 인증코드")
  })
  public ResponseEntity<?> verifyEmail(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "VerifyEmailExample",
                      value = "{\"verificationCode\": \"123456\"}"
                  )
              }
          )
      ) @RequestBody EmailVerificationRequestDto emailVerificationRequestDto,
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
  @Operation(
      summary = "사용자 회원 가입",
      description = "사용자의 회원 가입을 처리합니다.")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "회원 가입 성공"),
      @ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
      @ApiResponse(responseCode = "500", description = "회원 가입 실패")
  })
  public ResponseEntity<?> signup(
      @Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "SignupExample",
                      value = "{\"email\": \"example@naver.com\", \"password\": \"Abc123!@#\", \"name\": \"example\", \"birthdate\": \"2000-11-22\"}"
                  )
              }
          )
      ) @RequestBody SignupRequestDto signupRequestDto,
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
  @Operation(
      summary = "사용자 로그인",
      description = """
          사용자 로그인을 처리합니다.\s

          Swagger UI에서 테스트하려면 다음 단계를 따르세요:\s

          1. Swagger UI 상단의 자물쇠 모양 'Authorize' 버튼을 클릭합니다.\s
          2. 'Available authorizations' 창에서 'bearerAuth'를 선택하고 'Authorize' 버튼을 클릭합니다.\s
          3. 'Value' 입력란에 유효한 JWT 토큰을 입력한 후 'Authorize' 버튼을 클릭하고 'Close' 버튼을 클릭합니다.\s

          이제 Swagger UI에서 보내는 모든 요청에 입력한 JWT 토큰이 'Authorization: Bearer <token>' 형식으로 포함됩니다."""
  )

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그인 성공"),
      @ApiResponse(responseCode = "400", description = "유효성 검사 실패")
  })
  public ResponseEntity<?> login(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "LoginExample",
                      value = "{\"email\": \"example@naver.com\", \"password\": \"Abc123!@#\"}"
                  )
              }
          )
      ) @RequestBody LoginRequestDto loginRequestDto,
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
  @Operation(
      summary = "사용자 로그아웃",
      description = "사용자 로그아웃을 처리합니다.")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
      @ApiResponse(responseCode = "500", description = "로그아웃 처리 중 오류 발생")
  })
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
  @Operation(
      summary = "회원 정보 조회",
      description = "사용자의 회원 정보를 조회합니다.")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
  })
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
  @Operation(
      summary = "회원 정보 수정 - 이름",
      description = "사용자의 이름을 수정합니다.")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "이름 수정 성공"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "이름 변경 중 오류 발생")
  })
  public ResponseEntity<?> updateUserName(
      @PathVariable Long userId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "UpdateNameExample",
                      value = "{\"name\": \"김철수\"}"
                  )
              }
          )
      ) @RequestBody @Valid UserNameUpdateRequestDto userNameUpdateRequestDto) {

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
  @Operation(
      summary = "회원 정보 수정 - 비밀번호",
      description = "사용자의 비밀번호를 수정합니다.")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "비밀번호 수정 성공"),
      @ApiResponse(responseCode = "400", description = "새로운 비밀번호 확인 실패"),
      @ApiResponse(responseCode = "401", description = "현재 비밀번호 인증 실패"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "비밀번호 변경 중 오류 발생")
  })
  public ResponseEntity<?> updateUserPassword(
      @PathVariable Long userId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "UpdatePasswordExample",
                      value = "{\"currentPassword\": \"old_password\", \"newPassword\": \"new_password\"}"
                  )
              }
          )
      ) @RequestBody @Valid UserPasswordUpdateRequestDto userPasswordUpdateRequestDto) {

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
  @Operation(
      summary = "회원 정보 삭제",
      description = "사용자의 회원 정보를 삭제합니다.")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "회원 정보 삭제 성공"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "사용자 삭제 중 오류 발생")
  })
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

  // 이름과 생년월일로 User 이메일 찾기
  @GetMapping("/find/email")
  @Operation(
      summary = "이메일 찾기",
      description = "사용자의 이름과 생년월일로 이메일을 찾습니다.")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "이메일 찾기 성공"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "서버 오류 발생")
  })
  public ResponseEntity<?> findUserEmail(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "FindEmailExample",
                      value = "{\"name\": \"example\", \"birthdate\": \"2000-11-22\"}"
                  )
              }
          )
      ) @RequestBody FindUserEmailRequestDto findUserEmailRequestDto) {

    try {
      FindUserEmailResponseDto findUserEmailResponseDto = userService.findUserEmail(
          findUserEmailRequestDto);
      return ResponseEntity.ok(findUserEmailResponseDto);

    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
    }
  }

  // 이메일로 임시 비밀번호 발급
  @PostMapping("/find/password")
  @Operation(
      summary = "임시 비밀번호 발급",
      description = "사용자의 이메일로 임시 비밀번호를 발급합니다.")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "임시 비밀번호 발급 성공"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "이메일 전송 실패 또는 임시 비밀번호 발급 실패")
  })
  public ResponseEntity<?> findUserPassword(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "FindPasswordExample",
                      value = "{\"email\": \"example@naver.com\"}"
                  )
              }
          )
      ) @RequestBody FindPasswordRequestDto findPasswordRequestDto) {
    try {
      userService.findUserPassword(findPasswordRequestDto);
      return ResponseEntity.ok("임시 비밀번호 발급 성공");

    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

    } catch (MailSendException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송에 실패했습니다.");

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("임시 비밀번호 발급에 실패했습니다.");
    }
  }
}
