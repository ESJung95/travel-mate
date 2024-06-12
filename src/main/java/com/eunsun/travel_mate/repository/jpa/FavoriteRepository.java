package com.eunsun.travel_mate.repository.jpa;

import com.eunsun.travel_mate.domain.Favorite;
import com.eunsun.travel_mate.domain.User;
import com.eunsun.travel_mate.domain.tourInfo.TourInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

  Optional<Favorite> findByUserAndTourInfoId(User user, TourInfo tourInfo);

  List<Favorite> findByUser(User user);
}
