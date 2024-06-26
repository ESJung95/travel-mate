package com.eunsun.travel_mate.service;

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
import com.eunsun.travel_mate.repository.jpa.UserRepository;
import com.eunsun.travel_mate.security.JwtTokenProvider;
import com.eunsun.travel_mate.util.RandomUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final MailComponent mailComponent;
  private final JwtTokenProvider jwtTokenProvider;

  // 이메일 중복 확인
  public boolean checkEmailDuplicated(String email) {
    return userRepository.existsByEmail(email);
  }

  // 인증 코드 생성 : 이메일로 전송 될 인증 코드
  public String generateVerificationCode() {
    return RandomUtil.generateRandomCode();
  }

  // 인증 코드 전송
  public boolean sendVerificationCode(String email, String verificationCode) {
    try {
      mailComponent.sendVerificationEmail(email, verificationCode);
      return true;

    } catch (Exception e) {

      log.error("인증 코드 메일 전송 실패: {}", e.getMessage());
      return false;
    }
  }

  // 이메일 인증 코드 확인
  public boolean verifyEmailCode(
      String verificationCode, String storedVerificationCode) {

    return storedVerificationCode != null
        && storedVerificationCode.equals(verificationCode);
  }

  // 회원 정보 저장
  public SignupResponseDto signup(
      SignupRequestDto signupRequestDto) {

    // 비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
    User user = SignupRequestDto.toEntity(signupRequestDto, encodedPassword);

    User savedUser = userRepository.save(user);
    log.info("회원 가입 정보 저장 성공");

    return SignupResponseDto.toSignupResponseDto(savedUser);
  }

  // 로그인 : 이메일 조회 + 비밀번호 일치 확인
  public LoginResponseDto loginUser(String email, String password) {
    log.info("로그인 요청 - 이메일: {}", email);

    User user = findUserByEmail(email);
    validatePassword(password, user);

    TokenDetailDto tokenDetailDto = jwtTokenProvider.generateToken(user.getUserId(), user.getRole());

    return LoginResponseDto.createLoginResponse(
        tokenDetailDto.getToken(),
        user.getUserId(),
        user.getName(),
        user.getEmail(),
        user.getRole(),
        tokenDetailDto.getLoginTime(),
        tokenDetailDto.getTokenExpiryTime()
    );
  }

  // 이메일로 User 조회
  private User findUserByEmail(String email) {
    User user =  userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("가입된 사용자가 없습니다. : " + email));

    log.info("이메일로 User 정보 조회 성공");
    return user;
  }

  // 비밀번호 일치 확인
  private void validatePassword(String inputPassword, User user) {
    if (!passwordEncoder.matches(inputPassword, user.getPassword())) {

      throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
    }
  }

  // 사용자 정보 조회
  public UserResponseDto getUserById(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다 : userId = " + userId));

    UserResponseDto userResponseDto = UserResponseDto.createUserResponse(user.getEmail(), user.getName(), user.getBirthdate());
    log.info("사용자 ID 로 사용자 정보 조회 성공 : userId = {}", userId);

    return userResponseDto;

  }

  // 사용자 정보 수정 - 이름 변경
  public UserNameUpdateResponseDto updateUserName(Long userId, UserNameUpdateRequestDto userNameUpdateRequestDto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다 : userId = " + userId));

    String oldName = user.getName();
    String newName = userNameUpdateRequestDto.getName();

    user.setName(newName);
    userRepository.save(user);
    log.info("사용자 이름 수정 완료 : {} -> {}", oldName, newName);

    return new UserNameUpdateResponseDto(newName);
  }

  // 사용자 정보 수정 -  비밀번호 변경
  public void updateUserPassword(Long userId, UserPasswordUpdateRequestDto userPasswordUpdateRequestDto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다 : userId = " + userId));

    if (!passwordEncoder.matches(userPasswordUpdateRequestDto.getCurrentPassword(), user.getPassword())) {
      throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
    }

    if (!userPasswordUpdateRequestDto.getNewPassword().equals(userPasswordUpdateRequestDto.getConfirmPassword())) {
      throw new IllegalArgumentException("새로운 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
    }

    if (userPasswordUpdateRequestDto.getNewPassword().equals(userPasswordUpdateRequestDto.getCurrentPassword())) {
      throw new IllegalArgumentException("새로운 비밀번호는 현재 비밀번호와 달라야 합니다.");
    }
    user.setPassword(passwordEncoder.encode(userPasswordUpdateRequestDto.getNewPassword()));
    userRepository.save(user);
    log.info("사용자 비밀번호 변경 완료 : userId = {}", userId);
  }

  // 사용자 정보 삭제
  public void deleteUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다 : userId = " + userId));

    userRepository.delete(user);
    log.info("사용자 정보 삭제 완료 : userId = {}", userId);
  }

  // 이름과 생년월일로 User 아이디 찾기
  public FindUserEmailResponseDto findUserEmail(FindUserEmailRequestDto findUserEmailRequestDto) {

    Optional<User> optionalUser = userRepository.findByNameAndBirthdate(findUserEmailRequestDto.getName(), findUserEmailRequestDto.getBirthdate());
    if (optionalUser.isPresent()) { // 이름 + 생년월일이 일치하는게 존재하면
      User user = optionalUser.get();
      return FindUserEmailResponseDto.createFindUserEmailResponse(user.getName(), user.getEmail());
    } else {
      throw new UserNotFoundException("이메일을 찾을 수 없습니다.");
    }
  }

  @Transactional
  // 임시 비밀번호 생성 후 update -> 이메일로 전송
  public void findUserPassword(FindPasswordRequestDto findPasswordRequestDto) {

    // 이메일로 사용자 조회
    User user = userRepository.findByEmail(findPasswordRequestDto.getEmail())
        .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

    try {
      // 임시 비밀번호 생성
      String temporaryPassword = RandomUtil.generateTemporaryPassword();

      // 임시 비밀번호 저장
      user.setPassword(passwordEncoder.encode(temporaryPassword));
      userRepository.save(user);
      log.info("임시 비밀번호 발급 및 이메일 전송 완료");

      // 메일 전송
      mailComponent.sendTemporaryPasswordEmail(user.getEmail(), temporaryPassword);
    } catch (Exception e) {
      throw new MailSendException("이메일 전송에 실패했습니다.", e);
    }
  }
}
