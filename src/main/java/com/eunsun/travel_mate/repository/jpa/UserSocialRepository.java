package com.eunsun.travel_mate.repository.jpa;

import com.eunsun.travel_mate.domain.UserSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSocialRepository extends JpaRepository<UserSocial, Long> {

}
