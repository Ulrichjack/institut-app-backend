package com.example.institue1.dto.formation;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormationCreateDto {

    // === INFORMATIONS DE BASE ===
    @NotBlank(message = "Le nom de la formation est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String nom;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, max = 2000, message = "La description doit contenir entre 50 et 2000 caractères")
    private String description;

    @NotBlank(message = "La durée est obligatoire")
    private String duree;

    @NotNull(message = "Les frais Inscriptions sont obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Les frais Inscriptions doivent être supérieur à 0")
    private BigDecimal fraisInscription;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private BigDecimal prix;

    @NotBlank(message = "La catégorie est obligatoire")
    private String categorie;

    // === CERTIFICAT ===
    private Boolean certificatDelivre = true;
    private String nomCertificat;

    // === CONTENU PÉDAGOGIQUE ===
    private String programme;
    private String objectifs;
    private String materielFourni;

    // === PLANNING ===
    private String horaires;
    private String frequence;

    // === PLACES ET SOCIAL PROOF ===
    @Min(value = 1, message = "Le nombre de places doit être au moins 1")
    private Integer nombrePlaces = 15;

    @Min(value = 0, message = "Le nombre d'inscrits affiché ne peut être négatif")
    private Integer nombreInscritsAffiche = 0;

    private Boolean socialProofActif = false;

    // === MÉDIAS ===
    private String photoPrincipale;
    private List<String> photosGalerie = new ArrayList<>();
    private String videoPresentation;

    // === PROMOTION ===
    private Boolean enPromotion = false;

    @DecimalMin(value = "0.0", message = "Le pourcentage de réduction doit être positif")
    @DecimalMax(value = "100.0", message = "Le pourcentage de réduction ne peut dépasser 100%")
    private BigDecimal pourcentageReduction = BigDecimal.ZERO;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateDebutPromo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateFinPromo;

    // === SEO ===
    private String metaTitle;
    private String metaDescription;
    private String slug; // Optionnel, sera généré automatiquement si absent

    // === STATUT ===
    private Boolean active = true;
}
