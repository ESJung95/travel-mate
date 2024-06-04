package com.eunsun.travel_mate.util;

import java.util.Random;

public class RandomUtil {
  private static final String CHAR_POOL = "abcdefghijklmnopqrstuvwxyz0123456789";
  private static final int CODE_LENGTH = 6; // 인증 코드 길이
  private static final int PASSWORD_LENGTH = 10; // 임시 비밀번호 길이


  // 이메일 인증번호 생성
  public static String generateRandomCode() {
    StringBuilder sb = new StringBuilder();
    Random random = new Random();

    for (int i = 0; i < CODE_LENGTH; i++) {
      int index = random.nextInt(CHAR_POOL.length());
      sb.append(CHAR_POOL.charAt(index));
    }

    return sb.toString();
  }

  // 임시 비밀번호 생성
  public static String generateTemporaryPassword() {
    StringBuilder sb = new StringBuilder();

    // 비밀번호에 포함될 문자 집합
    String uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String lowercaseChars = "abcdefghijklmnopqrstuvwxyz";
    String digitChars = "0123456789";
    String specialChars = "!@#$%^&*";

    // 비밀번호에 포함될 문자 유형 선택
    String allChars = uppercaseChars + lowercaseChars + digitChars + specialChars;

    // 비밀번호에 반드시 포함되어야 할 문자 유형
    sb.append(getRandomChar(uppercaseChars));
    sb.append(getRandomChar(lowercaseChars));
    sb.append(getRandomChar(digitChars));
    sb.append(getRandomChar(specialChars));

    // 나머지 문자 랜덤 선택
    for (int i = 4; i < PASSWORD_LENGTH; i++) {
      sb.append(getRandomChar(allChars));
    }

    return sb.toString();
 }

  private static char getRandomChar(String chars) {
    Random random = new Random();
    int index = random.nextInt(chars.length());
    return chars.charAt(index);
  }
}
