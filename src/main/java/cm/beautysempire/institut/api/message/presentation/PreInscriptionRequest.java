package cm.beautysempire.institut.api.message.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PreInscriptionRequest {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Format de téléphone invalide (ex: +2376XXXXXXXX)")
    private String telephone;

    private String email; // Optionnel
    private String ville;
    private String quartier;

    @NotNull(message = "L'ID de la formation est obligatoire")
    private Long formationId;

    private String disponibilites;
    private String message;
}