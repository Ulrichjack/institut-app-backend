package com.example.institue1.repository;

import com.example.institue1.model.GalleryImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long> {

    @Query("SELECT g FROM GalleryImage g WHERE LOWER(g.formation.nom) LIKE LOWER(CONCAT('%', :nomFormation, '%'))")
    List<GalleryImage> findByFormationNomContaining(@Param("nomFormation") String nomFormation);

    // Variante paginée
    @Query("SELECT g FROM GalleryImage g WHERE LOWER(g.formation.nom) LIKE LOWER(CONCAT('%', :nomFormation, '%'))")
    Page<GalleryImage> findByFormationNomContainingPaged(@Param("nomFormation") String nomFormation, PageRequest pageable);

    @Query("SELECT g FROM GalleryImage g WHERE g.formation.id = :formationId ORDER BY g.dateCreation DESC")
    List<GalleryImage> findByFormationIdOrderByDateCreationDesc(@Param("formationId") Long formationId);
}