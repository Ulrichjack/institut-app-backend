package com.example.institue1.dto.formation;

import com.example.institue1.dto.gallery.GalleryImageDto;
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
public class FormationDetailDto {

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

    // === PLACES (avec social proof) ===
    private Integer nombrePlaces;
    private Integer nombreInscritsAffiche;
    private Integer placesRestantes;
    private Double tauxRemplissage;
    private String messageSocialProof;
    private Boolean formationComplete;

    // === MÉDIAS ===
    private String photoPrincipale;
    private List<String> photosGalerie;
    private List<GalleryImageDto> imagesGalerie;
    // === PROMOTION ===
    private Boolean enPromotion;
    private BigDecimal pourcentageReduction;
    private Boolean promoActive;
    private LocalDateTime dateDebutPromo;
    private LocalDateTime dateFinPromo;

    // === SEO ===
    private String metaTitle;
    private String metaDescription;
    private String slug;

    // === STATISTIQUES PUBLIQUES ===
    private Integer nombreVues;
    private Integer nombreDemandesInfo;
    private Integer nombreInscriptions;

    // === MÉTADONNÉES ===
    private LocalDateTime dateCreation;
    private LocalDateTime dateMiseAJour;
}
