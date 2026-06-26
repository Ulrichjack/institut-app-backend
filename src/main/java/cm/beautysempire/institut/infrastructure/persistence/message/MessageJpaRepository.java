package cm.beautysempire.institut.infrastructure.persistence.message;

import cm.beautysempire.institut.domain.messages.Message;
import cm.beautysempire.institut.domain.messages.StatutMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageJpaRepository extends JpaRepository<MessageJpaEntity, Long> {
    long countByStatut(StatutMessage statut);
}