package cm.beautysempire.institut.infrastructure.persistence.message;

import cm.beautysempire.institut.domain.messages.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessagePersistenceMapper {

    // JPA -> Domain
    Message toDomain(MessageJpaEntity entity);

    // Domain -> JPA
    MessageJpaEntity toEntity(Message domain);
}