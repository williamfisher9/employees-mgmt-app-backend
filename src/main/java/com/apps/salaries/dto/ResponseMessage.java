package com.apps.salaries.dto;

public class ResponseMessage {
    private Object message;
    private int status;

    public ResponseMessage(Object message, int status) {
        this.message = message;
        this.status = status;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
