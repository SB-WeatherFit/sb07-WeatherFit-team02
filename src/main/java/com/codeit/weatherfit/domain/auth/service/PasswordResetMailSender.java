package com.codeit.weatherfit.domain.auth.service;

public interface PasswordResetMailSender {

    void send(String email, String temporaryPassword);
}