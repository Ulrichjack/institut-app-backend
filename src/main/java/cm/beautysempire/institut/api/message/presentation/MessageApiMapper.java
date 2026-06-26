package cm.beautysempire.institut.api.message.presentation;

import cm.beautysempire.institut.domain.messages.Message;
import cm.beautysempire.institut.domain.messages.TypeMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = {TypeMessage.class})
public interface MessageApiMapper {

    @Mapping(target = "type", expression = "java(TypeMessage.CONTACT_GENERAL)")
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "formationId", ignore = true)
    @Mapping(target = "formationNom", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateLecture", ignore = true)
    @Mapping(target = "dateTraitement", ignore = true)
    @Mapping(target = "traiteParAdmin", ignore = true)
    @Mapping(target = "sourceVisite", ignore = true)
    @Mapping(target = "adresseIp", ignore = true)
    @Mapping(target = "userAgent", ignore = true)
    @Mapping(target = "emailConfirmationEnvoye", ignore = true)
    @Mapping(target = "whatsappNotificationEnvoye", ignore = true)
    @Mapping(target = "dateEmailConfirmation", ignore = true)
    @Mapping(target = "dateWhatsappNotification", ignore = true)
    @Mapping(target = "disponibilites", ignore = true)
    Message toDomain(ContactCreateRequest request);

    @Mapping(target = "type", expression = "java(TypeMessage.PRE_INSCRIPTION)")
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "formationId", ignore = true)
    @Mapping(target = "formationNom", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateLecture", ignore = true)
    @Mapping(target = "dateTraitement", ignore = true)
    @Mapping(target = "traiteParAdmin", ignore = true)
    @Mapping(target = "sourceVisite", ignore = true)
    @Mapping(target = "adresseIp", ignore = true)
    @Mapping(target = "userAgent", ignore = true)
    @Mapping(target = "emailConfirmationEnvoye", ignore = true)
    @Mapping(target = "whatsappNotificationEnvoye", ignore = true)
    @Mapping(target = "dateEmailConfirmation", ignore = true)
    @Mapping(target = "dateWhatsappNotification", ignore = true)
    Message toDomain(PreInscriptionRequest request);

    @Mapping(target = "whatsappConfirmationLink", source = "whatsappLink")
    MessageResponse toResponse(Message message, String whatsappLink);

    MessageListResponse toListResponse(Message message);
    List<MessageListResponse> toListResponse(List<Message> messages);
}