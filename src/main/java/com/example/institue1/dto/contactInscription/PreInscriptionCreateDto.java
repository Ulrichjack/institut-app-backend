package com.example.institue1.dto.contactInscription;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreInscriptionCreateDto {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{8,15}$", message = "Format téléphone invalide")
    private String telephone;

    @Size(max = 50)
    private String ville;

    @NotNull(message = "La formation est obligatoire")
    private Long formationId;

    private String formationNom; // Backup si formation supprimée

    @Size(max = 2000, message = "Les disponibilités ne peuvent dépasser 2000 caractères")
    private String disponibilites;

    @Size(max = 2000)
    private String message; // Message optionnel

    // Tracking
    private String sourceVisite;
    private String adresseIP;
    private String userAgent;
}

