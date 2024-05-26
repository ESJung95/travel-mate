package com.eunsun.travel_mate.service;

import com.eunsun.travel_mate.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

  private final UserRepository userRepository;

}
