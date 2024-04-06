package org.task.webapplication.mail;

public interface EmailService {

    void sendEmailVerification(String to, String subject, String text);
}
