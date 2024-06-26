package com.eunsun.travel_mate.repository.jpa;

import com.eunsun.travel_mate.domain.User;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  // 회원 가입 시 이메일 중복 확인 -> DB에 이메일 존재?
  boolean existsByEmail(String email);

  // 로그인 시 받아온 이메일 조회 -> 존재하면 User return
  Optional<User> findByEmail(String email);

  // 이름과 생년월일로 User 정보 조회
  Optional<User> findByNameAndBirthdate(String name, LocalDate birthdate);

  // 사용자 이름 조회
  Optional<User> findById(Long userId);

}
