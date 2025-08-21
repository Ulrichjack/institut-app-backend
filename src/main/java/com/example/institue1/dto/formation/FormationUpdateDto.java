package com.example.institue1.dto.formation;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormationUpdateDto {

    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String nom;

    @Size(min = 10, max = 2000, message = "La description doit contenir entre 10 et 2000 caractères")
    private String description;

    private String duree;


    @DecimalMin(value = "0.0", inclusive = false, message = "Les frais Inscriptions doivent être supérieur à 0")
    private BigDecimal fraisInscription;

    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private BigDecimal prix;

    private String categorie;
    private Boolean certificatDelivre;
    private String nomCertificat;
    private String programme;
    private String objectifs;
    private String materielFourni;
    private String horaires;
    private String frequence;

    @Min(value = 1, message = "Le nombre de places doit être au moins 1")
    private Integer nombrePlaces;

    @Min(value = 0, message = "Le nombre d'inscrits affiché ne peut être négatif")
    private Integer nombreInscritsAffiche;

    private Boolean socialProofActif;
    private String photoPrincipale;
    private List<String> photosGalerie;
    private String videoPresentation;
    private Boolean enPromotion;

    @DecimalMin(value = "0.0", message = "Le pourcentage de réduction doit être positif")
    @DecimalMax(value = "100.0", message = "Le pourcentage de réduction ne peut dépasser 100%")
    private BigDecimal pourcentageReduction;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateDebutPromo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateFinPromo;

    private String metaTitle;
    private String metaDescription;
    private String slug;
    private Boolean active;
}
