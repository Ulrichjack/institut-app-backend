package com.example.institue1.mapper;


import com.example.institue1.dto.contactInscription.*;
import com.example.institue1.model.Formation;
import com.example.institue1.model.Message;
import com.example.institue1.utils.MessageUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {FormationSimpleDto.class},
        imports = {com.example.institue1.utils.MessageUtils.class}
)
public interface MessageMapper {

    // Message → MessageListDto
    @Mapping(target = "type", expression = "java( message.getType().getLibelle() )")
    @Mapping(target = "statut", expression = "java( message.getStatut().getLibelle() )")
    @Mapping(target = "urgent", ignore = true)
    @Mapping(target = "ageEnHeures", ignore = true)
    @Mapping(target = "priorite", ignore = true)
    MessageListDto toListDto(Message message);

    //liste de Message en liste de MessageListDto
    List<MessageListDto> toListDto(List<Message> messages);


    // Message → MessageDetailDto
    @Mapping(target = "type", expression = "java( message.getType().getLibelle() )")
    @Mapping(target = "statut", expression = "java( message.getStatut().getLibelle() )")
    @Mapping(target = "formationId", expression = "java( message.getFormationInteresse() != null ? message.getFormationInteresse().getId() : null )")
    @Mapping(target = "formation", source = "formationInteresse")
    @Mapping(target = "navigateur", expression = "java( MessageUtils.extraireInfosNavigateur(message.getUserAgent()) )")
    @Mapping(target = "urgent", ignore = true)
    @Mapping(target = "ageEnHeures", ignore = true)
    @Mapping(target = "priorite", ignore = true)
    @Mapping(target = "scoreSpam", ignore = true)
    MessageDetailDto toDetailDto(Message message);

    // DTO → Message
    Message fromContactCreateDto(ContactCreateDto dto);

    @Mapping(target = "formationInteresse", source = "formation")
    @Mapping(target = "nom", source = "dto.nom")
    @Mapping(target = "email", source = "dto.email")
    @Mapping(target = "telephone", source = "dto.telephone")
    @Mapping(target = "ville", source = "dto.ville")
    @Mapping(target = "disponibilites", source = "dto.disponibilites")
    @Mapping(target = "message", source = "dto.message")
    Message fromPreInscriptionCreateDto(PreInscriptionCreateDto dto, Formation formation);

    // Formation → FormationSimpleDto
    FormationSimpleDto toSimpleDto(Formation formation);
    List<FormationSimpleDto> toSimpleDto(List<Formation> formations);

    // === POST-MAPPING CALCULS ===
    @AfterMapping
    default void enrichListDto(Message message, @MappingTarget MessageListDto dto) {
        dto.setUrgent(MessageUtils.isUrgent(message));
        dto.setAgeEnHeures(MessageUtils.getAgeEnHeures(message));
        dto.setPriorite(MessageUtils.calculerPriorite(message));
    }

    @AfterMapping
    default void enrichDetailDto(Message message, @MappingTarget MessageDetailDto dto) {
        dto.setUrgent(MessageUtils.isUrgent(message));
        dto.setAgeEnHeures(MessageUtils.getAgeEnHeures(message));
        dto.setPriorite(MessageUtils.calculerPriorite(message));
        dto.setScoreSpam(MessageUtils.calculerScoreSpam(message));
    }
}
