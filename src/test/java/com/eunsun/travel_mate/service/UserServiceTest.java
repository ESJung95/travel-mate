package com.eunsun.travel_mate.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.dto.request.SignupRequestDto;
import com.eunsun.travel_mate.dto.response.SignupResponseDto;
import com.eunsun.travel_mate.repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private MailService mailService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  @Test
  @DisplayName("중복된 이메일이 DB에 있는 경우")
  void isEmailDuplicated_true() {
    // given
    String email = "test@example.com";
    when(userRepository.existsByEmail(email)).thenReturn(true);

    // when
    boolean result = userService.checkEmailDuplicated(email);

    // then
    assertTrue(result);
    verify(userRepository, times(1)).existsByEmail(email);
  }

  @Test
  @DisplayName("중복된 이메일이 DB에 없는 경우")
  void isEmailDuplicated_false() {
    // given
    String email = "test@example.com";
    when(userRepository.existsByEmail(email)).thenReturn(false);

    // when
    boolean result = userService.checkEmailDuplicated(email);

    // then
    assertFalse(result);
    verify(userRepository, times(1)).existsByEmail(email);
  }

  @Test
  @DisplayName("인증 코드 생성이 조건에 맞는지")
  void generateVerificationCode() {

    // given
    int expectedCodeLength = 6;
    String charPool = "abcdefghijklmnopqrstuvwxyz0123456789";

    // when
    String verificationCode = userService.generateVerificationCode();

    // then
    assertNotNull(verificationCode);
    assertAll("Verification code properties",
        () -> assertEquals(expectedCodeLength, verificationCode.length(), "인증 코드의 길이가 일치해야 합니다."),
        () -> assertTrue(verificationCode.chars().allMatch(c -> charPool.contains(String.valueOf((char) c))), "인증 코드는 허용된 문자만 포함해야 합니다.")
    );
  }

  @Test
  @DisplayName("인증 코드 전송 성공")
  void isSendVerificationCode_true() {

    // given
    String email = "test@example.com";
    String verificationCode = "123456";

    // when
    boolean result = userService.sendVerificationCode(email, verificationCode);

    // then
    assertTrue(result);
    verify(mailService, times(1)).sendVerificationEmail(eq(email), eq(verificationCode));
  }

  @Test
  @DisplayName("인증 코드 전송 실패")
  void isSendVerificationCode_false() {

    // given
    String email = "test@example.com";
    String verificationCode = "123456";
    doThrow(new RuntimeException("메일 전송 오류")).when(mailService).sendVerificationEmail(email, verificationCode);

    // when
    boolean result = userService.sendVerificationCode(email, verificationCode);

    // then
    assertFalse(result);
    verify(mailService, times(1)).sendVerificationEmail(eq(email), eq(verificationCode));
  }

  @Test
  @DisplayName("인증코드가 일치")
  void isVerificationCodeMatch_true() {
    // given
    String storedVerificationCode = "abc123";
    String userInputVerificationCode = "abc123";

    // when
    boolean result = userService.verifyEmailCode(userInputVerificationCode, storedVerificationCode);

    // then
    assertTrue(result);
  }

  @Test
  @DisplayName("인증코드가 일치하지 않음")
  void isVerificationCodeMatch_false() {

    // given
    String storedVerificationCode = "abc123";
    String userInputVerificationCode = "invalid";

    // when
    boolean result = userService.verifyEmailCode(userInputVerificationCode, storedVerificationCode);

    // then
    assertFalse(result);
  }
  @Test
  @DisplayName("회원 가입 정보 저장 성공")
  void signup() {
    // given
    String email = "test@example.com";
    String password = "ABCde123@!";
    String name = "test";
    LocalDate birthdate = LocalDate.of(2000, 11, 22);

    SignupRequestDto signupRequestDto = SignupRequestDto.builder()
        .email(email)
        .password(password)
        .name(name)
        .birthdate(birthdate)
        .build();

    String encodedPassword = "encodedPassword";
    when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

    User user = SignupRequestDto.toEntity(signupRequestDto, encodedPassword);
    when(userRepository.save(any(User.class))).thenReturn(user);

    // when
    SignupResponseDto signupResponseDto = userService.signup(signupRequestDto);

    // then
    verify(passwordEncoder, times(1)).encode(password);
    verify(userRepository, times(1)).save(any(User.class));
    assertNotNull(signupResponseDto);
    assertEquals(email, signupResponseDto.getEmail());
    assertEquals(name, signupResponseDto.getName());
  }
}
