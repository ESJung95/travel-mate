package com.eunsun.travel_mate.util;

import jakarta.servlet.http.HttpServletRequest;

public class SessionUtil {
  public static void setVerificationCode(HttpServletRequest request, String verificationCode) {
    request.getSession().setAttribute("verificationCode", verificationCode);
  }

  public static String getVerificationCode(HttpServletRequest request) {
    return (String) request.getSession().getAttribute("verificationCode");
  }

  public static void setEmailVerified(HttpServletRequest request, boolean isVerified) {
    request.getSession().setAttribute("isEmailVerified", isVerified);
    request.getSession().setMaxInactiveInterval(30 * 60);
  }

  public static boolean isEmailVerified(HttpServletRequest request) {
    Boolean isVerified = (Boolean) request.getSession().getAttribute("isEmailVerified");
    return isVerified != null && isVerified;
  }
}
