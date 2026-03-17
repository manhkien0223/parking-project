package com.kien.parking_system.payloads.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"status", "code", "message", "data"})
public class ResponseUtils<T> {
    private int status;
    private String code;
    private String message;
    private T data;

    // 1. Khuôn Full-topping (Dùng khi thành công, có trả về data)
    public ResponseUtils(int status, String code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 2. Khuôn Basic (Dùng khi báo lỗi, không có data trả về)
    public ResponseUtils(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
