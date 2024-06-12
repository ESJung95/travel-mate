package com.eunsun.travel_mate.repository.jpa;

import com.eunsun.travel_mate.domain.TokenBlacklist;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {

  boolean existsByToken(String token);

  List<TokenBlacklist> findAllByExpiredTimeBefore(LocalDateTime dateTime);
}
