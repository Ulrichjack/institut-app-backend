package com.example.institue1.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "formations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === RELATION AVEC MESSAGES ===
    @OneToMany(mappedBy = "formationInteresse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Message> messages = new ArrayList<>();

    // === RELATION AVEC GALLERIE D'IMAGES ===
    @OneToMany(mappedBy = "formation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<GalleryImage> galleryImages = new ArrayList<>();

    // === INFORMATIONS DE BASE ===
    @NotBlank(message = "Le nom de la formation est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    @Column(nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, max = 2000, message = "La description doit contenir entre 50 et 2000 caractères")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "La durée est obligatoire")
    @Column(nullable = false, length = 50)
    private String duree; // Ex: "3 mois", "6 semaines"

    @NotNull(message = "Les frais Inscriptions sont obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Les frais Inscriptions doivent être supérieur à 0")
    @Column(nullable = false, precision = 10, scale = 2, name = "frais_inscription")
    private BigDecimal fraisInscription; // En FCFA

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @Column(nullable = false, precision = 10, scale = 2 )
    private BigDecimal prix; // En FCFA

    // === CATÉGORIE ET CLASSIFICATION ===
    @NotBlank(message = "La catégorie est obligatoire")
    @Column(nullable = false, length = 50)
    private String categorie;

    // === CERTIFICAT ET PRÉREQUIS ===
    @Column(nullable = false)
    private Boolean certificatDelivre = true;

    @Column(length = 100)
    private String nomCertificat; // "Certificat en Maquillage Professionnel IBE"

    // === CONTENU PÉDAGOGIQUE ===
    @Column(columnDefinition = "TEXT")
    private String programme; // Programme détaillé cours par cours

    @Column(columnDefinition = "TEXT")
    private String objectifs; // Ce que l'étudiant va apprendre

    @Column(columnDefinition = "TEXT")
    private String materielFourni; // "Kit complet maquillage, produits professionnels"

    // === PLANNING ET DISPONIBILITÉ ===
    @Column(length = 100)
    private String horaires; // "Lun-Ven 9h-12h" ou "Weekend 8h-17h"

    @Column(length = 50)
    private String frequence; // "3 fois par semaine", "Intensif weekend"

    // === GESTION DES PLACES ET SOCIAL PROOF ===
    @Min(value = 1, message = "Le nombre de places doit être au moins 1")
    @Column(nullable = false)
    private Integer nombrePlaces = 15; // Places réelles disponibles par session

    @Min(value = 0, message = "Le nombre d'inscrits réels ne peut être négatif")
    @Column(nullable = false)
    private Integer nombreInscritsReel = 0; // Vrais inscrits (pour stats internes)

    @Min(value = 0, message = "Le nombre d'inscrits affiché ne peut être négatif")
    @Column(nullable = false)
    private Integer nombreInscritsAffiche = 0; // Nombre affiché pour social proof (peut être fictif)

    @Column(nullable = false)
    private Boolean socialProofActif = false; // Active/désactive le social proof

    // === MÉDIAS ET PHOTOS ===
    @Column(length = 255)
    private String photoPrincipale; // URL photo principale

    @ElementCollection
    @CollectionTable(name = "formation_photos", joinColumns = @JoinColumn(name = "formation_id"))
    @Column(name = "photo_url")
    private List<String> photosGalerie = new ArrayList<>(); // Galerie photos


    // === STATUT ET GESTION ===
    @Column(nullable = false)
    private Boolean active = true; // Visible sur le site

    @Column(nullable = false)
    private Boolean enPromotion = false;

    @DecimalMin(value = "0.0", message = "Le pourcentage de réduction doit être positif")
    @DecimalMax(value = "100.0", message = "Le pourcentage de réduction ne peut dépasser 100%")
    @Column(precision = 5, scale = 2)
    private BigDecimal pourcentageReduction = BigDecimal.ZERO; // % de réduction

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateDebutPromo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateFinPromo;

    // === MÉTADONNÉES ===
    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateMiseAJour;

    @Column(length = 50)
    private String creeParAdmin; // Nom de l'admin qui a créé

    @Column(length = 50)
    private String modifiePar; // Dernier admin qui a modifié

    // === STATISTIQUES ===
    @Column(nullable = false)
    private Integer nombreVues = 0; // Nombre de fois consultée

    @Column(nullable = false)
    private Integer nombreDemandesInfo = 0; // Demandes d'informations reçues

    @Column(nullable = false)
    private Integer nombreInscriptions = 0; // Total inscriptions depuis création

    // === SEO ET RÉFÉRENCEMENT ===
    @Column(length = 200)
    private String metaTitle; // Titre SEO

    @Column(length = 300)
    private String metaDescription; // Description SEO

    @Column(length = 100, unique = true)
    private String slug; // URL friendly: "maquillage-professionnel-ibe"

    // === MÉTHODES UTILITAIRES ===

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateMiseAJour = LocalDateTime.now();
        if (slug == null || slug.isEmpty()) {
            slug = generateSlug();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dateMiseAJour = LocalDateTime.now();
    }

    // Génère un slug unique à partir du nom
    private String generateSlug() {
        String baseSlug = nom.toLowerCase()
                .replaceAll("[àáâãäå]", "a")
                .replaceAll("[èéêë]", "e")
                .replaceAll("[ìíîï]", "i")
                .replaceAll("[òóôõö]", "o")
                .replaceAll("[ùúûü]", "u")
                .replaceAll("[ç]", "c")
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        // Ajouter timestamp si besoin d'unicité
        return baseSlug + "-" + System.currentTimeMillis() % 10000;
    }



}