package org.example.community.enums;

public enum Status {
    SUCCESS(200, "成功"),
    ERROR(500, "失败");

    private final int code;
    private final String message;

    Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
