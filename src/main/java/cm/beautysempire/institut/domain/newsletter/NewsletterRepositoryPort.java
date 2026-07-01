package cm.beautysempire.institut.domain.newsletter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NewsletterRepositoryPort {
    NewsletterSubscription save(NewsletterSubscription subscription);
    Page<NewsletterSubscription> findAll(Pageable pageable);
    Optional<NewsletterSubscription> findById(Long id);
    boolean existsByTelephone(String telephone);
    long count();
}