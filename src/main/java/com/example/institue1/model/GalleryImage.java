package com.example.institue1.model;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String url; // URL complète de l'image

    @Column(nullable = false)
    private String categorie; // FORMATION, EVENEMENT, INSTITUT

    @Column(nullable = false)
    private Boolean isPublic = true;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();

    // Optionnel: associer à une formation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    private Formation formation;
}