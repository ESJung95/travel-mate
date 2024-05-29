package com.eunsun.travel_mate.util;

import java.util.Random;

public class RandomUtil {
  private static final String CHAR_POOL = "abcdefghijklmnopqrstuvwxyz0123456789";
  private static final int CODE_LENGTH = 6; // 인증 코드 길이

  public static String generateRandomCode() {
    StringBuilder sb = new StringBuilder();
    Random random = new Random();

    for (int i = 0; i < CODE_LENGTH; i++) {
      int index = random.nextInt(CHAR_POOL.length());
      sb.append(CHAR_POOL.charAt(index));
    }

    return sb.toString();
  }
}
