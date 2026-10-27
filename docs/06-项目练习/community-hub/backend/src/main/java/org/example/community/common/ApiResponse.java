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
    return of(Status.SUCCESS, data, "成功");
  }

  public static <T> ApiResponse<T> ok(T data, String message) {
    return of(Status.SUCCESS, data, message);
  }

  public static <T> ApiResponse<T> error(T data) {
    return of(Status.ERROR, data, "失败");
  }

  public static <T> ApiResponse<T> error(T data, String message) {
    return of(Status.ERROR, data, message);
  }

  private static <T> ApiResponse<T> of(Status status, T data, String message) {
    return new ApiResponse<>(status.getCode(), message, data);
  }
}
