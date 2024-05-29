package com.eunsun.travel_mate.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.dto.SignupDto;
import com.eunsun.travel_mate.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    assertThrows(IllegalArgumentException.class, () -> userService.checkEmailDuplicated(email));

    // then
    verify(userRepository, times(1)).existsByEmail(email);
  }

  @Test
  @DisplayName("중복된 이메일이 DB에 없는 경우")
  void isEmailDuplicated_false() {
    // given
    String email = "test@example.com";
    when(userRepository.existsByEmail(email)).thenReturn(false);

    // when
    assertDoesNotThrow(() -> userService.checkEmailDuplicated(email));

    // then
    verify(userRepository, times(1)).existsByEmail(email);
  }

  @Test
  @DisplayName("인증 코드가 옳게 생성되는지")
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
  @DisplayName("인증 코드가 잘 전송됬는지")
  void sendVerificationCode() {

    // given
    String email = "test@example.com";
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    when(request.getSession()).thenReturn(session);

    // when
    userService.sendVerificationCode(email, request);

    // then
    verify(mailService, times(1)).sendVerificationEmail(eq(email), anyString());
    verify(session, times(1)).setAttribute(eq("verificationCode"), anyString());
  }

  @Test
  @DisplayName("인증코드가 일치")
  void isVerificationCodeMatch_true() {
    // given
    String storedVerificationCode = "abc123";
    String userInputVerificationCode = "abc123";
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    when(request.getSession()).thenReturn(session);
    when(session.getAttribute("verificationCode")).thenReturn(storedVerificationCode);

    // when
    boolean result = userService.verifyEmailCode(userInputVerificationCode, request);

    // then
    assertTrue(result);
    verify(session, times(1)).setAttribute(eq("isEmailVerified"), eq(true));
  }

  @Test
  @DisplayName("인증코드가 일치하지 않음")
  void isVerificationCodeMatch_false() {

    // given
    String storedVerificationCode = "abc123";
    String userInputVerificationCode = "invalid";
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    when(request.getSession()).thenReturn(session);
    when(session.getAttribute("verificationCode")).thenReturn(storedVerificationCode);

    // when
    boolean result = userService.verifyEmailCode(userInputVerificationCode, request);

    // then
    assertFalse(result);
    verify(session, never()).setAttribute(eq("isEmailVerified"), anyBoolean());
  }

  @Test
  @DisplayName("회원 가입 정보 저장")
  void signup() {
    // given
    String email = "test@example.com";
    String password = "ABCde123@!";
    String name = "test";
    LocalDate birthdate = LocalDate.of(2000, 11, 22);

    SignupDto signupDto = SignupDto.builder()
        .email(email)
        .password(password)
        .name(name)
        .birthdate(birthdate)
        .build();

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    when(request.getSession()).thenReturn(session);
    when(session.getAttribute("isEmailVerified")).thenReturn(true);

    String encodedPassword = "encodedPassword";
    when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

    User user = SignupDto.toEntity(signupDto, encodedPassword);
    when(userRepository.save(any(User.class))).thenReturn(user);

    // when
    User savedUser = userService.signup(signupDto, request);

    // then
    verify(passwordEncoder, times(1)).encode(password);
    verify(userRepository, times(1)).save(any(User.class));
    assertEquals(email, savedUser.getEmail());
    assertEquals(encodedPassword, savedUser.getPassword());
    assertEquals(name, savedUser.getName());
    assertEquals(birthdate, savedUser.getBirthdate());
  }
}
