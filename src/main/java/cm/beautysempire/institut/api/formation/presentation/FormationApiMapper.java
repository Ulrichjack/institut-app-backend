package cm.beautysempire.institut.api.formation.presentation;

import cm.beautysempire.institut.domain.formation.Formation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FormationApiMapper {

    Formation toDomain(FormationCreateRequest request);

    // MapStruct va automatiquement appeler les méthodes getPrixAvecReduction(), etc. de ton Domaine !
    @Mapping(target = "prixAvecReduction", expression = "java(formation.getPrixAvecReduction())")
    @Mapping(target = "placesRestantesAffichees", expression = "java(formation.getPlacesRestantesAffichees())")
    @Mapping(target = "isPromoActive", expression = "java(formation.isPromoActive())")
    FormationResponse toResponse(Formation formation);
}