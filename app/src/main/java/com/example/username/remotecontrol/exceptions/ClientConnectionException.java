package com.example.username.remotecontrol.exceptions;

public class ClientConnectionException extends RuntimeException {
    private String message;

    public ClientConnectionException(Throwable ex){
        message = ex.getMessage();
        initCause(ex);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
