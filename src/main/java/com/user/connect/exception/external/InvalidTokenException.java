package com.user.connect.exception.external;

import org.springframework.security.core.AuthenticationException;

public class InvalidTokenException extends AuthenticationException {


    public InvalidTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InvalidTokenException(String msg) {
        super(msg);
    }

    public static String errorCode() {
        return "invalid.code";
    }
}
