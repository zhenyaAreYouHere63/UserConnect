package org.task.authenticify.exception.external;

public class EmailNotConfirmedException extends RuntimeException {

    public EmailNotConfirmedException(String message) {
        super(message);
    }
}
