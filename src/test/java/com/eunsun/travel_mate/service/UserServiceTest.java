package com.eunsun.travel_mate.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eunsun.travel_mate.component.MailComponent;
import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.dto.request.FindPasswordRequestDto;
import com.eunsun.travel_mate.dto.request.FindUserEmailRequestDto;
import com.eunsun.travel_mate.dto.request.SignupRequestDto;
import com.eunsun.travel_mate.dto.request.UserNameUpdateRequestDto;
import com.eunsun.travel_mate.dto.request.UserPasswordUpdateRequestDto;
import com.eunsun.travel_mate.dto.response.FindUserEmailResponseDto;
import com.eunsun.travel_mate.dto.response.LoginResponseDto;
import com.eunsun.travel_mate.dto.response.SignupResponseDto;
import com.eunsun.travel_mate.dto.response.TokenDetailDto;
import com.eunsun.travel_mate.dto.response.UserNameUpdateResponseDto;
import com.eunsun.travel_mate.dto.response.UserResponseDto;
import com.eunsun.travel_mate.exception.UserNotFoundException;
import com.eunsun.travel_mate.repository.UserRepository;
import com.eunsun.travel_mate.security.JwtTokenProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private MailComponent mailComponent;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @InjectMocks
  private UserService userService;

  private User user;
  private String email;
  private String password;

  @BeforeEach
  void setUp() {
    email = "test@example.com";
    password = "password";
    String encodedPassword = "encodedPassword";
    user = User.builder()
        .email(email)
        .password(encodedPassword)
        .userId(1L)
        .build();
  }
  @Test
  @DisplayName("중복된 이메일이 DB에 있는 경우")
  void isEmailDuplicated_true() {
    // given
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
    verify(mailComponent, times(1)).sendVerificationEmail(eq(email), eq(verificationCode));
  }

  @Test
  @DisplayName("인증 코드 전송 실패")
  void isSendVerificationCode_false() {

    // given
    String email = "test@example.com";
    String verificationCode = "123456";
    doThrow(new RuntimeException("메일 전송 오류")).when(mailComponent).sendVerificationEmail(email, verificationCode);

    // when
    boolean result = userService.sendVerificationCode(email, verificationCode);

    // then
    assertFalse(result);
    verify(mailComponent, times(1)).sendVerificationEmail(eq(email), eq(verificationCode));
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

  @Test
  @DisplayName("로그인 성공")
  void loginUser_success() {
    // given
    String encodedPassword = "encodedPassword";
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

    LocalDateTime loginTime = LocalDateTime.now();
    LocalDateTime tokenExpiryTime = loginTime.plusHours(1);
    TokenDetailDto tokenDetailDto = new TokenDetailDto("token", loginTime, tokenExpiryTime);

    when(jwtTokenProvider.generateToken(anyLong(), any())).thenReturn(tokenDetailDto);

    // when
    LoginResponseDto loginResponseDto = userService.loginUser(email, password);

    // then
    assertNotNull(loginResponseDto);
    assertEquals(email, loginResponseDto.getEmail());
    verify(userRepository, times(1)).findByEmail(email);
    verify(passwordEncoder, times(1)).matches(password, user.getPassword());
    verify(jwtTokenProvider, times(1)).generateToken(anyLong(), any());
  }

  @Test
  @DisplayName("로그인 실패 = 사용자를 찾을 수 없음")
  void loginUser_userNotFound() {
    // given
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // when
    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userService.loginUser(email, password));

    // then
    assertNotNull(exception);
    assertEquals("가입된 사용자가 없습니다. : " + email, exception.getMessage());
    verify(userRepository, times(1)).findByEmail(email);
    verify(passwordEncoder, never()).matches(anyString(), anyString());
    verify(jwtTokenProvider, never()).generateToken(anyLong(), any());
  }

  @Test
  @DisplayName("로그인 실패 = 유효하지 않은 비밀번호")
  void loginUser_invalidPassword() {
    // given
    String encodedPassword = "encodedPassword";
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

    // when
    BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> userService.loginUser(email, password));

    // then
    assertNotNull(exception);
    assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    verify(userRepository, times(1)).findByEmail(email);
    verify(passwordEncoder, times(1)).matches(password, user.getPassword());
    verify(jwtTokenProvider, never()).generateToken(anyLong(), any());
  }


  @Test
  @DisplayName("사용자 ID로 사용자 정보 조회 성공")
  void getUserById_success() {
    // given
    Long userId = user.getUserId();
    String name = "John Doe";
    LocalDate birthdate = LocalDate.of(1990, 1, 1);

    User userWithAdditionalInfo = User.builder()
        .userId(userId)
        .email(email)
        .name(name)
        .birthdate(birthdate)
        .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(userWithAdditionalInfo));

    // when
    UserResponseDto userResponseDto = userService.getUserById(userId);

    // then
    assertNotNull(userResponseDto);
    assertEquals(email, userResponseDto.getEmail());
    assertEquals(name, userResponseDto.getName());
    assertEquals(birthdate, userResponseDto.getBirthdate());
    verify(userRepository, times(1)).findById(userId);
  }

  @Test
  @DisplayName("사용자 ID로 사용자 정보 조회 실패 - 사용자를 찾을 수 없음")
  void getUserById_userNotFound() {
    // given
    Long userId = user.getUserId();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // when
    UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));

    // then
    assertNotNull(exception);
    assertEquals("사용자를 찾을 수 없습니다 : userId = " + userId, exception.getMessage());
    verify(userRepository, times(1)).findById(userId);
  }

  @Test
  @DisplayName("사용자 이름 수정 성공")
  void updateUserName_success() {
    // given
    Long userId = 1L;
    String oldName = "처음 이름";
    String newName = "바꾼 이름";

    User userWithOldName = User.builder()
        .userId(userId)
        .email(email)
        .password(user.getPassword())
        .name(oldName)
        .build();

    UserNameUpdateRequestDto userNameUpdateRequestDto = new UserNameUpdateRequestDto();
    userNameUpdateRequestDto.setName(newName);

    when(userRepository.findById(userId)).thenReturn(Optional.of(userWithOldName));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    UserNameUpdateResponseDto responseDto = userService.updateUserName(userId, userNameUpdateRequestDto);

    // then
    assertNotNull(responseDto);
    assertEquals(newName, responseDto.getName());
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("사용자 이름 수정 실패 - 사용자를 찾을 수 없음")
  void updateUserName_userNotFound() {
    // given
    Long userId = user.getUserId();
    UserNameUpdateRequestDto userNameUpdateRequestDto = new UserNameUpdateRequestDto();
    userNameUpdateRequestDto.setName("이름");

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // when
    UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.updateUserName(userId, userNameUpdateRequestDto));

    // then
    assertNotNull(exception);
    assertEquals("사용자를 찾을 수 없습니다 : userId = " + userId, exception.getMessage());
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("사용자 비밀번호 수정 성공")
  void updateUserPassword_success() {
    // given
    Long userId = user.getUserId();
    String currentPassword = "oldPassword";
    String newPassword = "newPassword";
    String confirmPassword = "newPassword";

    User userWithOldPassword = User.builder()
        .userId(userId)
        .email(email)
        .password(passwordEncoder.encode(currentPassword))
        .build();

    UserPasswordUpdateRequestDto requestDto = UserPasswordUpdateRequestDto.builder()
        .currentPassword(currentPassword)
        .newPassword(newPassword)
        .confirmPassword(confirmPassword)
        .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(userWithOldPassword));
    when(passwordEncoder.matches(currentPassword, userWithOldPassword.getPassword())).thenReturn(true);

    // when
    userService.updateUserPassword(userId, requestDto);

    // then
    verify(userRepository, times(1)).findById(userId);
    verify(passwordEncoder, times(1)).matches(currentPassword, userWithOldPassword.getPassword());
    verify(passwordEncoder, times(1)).encode(newPassword);
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("사용자 비밀번호 수정 실패 - 사용자를 찾을 수 없음")
  void updateUserPassword_userNotFound() {
    // given
    Long userId = user.getUserId();
    UserPasswordUpdateRequestDto requestDto = UserPasswordUpdateRequestDto.builder()
        .currentPassword("oldPassword")
        .newPassword("newPassword")
        .confirmPassword("newPassword")
        .build();

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // when
    UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.updateUserPassword(userId, requestDto));

    // then
    assertNotNull(exception);
    assertEquals("사용자를 찾을 수 없습니다 : userId = " + userId, exception.getMessage());
    verify(userRepository, times(1)).findById(userId);
    verify(passwordEncoder, never()).matches(anyString(), anyString());
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("사용자 비밀번호 수정 실패 - 현재 비밀번호 불일치")
  void updateUserPassword_currentPasswordNotMatch() {
    // given
    Long userId = user.getUserId();
    String currentPassword = "oldPassword";
    String newPassword = "newPassword";
    String confirmPassword = "newPassword";

    String encodedDifferentPassword = "encodedDifferentPassword";
    User userWithOldPassword = User.builder()
        .userId(userId)
        .email(email)
        .password(encodedDifferentPassword)
        .build();

    UserPasswordUpdateRequestDto requestDto = UserPasswordUpdateRequestDto.builder()
        .currentPassword(currentPassword)
        .newPassword(newPassword)
        .confirmPassword(confirmPassword)
        .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(userWithOldPassword));
    when(passwordEncoder.matches(currentPassword, userWithOldPassword.getPassword())).thenReturn(false);

    // when
    BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> userService.updateUserPassword(userId, requestDto));

    // then
    assertNotNull(exception);
    assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    verify(userRepository, times(1)).findById(userId);
    verify(passwordEncoder, times(1)).matches(currentPassword, userWithOldPassword.getPassword());
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any(User.class));
  }


  @Test
  @DisplayName("사용자 비밀번호 수정 실패 - 새로운 비밀번호와 확인 불일치")
  void updateUserPassword_newPasswordNotMatchConfirm() {
    // given
    Long userId = user.getUserId();
    String currentPassword = "oldPassword";
    String newPassword = "newPassword";
    String confirmPassword = "differentPassword";

    String encodedDifferentPassword = "encodedDifferentPassword";
    User userWithOldPassword = User.builder()
        .userId(userId)
        .email(email)
        .password(encodedDifferentPassword)
        .build();

    UserPasswordUpdateRequestDto requestDto = UserPasswordUpdateRequestDto.builder()
        .currentPassword(currentPassword)
        .newPassword(newPassword)
        .confirmPassword(confirmPassword)
        .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(userWithOldPassword));
    when(passwordEncoder.matches(currentPassword, userWithOldPassword.getPassword())).thenReturn(true);

    // when
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.updateUserPassword(userId, requestDto));

    // then
    assertNotNull(exception);
    assertEquals("새로운 비밀번호와 비밀번호 확인이 일치하지 않습니다.", exception.getMessage());
    verify(userRepository, times(1)).findById(userId);
    verify(passwordEncoder, times(1)).matches(currentPassword, userWithOldPassword.getPassword());
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("사용자 비밀번호 수정 실패 - 새로운 비밀번호가 현재 비밀번호와 동일")
  void updateUserPassword_newPasswordSameAsCurrent() {
    // given
    Long userId = user.getUserId();
    String currentPassword = "oldPassword";
    String newPassword = "oldPassword";
    String confirmPassword = "oldPassword";

    String encodedDifferentPassword = "encodedDifferentPassword";
    User userWithOldPassword = User.builder()
        .userId(userId)
        .email(email)
        .password(encodedDifferentPassword)
        .build();

    UserPasswordUpdateRequestDto requestDto = UserPasswordUpdateRequestDto.builder()
        .currentPassword(currentPassword)
        .newPassword(newPassword)
        .confirmPassword(confirmPassword)
        .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(userWithOldPassword));
    when(passwordEncoder.matches(currentPassword, userWithOldPassword.getPassword())).thenReturn(true);

    // when
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.updateUserPassword(userId, requestDto));

    // then
    assertNotNull(exception);
    assertEquals("새로운 비밀번호는 현재 비밀번호와 달라야 합니다.", exception.getMessage());
    verify(userRepository, times(1)).findById(userId);
    verify(passwordEncoder, times(1)).matches(currentPassword, userWithOldPassword.getPassword());
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("사용자 정보 삭제 성공")
  void deleteUser_success() {
    // given
    Long userId = user.getUserId();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    // when
    userService.deleteUser(userId);

    // then
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, times(1)).delete(user);
  }

  @Test
  @DisplayName("사용자 정보 삭제 실패 - 사용자를 찾을 수 없음")
  void deleteUser_userNotFound() {
    // given
    Long userId = user.getUserId();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // when
    UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));

    // then
    assertNotNull(exception);
    assertEquals("사용자를 찾을 수 없습니다 : userId = " + userId, exception.getMessage());
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, never()).delete(any(User.class));
  }

  @Test
  @DisplayName("이름과 생년월일로 이메일 찾기 성공")
  void findUserEmail_success() {
    // given
    String name = "이름";
    LocalDate birthdate = LocalDate.of(2000, 11, 22);

    User user = User.builder()
        .name(name)
        .email(email)
        .birthdate(birthdate)
        .build();

    FindUserEmailRequestDto requestDto = FindUserEmailRequestDto.builder()
        .name(name)
        .birthdate(birthdate)
        .build();

    when(userRepository.findByNameAndBirthdate(name, birthdate)).thenReturn(Optional.of(user));

    // when
    FindUserEmailResponseDto responseDto = userService.findUserEmail(requestDto);

    // then
    assertNotNull(responseDto);
    assertEquals(name, responseDto.getName());
    assertEquals(email, responseDto.getEmail());
    verify(userRepository, times(1)).findByNameAndBirthdate(name, birthdate);
  }

  @Test
  @DisplayName("이름과 생년월일로 이메일 찾기 실패 - 사용자를 찾을 수 없음")
  void findUserEmail_userNotFound() {
    // given
    String name = "이름";
    LocalDate birthdate = LocalDate.of(1990, 1, 1);

    FindUserEmailRequestDto requestDto = FindUserEmailRequestDto.builder()
        .name(name)
        .birthdate(birthdate)
        .build();

    when(userRepository.findByNameAndBirthdate(name, birthdate)).thenReturn(Optional.empty());

    // when
    UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.findUserEmail(requestDto));

    // then
    assertNotNull(exception);
    assertEquals("이메일을 찾을 수 없습니다.", exception.getMessage());
    verify(userRepository, times(1)).findByNameAndBirthdate(name, birthdate);
  }

  @Test
  @DisplayName("임시 비밀번호 생성 및 이메일 전송 성공")
  void findUserPassword_success() {
    // given
    String email = "test@example.com";
    FindPasswordRequestDto requestDto = FindPasswordRequestDto.builder()
        .email(email)
        .build();

    User user = User.builder()
        .email(email)
        .password("oldPassword")
        .build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    userService.findUserPassword(requestDto);

    // then
    verify(userRepository, times(1)).findByEmail(email);
    verify(passwordEncoder, times(1)).encode(anyString());
    verify(userRepository, times(1)).save(any(User.class));
    verify(mailComponent, times(1)).sendTemporaryPasswordEmail(eq(email), anyString());
  }

  @Test
  @DisplayName("임시 비밀번호 생성 및 이메일 전송 실패 - 사용자를 찾을 수 없음")
  void findUserPassword_userNotFound() {
    // given
    String email = "test@example.com";
    FindPasswordRequestDto requestDto = FindPasswordRequestDto.builder()
        .email(email)
        .build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // when
    UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.findUserPassword(requestDto));

    // then
    assertNotNull(exception);
    assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
    verify(userRepository, times(1)).findByEmail(email);
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any(User.class));
    verify(mailComponent, never()).sendTemporaryPasswordEmail(anyString(), anyString());
  }

  @Test
  @DisplayName("임시 비밀번호 생성 및 이메일 전송 실패 - 이메일 전송 실패")
  void findUserPassword_mailSendFail() {
    // given
    String email = "test@example.com";
    FindPasswordRequestDto requestDto = FindPasswordRequestDto.builder()
        .email(email)
        .build();

    User user = User.builder()
        .email(email)
        .password("oldPassword")
        .build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    doThrow(new RuntimeException("메일 전송 오류")).when(mailComponent).sendTemporaryPasswordEmail(anyString(), anyString());

    // when
    MailSendException exception = assertThrows(MailSendException.class, () -> userService.findUserPassword(requestDto));

    // then
    assertNotNull(exception);
    assertEquals("이메일 전송에 실패했습니다.", exception.getMessage());
    assertInstanceOf(RuntimeException.class, exception.getCause());
    assertEquals("메일 전송 오류", exception.getCause().getMessage());
    verify(userRepository, times(1)).findByEmail(email);
    verify(passwordEncoder, times(1)).encode(anyString());
    verify(userRepository, times(1)).save(any(User.class));
    verify(mailComponent, times(1)).sendTemporaryPasswordEmail(eq(email), anyString());
  }


}
