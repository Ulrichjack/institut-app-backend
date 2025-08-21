package com.example.institue1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String error;
    private int statusCode;
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data, String message){
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    //reponse succes simple
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Opération réussie");
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String error, HttpStatus httpStatus) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .statusCode(httpStatus.value())
                .status(httpStatus.name())
                .timestamp(LocalDateTime.now())
                .build();
    }


    public static <T> ApiResponse<T> error(String error, String message, HttpStatus httpStatus) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(error)
                .statusCode(httpStatus.value())
                .status(httpStatus.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }

    public static <T> ApiResponse<T> badRequest(String error) {
        return error(error, HttpStatus.BAD_REQUEST);
    }
}
