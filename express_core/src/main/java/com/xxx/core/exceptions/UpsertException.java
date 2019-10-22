package com.xxx.core.exceptions;

public class UpsertException extends Exception {
    public UpsertException() {
    }

    public UpsertException(String message) {
        super(message);
    }

    public UpsertException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpsertException(Throwable cause) {
        super(cause);
    }

    public UpsertException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
