package com.eunsun.travel_mate.service;

import com.eunsun.travel_mate.domain.TokenBlacklist;
import com.eunsun.travel_mate.dto.request.TokenBlacklistRequestDto;
import com.eunsun.travel_mate.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
      log.info("토큰이 블랙리스트에 추가되었습니다 : {}", tokenBlacklistRequestDto.getToken());
    } catch (Exception e) {
      log.error("블랙리스트 추가 중 오류 발생", e);
      throw e;
    }
  }

  // 토큰이 블랙리스트에 존재하는지 확인
  public boolean isTokenBlacklisted(String token) {
    return tokenBlacklistRepository.existsByToken(token);
  }
}
