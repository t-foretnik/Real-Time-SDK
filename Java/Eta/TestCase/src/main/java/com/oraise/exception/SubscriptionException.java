package com.oraise.exception;

public class SubscriptionException extends Exception {

    private static final long serialVersionUID = 4754887884047233760L;

    public SubscriptionException(String message) {
        super(message);
    }

    public SubscriptionException(Exception rootCause) {
        super(rootCause.getMessage(), rootCause);
    }
}
