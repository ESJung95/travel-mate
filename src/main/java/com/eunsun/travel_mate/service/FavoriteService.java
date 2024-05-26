package com.eunsun.travel_mate.service;

import com.eunsun.travel_mate.repository.FavoriteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class FavoriteService {

  private final FavoriteRepository favoriteRepository;

}
