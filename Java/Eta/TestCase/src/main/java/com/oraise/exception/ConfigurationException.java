package com.oraise.exception;

public class ConfigurationException extends Exception {

    private static final long serialVersionUID = -4762579248298354449L;

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(Exception rootCause) {
        super(rootCause.getMessage(), rootCause);
    }

}
