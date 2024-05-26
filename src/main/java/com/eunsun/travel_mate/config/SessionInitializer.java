package com.eunsun.travel_mate.config;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class SessionInitializer implements HttpSessionListener {

  @Override
  public void sessionCreated(HttpSessionEvent se) {
    // 세션이 생성될 때 초기화 로직 추가
    se.getSession().setAttribute("isEmailVerified", false);
  }
}
