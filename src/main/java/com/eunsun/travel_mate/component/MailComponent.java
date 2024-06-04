package com.eunsun.travel_mate.component;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailComponent {

  private final JavaMailSender mailSender;

  public void sendEmail(String to, String subject, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    mailSender.send(message);
  }

  public void sendVerificationEmail(String to, String verificationCode) {
    String subject = "Travel Mate 회원가입 인증 코드";
    String text = "인증 코드: " + verificationCode;
    sendEmail(to, subject, text);
  }

  public void sendTemporaryPasswordEmail(String to, String temporaryPassword) {
    String subject = "Travel Mate 임시 비밀번호";
    String text = "임시 비밀번호: " + temporaryPassword;
    sendEmail(to, subject, text);
  }
}