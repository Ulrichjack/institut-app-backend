package cm.beautysempire.institut.infrastructure.persistence.formation;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FormationJpaRepository extends JpaRepository<FormationJpaEntity, Long> {
    Optional<FormationJpaEntity> findBySlug(String slug);
    List<FormationJpaEntity> findByActiveTrue();
}