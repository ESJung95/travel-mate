package com.eunsun.travel_mate.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eunsun.travel_mate.domain.TokenBlacklist;
import com.eunsun.travel_mate.dto.request.TokenBlacklistRequestDto;
import com.eunsun.travel_mate.repository.jpa.TokenBlacklistRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

  @Mock
  private TokenBlacklistRepository tokenBlacklistRepository;

  @InjectMocks
  private TokenBlacklistService tokenBlacklistService;

  @Test
  @DisplayName("토큰을 블랙리스트에 추가")
  void addToBlacklist() {
    // given
    String token = "testToken";
    LocalDateTime expiredTime = LocalDateTime.now().plusHours(1);
    TokenBlacklistRequestDto requestDto = new TokenBlacklistRequestDto(token, expiredTime);

    // when
    tokenBlacklistService.addToBlacklist(requestDto);

    // then
    verify(tokenBlacklistRepository, times(1)).save(any(TokenBlacklist.class));
  }

  @Test
  @DisplayName("토큰이 블랙리스트에 존재하는지 확인")
  void isTokenBlacklisted() {
    // given
    String token = "testToken";
    when(tokenBlacklistRepository.existsByToken(token)).thenReturn(true);

    // when
    boolean result = tokenBlacklistService.isTokenBlacklisted(token);

    // then
    assertTrue(result);
    verify(tokenBlacklistRepository, times(1)).existsByToken(token);
  }

  @Test
  @DisplayName("유효 시간이 지난 토큰 삭제")
  void cleanUpExpiredTokens() {
    // given
    LocalDateTime now = LocalDateTime.now();
    List<TokenBlacklist> expiredTokens = new ArrayList<>();
    expiredTokens.add(TokenBlacklistRequestDto.toEntity("expiredToken1", now.minusHours(1)));
    expiredTokens.add(TokenBlacklistRequestDto.toEntity("expiredToken2", now.minusHours(2)));
    when(tokenBlacklistRepository.findAllByExpiredTimeBefore(any(LocalDateTime.class))).thenReturn(expiredTokens);

    // when
    tokenBlacklistService.cleanUpExpiredTokens();

    // then
    verify(tokenBlacklistRepository, times(1)).findAllByExpiredTimeBefore(any(LocalDateTime.class));
    verify(tokenBlacklistRepository, times(1)).deleteAll(expiredTokens);
  }
}