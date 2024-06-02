package com.eunsun.travel_mate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

  @Mock
  private JavaMailSender mailSender;

  @InjectMocks
  private MailService mailService;

  @Test
  @DisplayName("인증 메일로 전송 테스트")
  public void testSendEmail_success() {
    // given
    String to = "test@example.com";
    String subject = "Test Subject";
    String text = "Test Text";

    // when
    mailService.sendEmail(to, subject, text);

    // then
    verify(mailSender).send(createExpectedMessage(to, subject, text));
  }

  @Test
  @DisplayName("메일 전송 실패 테스트")
  public void testSendEmail_fail() {
    // given
    String to = "test@example.com";
    String subject = "Test Subject";
    String text = "Test Text";
    RuntimeException exception = new RuntimeException("Sending email failed");
    doThrow(exception).when(mailSender).send(any(SimpleMailMessage.class));

    // when
    RuntimeException thrownException = null;
    try {
      mailService.sendEmail(to, subject, text);
    } catch (RuntimeException e) {
      thrownException = e;
    }

    // then
    verify(mailSender).send(any(SimpleMailMessage.class));
    assertThat(thrownException).isNotNull();
    assertThat(thrownException.getMessage()).isEqualTo("Sending email failed");
  }

  @Test
  @DisplayName("제대로 된 정보를 보내는지 테스트")
  public void testSendVerificationEmail() {
    // given
    String to = "test@example.com";
    String verificationCode = "123456";
    String expectedSubject = "Travel Mate 회원가입 인증 코드";
    String expectedText = "인증 코드: " + verificationCode;

    // when
    mailService.sendVerificationEmail(to, verificationCode);

    // then
    verify(mailSender).send(createExpectedMessage(to, expectedSubject, expectedText));
  }

  private SimpleMailMessage createExpectedMessage(String to, String subject, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    return message;
  }
}
