package com.xxx.core.exceptions;

public class ResponseEntityException extends Exception {
    private int responseStatus;

    public ResponseEntityException() {
    }

    public ResponseEntityException(int responseStatus, String message) {
        super(message);
        this.responseStatus = responseStatus;
    }

    public ResponseEntityException(String message) {
        super(message);
    }

    public ResponseEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseEntityException(Throwable cause) {
        super(cause);
    }

    public ResponseEntityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }
}
