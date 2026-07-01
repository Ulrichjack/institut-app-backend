package cm.beautysempire.institut.infrastructure.persistence.newsletter;

import cm.beautysempire.institut.domain.newsletter.NewsletterRepositoryPort;
import cm.beautysempire.institut.domain.newsletter.NewsletterSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NewsletterRepositoryAdapter implements NewsletterRepositoryPort {

    private final NewsletterJpaRepository jpaRepository;
    private final NewsletterPersistenceMapper mapper;

    @Override
    public NewsletterSubscription save(NewsletterSubscription newsletterSubscription){
        NewsletterJpaEntity entity = mapper.toEntity(newsletterSubscription);
        NewsletterJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Page<NewsletterSubscription> findAll(Pageable pageable){
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }
    @Override
    public Optional<NewsletterSubscription> findById(Long id){
        return  jpaRepository.findById(id).map(mapper::toDomain);
     }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public boolean existsByTelephone(String telephone){
        return jpaRepository.existsByTelephone(telephone);
    }
}
