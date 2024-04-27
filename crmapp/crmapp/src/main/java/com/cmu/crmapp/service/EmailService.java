package com.cmu.crmapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  @Autowired private JavaMailSender javaMailSender;

  public void sendEmail(String email, String message) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(email);
    mailMessage.setSubject("Activate your book store account");
    mailMessage.setText(
        "Dear "
            + message
            + ",\n"
            + "Welcome to the Book store created by rsequeir. \nExceptionally this time we won’t ask you to click a link to activate your account.");
    javaMailSender.send(mailMessage);
  }
}
