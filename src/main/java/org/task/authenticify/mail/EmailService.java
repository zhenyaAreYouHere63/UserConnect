package org.task.authenticify.mail;

public interface EmailService {

    void sendEmailVerification(String to, String subject, String text);
}
