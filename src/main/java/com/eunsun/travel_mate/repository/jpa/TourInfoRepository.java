package com.eunsun.travel_mate.repository.jpa;

import com.eunsun.travel_mate.domain.tourInfo.TourInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourInfoRepository extends JpaRepository<TourInfo, Long> {

}
