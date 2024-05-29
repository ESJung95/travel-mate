package com.eunsun.travel_mate.repository;

import com.eunsun.travel_mate.domain.User;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("이메일이 DB에 존재하는 경우")
  void existsByEmail() {

    // given
    User user = User.builder()
        .email("text@email.com")
        .birthdate(LocalDate.of(2000, 11, 22))
        .password("ABCd123@")
        .name("test")
        .build();
    userRepository.save(user);

    // when
    boolean exist = userRepository.existsByEmail("text@email.com");

    // then
    Assertions.assertThat(exist).isTrue();
  }

  @Test
  @DisplayName("이메일이 DB에 존재하지 않는 경우")
  void noneExistsByEmail() {
    // given

    // when
    boolean exist = userRepository.existsByEmail("text@email.com");

    // then
    Assertions.assertThat(exist).isFalse();
  }
}