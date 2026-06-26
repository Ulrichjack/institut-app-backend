package cm.beautysempire.institut.api.message.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ContactCreateRequest {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    // 🔥 Règle V4 : Téléphone obligatoire, format Camerounais
    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Format de téléphone invalide (ex: +2376XXXXXXXX)")
    private String telephone;

    private String email; // Optionnel (WhatsApp-First)
    private String ville;
    private String quartier;
    private String sujet;

    @NotBlank(message = "Le message est obligatoire")
    private String message;

    private Long formationId; // Optionnel
}