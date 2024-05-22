package com.user.connect.mail;

public interface EmailService {

    void sendEmailVerification(String to, String subject, String text);
}
