package com.eunsun.travel_mate.service;

import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.dto.request.SignupRequestDto;
import com.eunsun.travel_mate.dto.response.LoginResponseDto;
import com.eunsun.travel_mate.dto.response.SignupResponseDto;
import com.eunsun.travel_mate.repository.UserRepository;
import com.eunsun.travel_mate.util.RandomUtil;
import com.eunsun.travel_mate.util.UserUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final MailService mailService;

  // 이메일 중복 확인
  public boolean checkEmailDuplicated(String email) {
    return userRepository.existsByEmail(email);
  }

  // 인증 코드 생성
  public String generateVerificationCode() {
    return RandomUtil.generateRandomCode();
  }

  // 인증 코드 전송
  public boolean sendVerificationCode(String email, String verificationCode) {
    try {
      mailService.sendVerificationEmail(email, verificationCode);
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
    User user = UserUtils.toEntity(signupRequestDto, encodedPassword);

    User savedUser = userRepository.save(user);
    log.info("회원 가입 정보 저장 성공");

    return UserUtils.toSignupResponseDto(savedUser);
  }

  // 로그인 - 이메일 조회 + 비밀번호 일치 확인
  public LoginResponseDto loginUser(String email, String password) {
    log.info("로그인 요청 - 이메일: {}", email);

    // 이메일로 User 조회
    Optional<User> optionalUser =  userRepository.findByEmail(email);

    User user = null;
    if (optionalUser.isPresent()) {
      user = optionalUser.get();
      log.info("사용자 조회 성공 - 사용자 ID: {}", user.getEmail());
    } else {
      log.info("사용자 조회 실패 - 이메일: {}", email);
      throw new UsernameNotFoundException("가입된 사용자가 없습니다.");
    }

    // 비밀번호 일치 확인
    String encodedPassword = passwordEncoder.encode(password);
    log.info("입력된 비밀번호 암호화 - 암호화된 비밀번호: {}", encodedPassword);

    String storedEncodedPassword = user.getPassword();
    if (!passwordEncoder.matches(password, storedEncodedPassword)) {
      log.warn("비밀번호 불일치 - 사용자 ID: {}", user.getEmail());
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    String token = generateToken(user);

    return UserUtils.createLoginResponse(token, user.getName());
  }

  private String generateToken(User user) {
    log.info("토큰 생성 성공 - 사용자 ID: {}", user.getEmail());

    return "토큰";
  }
}
