package org.task.webapplication.auth;

public record AuthenticationRequest(
        String email,
        String password
) {

}
