package edu.java.distributedfileprocessing.exception;

public class NotSupportedAuthenticationException extends RuntimeException {

    public NotSupportedAuthenticationException(String message) {
        super(message);
    }

    public NotSupportedAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}
