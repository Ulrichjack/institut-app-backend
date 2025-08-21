package com.example.institue1.dto.formation;

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
public class FormationAdminDto {

    private Long id;
    private String nom;
    private String description;
    private String duree;
    private BigDecimal fraisInscription;
    private BigDecimal prix;
    private BigDecimal prixAvecReduction;
    private String categorie;

    // === CERTIFICAT ===
    private Boolean certificatDelivre;
    private String nomCertificat;

    // === CONTENU PÉDAGOGIQUE ===
    private String programme;
    private String objectifs;
    private String materielFourni;

    // === PLANNING ===
    private String horaires;
    private String frequence;

    // === PLACES ET SOCIAL PROOF (toutes les données) ===
    private Integer nombrePlaces;
    private Integer nombreInscritsReel; // Vrais inscrits (visible admin seulement)
    private Integer nombreInscritsAffiche; // Nombre affiché
    private Boolean socialProofActif;
    private Integer placesRestantesReelles;
    private Integer placesRestantesAffichees;
    private Double tauxRemplissageReel;
    private Double tauxRemplissageAffiche;
    private String messageSocialProof;
    private Boolean formationComplete;

    // === MÉDIAS ===
    private String photoPrincipale;
    private List<String> photosGalerie;
    private String videoPresentation;

    // === PROMOTION ===
    private Boolean enPromotion;
    private BigDecimal pourcentageReduction;
    private Boolean promoActive;
    private LocalDateTime dateDebutPromo;
    private LocalDateTime dateFinPromo;

    // === STATUT ===
    private Boolean active;

    // === SEO ===
    private String metaTitle;
    private String metaDescription;
    private String slug;

    // === STATISTIQUES COMPLÈTES ===
    private Integer nombreVues;
    private Integer nombreDemandesInfo;
    private Integer nombreInscriptions;

    // === MÉTADONNÉES ADMIN ===
    private LocalDateTime dateCreation;
    private LocalDateTime dateMiseAJour;
    private String creeParAdmin;
    private String modifiePar;
}

