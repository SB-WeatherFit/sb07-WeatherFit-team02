package com.codeit.weatherfit.domain.auth.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@ConditionalOnMissingBean(SmtpPasswordResetMailSender.class)
public class ConsolePasswordResetMailSender implements PasswordResetMailSender {

    @Override
    public void send(String email, String temporaryPassword) {
        System.out.println("[임시 비밀번호 발송]");
        System.out.println("to=" + email);
        System.out.println("temporaryPassword=" + temporaryPassword);
    }
}