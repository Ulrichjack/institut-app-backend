package cm.beautysempire.institut.application.service;

import cm.beautysempire.institut.application.exception.FormationCompleteException;
import cm.beautysempire.institut.application.exception.FormationNotFoundException;
import cm.beautysempire.institut.application.port.WhatsAppNotifierPort;
import cm.beautysempire.institut.domain.formation.Formation;
import cm.beautysempire.institut.domain.formation.FormationRepositoryPort;
import cm.beautysempire.institut.domain.messages.Message;
import cm.beautysempire.institut.domain.messages.MessageRepositoryPort;
import cm.beautysempire.institut.domain.messages.StatutMessage;
import cm.beautysempire.institut.domain.messages.TypeMessage;
import cm.beautysempire.institut.application.exception.MessageNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class MessageUseCase {

    private final MessageRepositoryPort messageRepositoryPort;
    private final FormationRepositoryPort formationRepositoryPort;
    private final WhatsAppNotifierPort whatsAppNotifierPort;

    // 1. Soumettre un simple contact (Question générale)
    public Message soumettreContact(Message message) {
        message.initialiserCreation();
        return messageRepositoryPort.save(message);

    }

    // 2. Soumettre une pré-inscription (Liée à une formation)
    public Message soumettrePreInscription(Message message, Long formationId) {
        Formation formation = formationRepositoryPort.findById(formationId)
                .orElseThrow(() -> new FormationNotFoundException(formationId));

        if (!formation.peutSInscrire()) {
            throw new FormationCompleteException("Impossible de se pré-inscrire : la formation est complète ou inactive.");
        }

        message.setFormationId(formation.getId());
        message.setFormationNom(formation.getNom());
        message.initialiserCreation();

        return messageRepositoryPort.save(message);
    }

    // 3. Générer le lien WhatsApp pour le client
    public String genererLienWhatsAppClient(Message message) {
        return whatsAppNotifierPort.genererLienConfirmationClient(message);
    }

    // 4. Changer le statut d'un message (Pour l'admin)
    public Message marquerCommeLu(Long messageId, String admin) {
        Message message = messageRepositoryPort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        message.marquerCommeLu(admin);
        return messageRepositoryPort.save(message);
    }



    // 6. Obtenir les statistiques (Pour le Dashboard Admin)
    public long compterMessagesNonLus() {

        return messageRepositoryPort.countByStatut(StatutMessage.NON_LU);
    }

    public Page<Message> listerMessagesPagines(int page, int size) {
        return listerMessagesPagines(page, size, "", null, null);
    }

    public Page<Message> listerMessagesPagines(int page, int size, String query, StatutMessage statut, TypeMessage type) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("La page doit être positive ou nulle et la taille comprise entre 1 et 100.");
        }
        PageRequest pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "dateCreation", "id"));
        return messageRepositoryPort.search(query == null ? "" : query.trim(), statut, type, pageable);
    }

    public Message marquerCommeTraite(Long messageId, String admin) {
        Message message = messageRepositoryPort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        message.marquerCommeTraite(admin);
        return messageRepositoryPort.save(message);
    }

    public Message marquerCommeArchive(Long messageId, String admin) {
        Message message = messageRepositoryPort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        message.marquerCommeArchive(admin);
        return messageRepositoryPort.save(message);
    }
}
