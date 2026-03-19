package com.codeit.weatherfit.domain.auth.service;

import org.springframework.stereotype.Component;

@Component
public class ConsolePasswordResetMailSender implements PasswordResetMailSender {

    @Override
    public void send(String email, String temporaryPassword) {
        // 임시 이메일 전송
        System.out.println("[임시 비밀번호 발송]");
        System.out.println("to=" + email);
        System.out.println("temporaryPassword=" + temporaryPassword);
    }
}