package com.codeit.weatherfit.domain.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmtpPasswordResetMailSender implements PasswordResetMailSender {

    private final JavaMailSender javaMailSender;

    @Value("${weatherfit.mail.from}")
    private String from;

    @Override
    public void send(String email, String temporaryPassword) {
        System.out.println("[SMTP 메일 발송기 호출] to=" + email);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            messageHelper.setFrom(from);
            messageHelper.setTo(email);
            messageHelper.setSubject("[WeatherFit] 임시 비밀번호 안내");
            messageHelper.setText(createMessage(temporaryPassword), false);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException | MailAuthenticationException | MailSendException exception) {
            throw new IllegalStateException("임시 비밀번호 메일 발송에 실패했습니다.", exception);
        }
    }

    private String createMessage(String temporaryPassword) {
        return """
                안녕하세요, WeatherFit 입니다.

                요청하신 임시 비밀번호를 안내드립니다.

                임시 비밀번호: %s

                로그인 후 반드시 비밀번호를 변경해주세요.
                감사합니다.
                """.formatted(temporaryPassword);
    }
}