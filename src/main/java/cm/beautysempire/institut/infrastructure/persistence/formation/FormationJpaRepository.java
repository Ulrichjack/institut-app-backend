package cm.beautysempire.institut.infrastructure.persistence.formation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FormationJpaRepository extends JpaRepository<FormationJpaEntity, Long> {
    Optional<FormationJpaEntity> findBySlug(String slug);

    Page<FormationJpaEntity> findByActiveTrue(Pageable pageable);

    boolean existsByNomIgnoreCase(String nom);

    @Query("SELECT f FROM FormationJpaEntity f WHERE f.active = true AND " +
            "(LOWER(f.nom) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
            "LOWER(f.description) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
            "LOWER(f.categorie) LIKE LOWER(CONCAT('%', :motCle, '%')))")
    Page<FormationJpaEntity> searchByMotCle(@Param("motCle") String motCle, Pageable pageable);
}