package com.example.institue1.repository;

import com.example.institue1.model.Formation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long>, JpaSpecificationExecutor<Formation> {

    Optional<Formation> findBySlug(String slug);
    Optional<Formation> findBySlugAndActiveTrue(String slug);
    Page<Formation> findByActiveTrueOrderByDateCreationDesc(Pageable pageable);
    List<Formation> findByActiveTrue();
    Page<Formation> findAllByOrderByDateCreationDesc(Pageable pageable);
    Optional<Formation> findByIdAndActiveTrue(Long id);
    boolean existsByIdAndActiveTrue(Long id);
    List<Formation> findByActiveTrueOrderByNomAsc();

    //   MÉTHODES POUR CHARGER LES IMAGES
    @Query("SELECT f FROM Formation f LEFT JOIN FETCH f.galleryImages WHERE f.id = :id")
    Optional<Formation> findByIdWithGalleryImages(@Param("id") Long id);
    @Query("SELECT f FROM Formation f LEFT JOIN FETCH f.galleryImages WHERE f.slug = :slug AND f.active = true")
    Optional<Formation> findBySlugWithGalleryImages(@Param("slug") String slug);
}