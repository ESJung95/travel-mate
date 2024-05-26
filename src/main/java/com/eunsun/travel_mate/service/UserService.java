package com.eunsun.travel_mate.service;

import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.dto.SignupDto;
import com.eunsun.travel_mate.repository.UserRepository;
import com.eunsun.travel_mate.util.EmailUtil;
import com.eunsun.travel_mate.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final EmailUtil emailUtil;

  // 이메일 중복 확인
  public boolean isEmailDuplicated(String email) {
    return userRepository.existsByEmail(email);
  }

  // 인증 코드 생성
  public String generateVerificationCode() {

    String verificationCode = RandomUtil.generateRandomCode();
    return verificationCode;
  }

  // Todo 비밀번호 암호화
  String encodedPassword;

  // 회원 정보 저장
  public User createUser(SignupDto signupDto) {

    User user = User.builder()
          .email(signupDto.getEmail())
          .password(signupDto.getPassword())
          .name(signupDto.getName())
          .birthdate(signupDto.getBirthdate())
          .build();

    log.info("회원 가입 정보 저장 성공");
    return userRepository.save(user);
    }
}
