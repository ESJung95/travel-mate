package com.eunsun.travel_mate.service;

import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.dto.SignupDto;
import com.eunsun.travel_mate.repository.UserRepository;
import com.eunsun.travel_mate.util.RandomUtil;
import com.eunsun.travel_mate.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
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
  public boolean isEmailDuplicated(String email) {
    return userRepository.existsByEmail(email);
  }


  // 인증 코드 생성
  public String generateVerificationCode() {
    return RandomUtil.generateRandomCode();
  }

  // 이메일 중복 확인 & 인증 코드 전송
  public void checkEmailAndSendCode(String email, HttpServletRequest request) {
    if (isEmailDuplicated(email)) {
      throw new IllegalArgumentException("사용 중인 이메일");
    }

    String verificationCode = generateVerificationCode();
    mailService.sendVerificationEmail(email, verificationCode);
    SessionUtil.setVerificationCode(request, verificationCode);
  }

  // 이메일 인증 코드 확인
  public boolean verifyEmailCode(
      String verificationCode,
      HttpServletRequest request) {
    String storedVerificationCode = SessionUtil.getVerificationCode(request);

    if (storedVerificationCode != null && storedVerificationCode.equals(verificationCode)) {
      SessionUtil.setEmailVerified(request, true);
      return true;
    } else {
      return false;
    }
  }

  // 회원 정보 저장
  public User createUser(
      SignupDto signupDto,
      HttpServletRequest request) {

    Boolean isEmailVerified = (Boolean) request.getSession().getAttribute("isEmailVerified");
    if (isEmailVerified == null || !isEmailVerified) {
      throw new IllegalStateException("이메일 인증을 안함");
    }

    // 비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(signupDto.getPassword());

    User user = User.builder()
          .email(signupDto.getEmail())
          .password(encodedPassword)
          .name(signupDto.getName())
          .birthdate(signupDto.getBirthdate())
          .build();

    log.info("회원 가입 정보 저장 성공");
    return userRepository.save(user);
    }
}
