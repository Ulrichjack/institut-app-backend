package com.example.institue1.dto.contactInscription;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactCreateDto {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{8,15}$", message = "Format téléphone invalide")
    private String telephone;

    @Size(max = 50, message = "Le nom de la ville ne peut dépasser 50 caractères")
    private String ville;

    @Size(max = 100, message = "Le sujet ne peut dépasser 100 caractères")
    private String sujet;

    @NotBlank(message = "Le message est obligatoire")
    @Size(min = 10, max = 2000, message = "Le message doit contenir entre 10 et 2000 caractères")
    private String message;

    // Optionnel - si question sur formation spécifique
    private String formationNom;
    private Long formationId;

    // Tracking
    private String sourceVisite;
    private String adresseIP;
    private String userAgent;
}
