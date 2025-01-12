package com.hnnu.egospace.launcher.utils;

public class Result<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> Result<T> success() {
        return new Result<T>().setSuccess(true);
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>().setSuccess(true).setData(data);
    }

    public static <T> Result<T> error(String message) {
        return new Result<T>().setSuccess(false).setMessage(message);
    }

    public boolean isSuccess() {
        return success;
    }

    public Result<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

}
