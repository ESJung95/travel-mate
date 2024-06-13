package com.eunsun.travel_mate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eunsun.travel_mate.domain.Favorite;
import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.domain.tourInfo.TourInfo;
import com.eunsun.travel_mate.dto.request.CreateFavoriteRequestDto;
import com.eunsun.travel_mate.dto.response.CheckFavoriteResponseDto;
import com.eunsun.travel_mate.dto.response.CreateFavoriteResponseDto;
import com.eunsun.travel_mate.repository.jpa.FavoriteRepository;
import com.eunsun.travel_mate.repository.jpa.TourInfoRepository;
import com.eunsun.travel_mate.repository.jpa.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

  @InjectMocks
  private FavoriteService favoriteService;

  @Mock
  private FavoriteRepository favoriteRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private TourInfoRepository tourInfoRepository;

  @Test
  @DisplayName("좋아요 기능 성공")
  void addFavorite_Success() {
    // given
    String userId = "1";
    Long tourInfoId = 1L;
    CreateFavoriteRequestDto requestDto = new CreateFavoriteRequestDto();
    requestDto.setTourInfoId(tourInfoId);
    User user = User.builder().userId(1L).build();
    TourInfo tourInfo = TourInfo.builder().tourInfoId(tourInfoId).build();
    Favorite favorite = Favorite.create(user, tourInfo);

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(tourInfoRepository.findById(tourInfoId)).thenReturn(Optional.of(tourInfo));
    when(favoriteRepository.findByUserAndTourInfoId(any(User.class), any(TourInfo.class))).thenReturn(Optional.empty());
    when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);

    // when
    CreateFavoriteResponseDto responseDto = favoriteService.addFavorite(userId, requestDto);

    // then
    assertEquals(favorite.getTourInfoId().getTourInfoId(), responseDto.getTourInfoId());
    verify(userRepository, times(1)).findById(anyLong());
    verify(tourInfoRepository, times(1)).findById(tourInfoId);
    verify(favoriteRepository, times(1)).findByUserAndTourInfoId(any(User.class), any(TourInfo.class));
    verify(favoriteRepository, times(1)).save(any(Favorite.class));
  }

  @Test
  @DisplayName("좋아요 기능 실패 - 사용자를 찾을 수 없음")
  void addFavorite_UserNotFound() {
    // given
    String userId = "1";
    CreateFavoriteRequestDto requestDto = new CreateFavoriteRequestDto();

    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when & then
    assertThrows(UsernameNotFoundException.class, () -> favoriteService.addFavorite(userId, requestDto));
    verify(userRepository, times(1)).findById(anyLong());
    verify(tourInfoRepository, never()).findById(anyLong());
    verify(favoriteRepository, never()).findByUserAndTourInfoId(any(User.class), any(TourInfo.class));
    verify(favoriteRepository, never()).save(any(Favorite.class));
  }

  @Test
  @DisplayName("사용자가 좋아요한 여행지 목록 조회 성공")
  void getFavoritesByUsername_Success() {
    // given
    String userId = "1";
    User user = User.builder().userId(1L).build();
    List<Favorite> favorites = Arrays.asList(
        Favorite.create(user, TourInfo.builder().tourInfoId(1L).build()),
        Favorite.create(user, TourInfo.builder().tourInfoId(2L).build())
    );

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(favoriteRepository.findByUser(any(User.class))).thenReturn(favorites);

    // when
    List<CheckFavoriteResponseDto> responseDtos = favoriteService.getFavoritesByUsername(userId);

    // then
    assertNotNull(responseDtos);
    assertEquals(favorites.size(), responseDtos.size());
    verify(userRepository, times(1)).findById(anyLong());
    verify(favoriteRepository, times(1)).findByUser(any(User.class));
  }

  @Test
  @DisplayName("사용자가 좋아요한 여행지 삭제 성공")
  void deleteFavorite_Success() {
    // given
    Long favoriteId = 1L;
    String userId = "1";
    User user = User.builder().userId(1L).build();
    Favorite favorite = Favorite.create(user, TourInfo.builder().tourInfoId(1L).build());

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(favoriteRepository.findById(anyLong())).thenReturn(Optional.of(favorite));

    // when
    favoriteService.deleteFavorite(favoriteId, userId);

    // then
    verify(userRepository, times(1)).findById(anyLong());
    verify(favoriteRepository, times(1)).findById(anyLong());
    verify(favoriteRepository, times(1)).delete(any(Favorite.class));
    // 사용자 권한 검사 확인
    assertEquals(user.getUserId(), favorite.getUser().getUserId());
  }
}