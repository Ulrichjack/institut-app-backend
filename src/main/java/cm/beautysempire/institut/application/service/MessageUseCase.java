package cm.beautysempire.institut.application.service;

import cm.beautysempire.institut.application.exception.FormationCompleteException;
import cm.beautysempire.institut.application.exception.FormationNotFoundException;
import cm.beautysempire.institut.application.port.WhatsAppNotifierPort;
import cm.beautysempire.institut.domain.formation.Formation;
import cm.beautysempire.institut.domain.formation.FormationRepositoryPort;
import cm.beautysempire.institut.domain.messages.Message;
import cm.beautysempire.institut.domain.messages.MessageRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class MessageUseCase {

    private final MessageRepositoryPort messageRepositoryPort;
    private final FormationRepositoryPort formationRepositoryPort;
    private final WhatsAppNotifierPort whatsAppNotifierPort;

    public Message soumettrePreInscription(Message message, Long formationId) {
        // 1. Vérifier la formation
        Formation formation = formationRepositoryPort.findById(formationId)
                .orElseThrow(() -> new FormationNotFoundException(formationId));

        if (!formation.peutSInscrire()) {
            throw new FormationCompleteException("Impossible de se pré-inscrire : la formation est complète.");
        }

        // 2. Lier le message à la formation
        message.setFormationId(formation.getId());
        message.setFormationNom(formation.getNom());
        message.setDateCreation(LocalDateTime.now());

        // 3. Sauvegarder
        return messageRepositoryPort.save(message);
    }
}