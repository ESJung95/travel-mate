package com.eunsun.travel_mate.repository;

import com.eunsun.travel_mate.domain.AreaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaCodeRepository extends JpaRepository<AreaCode, Long> {

}
