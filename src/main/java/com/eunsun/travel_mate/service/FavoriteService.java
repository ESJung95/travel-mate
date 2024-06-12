package com.eunsun.travel_mate.service;

import com.eunsun.travel_mate.domain.Favorite;
import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.domain.tourInfo.TourInfo;
import com.eunsun.travel_mate.dto.request.CreateFavoriteRequestDto;
import com.eunsun.travel_mate.dto.response.CheckFavoriteResponseDto;
import com.eunsun.travel_mate.dto.response.CreateFavoriteResponseDto;
import com.eunsun.travel_mate.repository.jpa.FavoriteRepository;
import com.eunsun.travel_mate.repository.jpa.TourInfoRepository;
import com.eunsun.travel_mate.repository.jpa.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class FavoriteService {

  private final FavoriteRepository favoriteRepository;
  private final UserRepository userRepository;
  private final TourInfoRepository tourInfoRepository;

  // 여행 정보 좋아요 저장하기
  public CreateFavoriteResponseDto addFavorite(String userId, CreateFavoriteRequestDto createFavoriteRequestDto) {

    User user = userRepository.findById(Long.parseLong(userId))
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

    TourInfo tourInfo = tourInfoRepository.findById(createFavoriteRequestDto.getTourInfoId())
        .orElseThrow(() -> new EntityNotFoundException("여행지 정보를 찾을 수 없습니다."));

    Optional<Favorite> DuplicateCheckFavorite = favoriteRepository.findByUserAndTourInfoId(user, tourInfo);

    if (DuplicateCheckFavorite.isPresent()) {
      throw new RuntimeException("이미 좋아요를 누른 여행지 입니다.");
    }

    Favorite favorite = Favorite.create(user, tourInfo);
    Favorite savedFavorite = favoriteRepository.save(favorite);

    CreateFavoriteResponseDto responseDto = CreateFavoriteResponseDto.from(savedFavorite);
    log.info("여행 정보 좋아요 성공");

    return responseDto;
  }

  // 여행 정보 좋아요 한 것들 전체 조회
  public List<CheckFavoriteResponseDto> getFavoritesByUsername(String userId) {
    User user = userRepository.findById(Long.parseLong(userId))
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

    List<Favorite> favorites = favoriteRepository.findByUser(user);

    List<CheckFavoriteResponseDto> responseDtoList = favorites.stream()
        .map(CheckFavoriteResponseDto::from)
        .collect(Collectors.toList());

    log.info("여행 정보 좋아요 전체 조회 성공");
    return responseDtoList;
  }

  // 좋아요 한 여행 정보 favoriteId로 삭제
  public void deleteFavorite(Long favoriteId, String userId) {
    User user = userRepository.findById(Long.parseLong(userId))
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

    Favorite favorite = favoriteRepository.findById(favoriteId)
        .orElseThrow(() -> new RuntimeException("관심 여행지를 찾을 수 없습니다."));

    if (!favorite.getUser().getUserId().equals(user.getUserId())) {
      throw new RuntimeException("삭제 권한이 없습니다.");
    }

    favoriteRepository.delete(favorite);
  }
}
