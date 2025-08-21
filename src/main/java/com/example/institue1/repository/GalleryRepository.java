package com.example.institue1.repository;

import com.example.institue1.model.GalleryImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryRepository extends JpaRepository<GalleryImage, Long> {
    // Images publiques
    Page<GalleryImage> findByPublic_(Boolean isPublic, Pageable pageable);

    // Par catégorie
    Page<GalleryImage> findByCategorie(String categorie, Pageable pageable);

    // Par catégorie et visibilité
    Page<GalleryImage> findByCategorieAndPublic_(String categorie, Boolean isPublic, Pageable pageable);

    // Images d'une formation
    List<GalleryImage> findByFormationIdAndPublic_(Long formationId, Boolean isPublic);

    // Images populaires pour la page d'accueil
    List<GalleryImage> findTop6ByPublic_OrderByDateCreationDesc(Boolean isPublic);
}