package cm.beautysempire.institut.infrastructure.persistence.formation;

import cm.beautysempire.institut.domain.formation.Formation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FormationPersistenceMapper {

    // JPA -> Domain
    Formation toDomain(FormationJpaEntity entity);

    // Domain -> JPA
    FormationJpaEntity toEntity(Formation domain);
}