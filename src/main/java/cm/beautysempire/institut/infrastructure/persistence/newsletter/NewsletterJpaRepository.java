package cm.beautysempire.institut.infrastructure.persistence.newsletter;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsletterJpaRepository extends JpaRepository<NewsletterJpaEntity, Long> {

    boolean existsByTelephone(String telephone);
}
