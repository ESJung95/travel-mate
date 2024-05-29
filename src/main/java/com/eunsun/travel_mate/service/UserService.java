package com.eunsun.travel_mate.service;

import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.dto.SignupDto;
import com.eunsun.travel_mate.repository.UserRepository;
import com.eunsun.travel_mate.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  public User signup(
      SignupDto signupDto) {

    // 비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(signupDto.getPassword());
    User user = SignupDto.toEntity(signupDto, encodedPassword);

    User savedUser = userRepository.save(user);
    log.info("회원 가입 정보 저장 성공");

    return savedUser;
  }
}
