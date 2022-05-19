package com.oraise.exception;

public class SnapshotException extends Exception {

    private static final long serialVersionUID = 6980110964541073614L;

    public SnapshotException(String message) {
        super(message);
    }

    public SnapshotException(Exception rootCause) {
        super(rootCause.getMessage(), rootCause);
    }
}
