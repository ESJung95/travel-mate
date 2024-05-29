package com.eunsun.travel_mate.util;

import jakarta.servlet.http.HttpServletRequest;

public class SessionUtil {

  // 인증 코드 저장
  public static void setVerificationCode(HttpServletRequest request, String verificationCode) {
    request.getSession(true).setAttribute("verificationCode", verificationCode);
  }

  // 세션에 저장된 인증코드 가져오기
  public static String getVerificationCode(HttpServletRequest request) {
    return (String) request.getSession().getAttribute("verificationCode");
  }

  // 메일 인증 여부 저장 & 세션 유효 시간 설정
  public static void setEmailVerified(HttpServletRequest request, boolean isVerified) {
    request.getSession(true).setAttribute("isEmailVerified", isVerified);
    request.getSession(true).setMaxInactiveInterval(30 * 60); // 30분
  }
}
