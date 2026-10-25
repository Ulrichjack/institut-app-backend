package cm.beautysempire.institut.infrastructure.persistence.message;

import cm.beautysempire.institut.domain.messages.Message;
import cm.beautysempire.institut.domain.messages.StatutMessage;
import cm.beautysempire.institut.domain.messages.TypeMessage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageJpaRepository extends JpaRepository<MessageJpaEntity, Long> {
    long countByStatut(StatutMessage statut);

    @Query("SELECT m FROM MessageJpaEntity m WHERE " +
            "(:statut IS NULL OR m.statut = :statut) AND (:type IS NULL OR m.type = :type) AND " +
            "(:q = '' OR LOWER(m.nom) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "OR m.telephone LIKE CONCAT('%', :q, '%') " +
            "OR LOWER(m.sujet) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "OR LOWER(m.message) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "OR LOWER(m.formationNom) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<MessageJpaEntity> search(@Param("q") String query, @Param("statut") StatutMessage statut,
                                  @Param("type") TypeMessage type, Pageable pageable);
}
