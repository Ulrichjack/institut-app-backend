package com.example.institue1.repository;


import com.example.institue1.model.Formation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long>, JpaSpecificationExecutor<Formation> {

    Optional<Formation> findBySlug(String slug);

    //Trouve par slug ET active (sécurité publique)
    Optional<Formation> findBySlugAndActiveTrue(String slug);

    //Toute formation actives
    Page<Formation> findByActiveTrueOrderByDateCreationDesc(Pageable pageable);

    //Par Catégorie (actives uniquement)
    Page<Formation> findByActiveTrueAndCategorieOrderByDateCreationDesc(String categorie, Pageable pageable);



    Page<Formation> findAllByOrderByDateCreationDesc(Pageable pageable);


    Optional<Formation> findByIdAndActiveTrue(Long id);

    boolean existsByIdAndActiveTrue(Long id);


    @Query("""
    SELECT f.id, f.nom, COUNT(f.id) as demandes
    FROM Formation f 
    WHERE f.active = true 
    GROUP BY f.id, f.nom 
    ORDER BY demandes DESC
""")
    List<Object[]> findFormationsPopulairesAvecDemandes();



    List<Formation> findByActiveTrueOrderByNomAsc();



}
