package com.eunsun.travel_mate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

public class SessionTest {
  @Test
  @DisplayName("세션 작동 테스트")
  public void testSessionAttribute() {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    HttpSession mockSession = new MockHttpSession();
    when(mockRequest.getSession()).thenReturn(mockSession);

    // 세션에 값 설정
    mockSession.setAttribute("userId", 123);

    // 세션 값 읽기
    Object userIdObj = mockSession.getAttribute("userId");
    int userId = userIdObj != null ? (int) userIdObj : 0;
    assertEquals(123, userId);
  }
}