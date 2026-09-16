package com.example.iiitquestionbank.model;

public class ApiResponse<T> {

    private boolean success;
    private int status;
    private String message;
    private T data;
    public ApiResponse() {
    }

    public boolean isSuccess() {
        return success;
    }
    public int getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
    public T getData() {
        return data;
    }
}
