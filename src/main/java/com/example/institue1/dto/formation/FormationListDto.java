package com.example.institue1.dto.formation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormationListDto {

    private Long id;
    private String nom;
    private String description; // Version courte pour la liste
    private String duree;
    private BigDecimal fraisInscription;
    private BigDecimal prix;
    private BigDecimal prixAvecReduction; // Prix final après réduction
    private String categorie;

    // === MÉDIAS ===
    private String photoPrincipale;
    private Integer nombreImages;

    // === PLACES (avec social proof) ===
    private Integer nombrePlaces;
    private Integer nombreInscritsAffiche; // Nombre affiché (social proof)
    private Integer placesRestantes;
    private Double tauxRemplissage;
    private String messageSocialProof; // Message dynamique

    // === PROMOTION ===
    private Boolean enPromotion;
    private BigDecimal pourcentageReduction;
    private Boolean promoActive;

    // === CERTIFICAT ===
    private Boolean certificatDelivre;

    // === SEO ===
    private String slug;

    // === STATUT ===
    private Boolean active;

    // === STATISTIQUES PUBLIQUES ===
    private Integer nombreVues;

    // === MÉTHODES UTILITAIRES ===
    public String getDescriptionCourte() {
        if (description == null) return "";
        return description.length() > 150 ?
                description.substring(0, 147) + "..." : description;
    }
}