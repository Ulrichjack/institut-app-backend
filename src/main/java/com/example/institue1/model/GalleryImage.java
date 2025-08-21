package com.example.institue1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "gallery_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String categorie; // FORMATION, EVENEMENT, TRAVAUX_ETUDIANTS, INSTITUT

    @Column(nullable = false)
    private String filename; // Nom fichier système

    @Column(nullable = false)
    private String url; // URL complète de l'image

    @Column(nullable = false)
    private String thumbnailUrl; // URL miniature

    @Column
    private Integer width; // Largeur de l'image

    @Column
    private Integer height; // Hauteur de l'image

    @Column
    private Long fileSize; // Taille en octets

    @Column(nullable = false, name = "is_public")
    private Boolean public_; // Image publique ou privée

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(length = 50)
    private String uploadedBy; // Admin qui a uploadé

    // Relation optionnelle avec Formation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    private Formation formation;
}