package cm.beautysempire.institut.infrastructure.persistence.newsletter;

import cm.beautysempire.institut.domain.newsletter.NewsletterSubscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NewsletterPersistenceMapper {

    NewsletterSubscription toDomain(NewsletterJpaEntity entity);


    NewsletterJpaEntity toEntity(NewsletterSubscription domain);
}
