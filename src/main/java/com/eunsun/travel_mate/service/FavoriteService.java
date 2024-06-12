package com.eunsun.travel_mate.service;

import com.eunsun.travel_mate.domain.Favorite;
import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.domain.tourInfo.TourInfo;
import com.eunsun.travel_mate.dto.request.FavoriteRequestDto;
import com.eunsun.travel_mate.dto.response.FavoriteResponseDto;
import com.eunsun.travel_mate.repository.jpa.FavoriteRepository;
import com.eunsun.travel_mate.repository.jpa.TourInfoRepository;
import com.eunsun.travel_mate.repository.jpa.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
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

  public FavoriteResponseDto addFavorite(String userId, FavoriteRequestDto favoriteRequestDto) {

    User user = userRepository.findById(Long.parseLong(userId))
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

    TourInfo tourInfo = tourInfoRepository.findById(favoriteRequestDto.getTourInfoId())
        .orElseThrow(() -> new EntityNotFoundException("여행지 정보를 찾을 수 없습니다."));

    Optional<Favorite> DuplicateCheckFavorite = favoriteRepository.findByUserAndTourInfoId(user, tourInfo);

    if (DuplicateCheckFavorite.isPresent()) {
      throw new RuntimeException("이미 좋아요를 누른 여행지 입니다.");
    }

    Favorite favorite = Favorite.create(user, tourInfo);
    Favorite savedFavorite = favoriteRepository.save(favorite);

    FavoriteResponseDto responseDto = FavoriteResponseDto.from(savedFavorite);
    log.info("여행 정보 좋아요 성공");

    return responseDto;
  }
}
