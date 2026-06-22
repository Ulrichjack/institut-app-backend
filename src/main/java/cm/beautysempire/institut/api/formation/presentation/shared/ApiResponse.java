// Fichier : src/main/java/cm/beautysempire/institut/api/formation/presentation/shared/ApiResponse.java

package cm.beautysempire.institut.api.formation.presentation.shared;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Masque les champs nuls (ex: pas d'error en cas de succès)
public class ApiResponse<T> {

    private final String status;      // "SUCCESS" ou "ERROR"
    private final int statusCode;     // Code HTTP (ex: 200, 201, 400)
    private final String message;     // Message explicatif

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    private T data;                   // Les données utiles (Payload)
    private Object error;             // Détails de l'erreur (si status == "ERROR")

    // --- FACTORY METHODS ---

    // Pour un succès classique (200 OK)
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status("SUCCESS")
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    // Pour un succès sans data (200 OK)
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .status("SUCCESS")
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    // Pour une création réussie (201 Created)
    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .status("SUCCESS")
                .statusCode(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
                .build();
    }

    // Pour les erreurs (400, 404, 500...)
    public static <T> ApiResponse<T> error(String message, HttpStatus status, Object errorDetails) {
        return ApiResponse.<T>builder()
                .status("ERROR")
                .statusCode(status.value())
                .message(message)
                .error(errorDetails)
                .build();
    }
}