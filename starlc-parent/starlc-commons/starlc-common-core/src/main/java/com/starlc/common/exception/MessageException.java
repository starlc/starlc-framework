package com.starlc.common.exception;

public class MessageException extends RuntimeException {

    public MessageException() {
        super();
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
