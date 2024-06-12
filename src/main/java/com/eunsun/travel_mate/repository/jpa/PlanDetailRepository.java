package com.eunsun.travel_mate.repository.jpa;

import com.eunsun.travel_mate.domain.PlanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanDetailRepository extends JpaRepository<PlanDetail, Long> {

}
