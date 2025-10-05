package org.example.teampack.service;

public interface UserEmailService {
    void sendVerificationCode(String email);
    boolean verifyCode(String email, String code);
}
