package com.eunsun.travel_mate.service;

import com.eunsun.travel_mate.domain.TokenBlacklist;
import com.eunsun.travel_mate.dto.request.TokenBlacklistRequestDto;
import com.eunsun.travel_mate.repository.TokenBlacklistRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenBlacklistService {

  private final TokenBlacklistRepository tokenBlacklistRepository;

  // 토큰을 블랙리스트에 추가
  public void addToBlacklist(TokenBlacklistRequestDto tokenBlacklistRequestDto) {
    log.info(tokenBlacklistRequestDto.getToken());

    try {
      TokenBlacklist tokenBlacklist = TokenBlacklistRequestDto.toEntity(
          tokenBlacklistRequestDto.getToken(), tokenBlacklistRequestDto.getExpiredTime());
      tokenBlacklistRepository.save(tokenBlacklist);
      log.info("토큰이 블랙리스트에 추가되었습니다! 토큰 유효시간 : {}", tokenBlacklistRequestDto.getExpiredTime());
    } catch (Exception e) {
      log.error("블랙리스트 추가 중 오류 발생", e);
      throw e;
    }
  }

  // 토큰이 블랙리스트에 존재하는지 확인
  public boolean isTokenBlacklisted(String token) {
    return tokenBlacklistRepository.existsByToken(token);
  }

  // 유효 시간이 지난 토큰 삭제 - 스케줄링 작업
  @Scheduled(fixedRate = 120000) // 1시간마다 실행 = 3600000
  public void cleanUpExpiredTokens() {
    LocalDateTime now = LocalDateTime.now();
    List<TokenBlacklist> expiredTokens = tokenBlacklistRepository.findAllByExpiredTimeBefore(now);

    if (!expiredTokens.isEmpty()) {
      tokenBlacklistRepository.deleteAll(expiredTokens);
      log.info("만료된 토큰이 블랙리스트에서 삭제되었습니다.");

      // 만료된 토큰들을 로그로 출력
      for (TokenBlacklist token : expiredTokens) {
        log.info("Expired Token: {}", token.getToken());
      }
    }
  }
}
