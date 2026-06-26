package cm.beautysempire.institut.api.newsletter;

import cm.beautysempire.institut.domain.newsletter.NewsletterSubscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NewsletterApiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateInscription", ignore = true)
    @Mapping(target = "contacte", ignore = true)
    NewsletterSubscription toDomain(NewsletterSubscribeRequest request);

    NewsletterResponse toResponse(NewsletterSubscription domain);
}