package com.example.institue1.exception;

import com.example.institue1.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //recupere les erreur de valiation @valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        log.warn("Erreurs de validation : {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Erreurs de validation")
                .data(errors)
                .error("VALIDATION_ERROR")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex) {

        log.warn("Violation de contrainte : {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(
                "Données invalides : " + ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(FormationNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleFormationNotFoundException(
            FormationNotFoundException ex) {

        log.warn("Formation non trouvée : {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.notFound(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("Erreur inattendue : ", ex);

        ApiResponse<Object> response = ApiResponse.error(
                "Une erreur inattendue s'est produite",
                "INTERNAL_SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        log.warn("Argument illégal : {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.badRequest(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex) {

        log.warn("Accès refusé : {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(
                "Accès refusé : " + ex.getMessage(),
                HttpStatus.FORBIDDEN
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

}
