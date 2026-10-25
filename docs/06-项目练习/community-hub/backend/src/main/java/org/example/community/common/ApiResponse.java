package org.example.community.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.community.enums.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return of(Status.SUCCESS, data);
    }

    public static <T> ApiResponse<T> error(T data) {
        return of(Status.ERROR, data);
    }

    private static <T> ApiResponse<T> of(Status status, T data) {
        return new ApiResponse<>(status.getCode(), status.getMessage(), data);
    }
}
