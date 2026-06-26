package cm.beautysempire.institut.api.message.presentation;

import cm.beautysempire.institut.domain.messages.StatutMessage;
import cm.beautysempire.institut.domain.messages.TypeMessage;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageListResponse {
    private Long id;
    private TypeMessage type;
    private StatutMessage statut;
    private String nom;
    private String telephone;
    private String sujet;
    private String formationNom;
    private LocalDateTime dateCreation;

    // On ne renvoie pas l'adresse IP, le UserAgent, etc. pour alléger la réponse !
}