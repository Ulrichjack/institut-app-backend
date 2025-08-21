package com.example.institue1.service;

import com.example.institue1.dto.contactInscription.*;
import com.example.institue1.dto.whatsapp.WhatsAppResponseDto;
import com.example.institue1.enums.StatutMessage;
import com.example.institue1.enums.TypeMessage;
import com.example.institue1.mapper.MessageMapper;
import com.example.institue1.model.Formation;
import com.example.institue1.model.Message;
import com.example.institue1.repository.FormationRepository;
import com.example.institue1.repository.MessageRepository;
import com.example.institue1.utils.MessageUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
public class MessageService {
    private final MessageRepository messageRepository;
    private final FormationRepository formationRepository;
    private final MessageMapper messageMapper;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;

    public MessageService (MessageRepository messageRepository,
                           FormationRepository formationRepository,
                           MessageMapper messageMapper,
                           EmailService emailService,
                           WhatsAppService whatsAppService){
        this.emailService = emailService;
        this.formationRepository = formationRepository;
        this.messageMapper =messageMapper;
        this.messageRepository = messageRepository;
        this.whatsAppService = whatsAppService;
    }

    public MessageDetailDto creerContact(ContactCreateDto dto) {
        log.info("Création nouveau contact de: {}", dto.getEmail());

        Message message = messageMapper.fromContactCreateDto(dto);
        message.setType(TypeMessage.CONTACT_GENERAL);
        // Enrichissement automatique
        enrichirMessage(message, dto.getSourceVisite(), dto.getAdresseIP(), dto.getUserAgent());
        // Si formation spécifiée, la récupérer
        if (dto.getFormationId() != null) {
            Formation formation = formationRepository.findById(dto.getFormationId())
                    .orElse(null);
            message.setFormationInteresse(formation);
            if (formation != null) {
                message.setFormationNom(formation.getNom());
            }
        }
        // Sauvegarde
        Message saved = messageRepository.save(message);
        // Notifications asynchrones
        envoyerNotifications(saved);
        log.info("Contact créé avec ID: {}", saved.getId());
        return messageMapper.toDetailDto(saved);
    }

    public MessageDetailDto creerPreInscription(PreInscriptionCreateDto dto) {
        log.info("Création pré-inscription pour formation ID: {}", dto.getFormationId());
        // Récupération formation obligatoire
        Formation formation = formationRepository.findById(dto.getFormationId())
                .orElseThrow(() -> new EntityNotFoundException("Formation introuvable ID: " + dto.getFormationId()));
        Message message = messageMapper.fromPreInscriptionCreateDto(dto, formation);
        message.setType(TypeMessage.PRE_INSCRIPTION);
        message.setFormationNom(formation.getNom()); // Sauvegarde historique
        // Enrichissement
        enrichirMessage(message, dto.getSourceVisite(), dto.getAdresseIP(), dto.getUserAgent());
        // Génération sujet automatique si vide
        if (message.getSujet() == null || message.getSujet().trim().isEmpty()) {
            message.setSujet(MessageUtils.genererSujetAuto(TypeMessage.PRE_INSCRIPTION, formation.getNom()));
        }
        Message saved = messageRepository.save(message);
        // Notifications prioritaires pour pré-inscriptions
        envoyerNotifications(saved);
        log.info("Pré-inscription créée avec ID: {}", saved.getId());
        return messageMapper.toDetailDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<MessageListDto> listerMessages(MessageFilterDto filtre, Pageable pageable) {
        log.debug("Listing messages avec filtres: {}", filtre);

        Specification<Message> spec = buildSpecification(filtre);
        Page<Message> messages = messageRepository.findAll(spec, pageable);

        return messages.map(messageMapper::toListDto);
    }
    @Transactional(readOnly = true)
    public MessageDetailDto obtenirMessage(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Message introuvable ID: " + id));

        return messageMapper.toDetailDto(message);
    }

    @Transactional(readOnly = true)
    public MessageStatsDto obtenirStatistiques() {
        log.debug("Calcul statistiques messages");

        MessageStatsDto stats = new MessageStatsDto();

        // Compteurs globaux
        stats.setTotalMessages(messageRepository.count());
        stats.setMessagesNonLus(compterParStatut(StatutMessage.NON_LU));
        stats.setMessagesTraites(compterParStatut(StatutMessage.TRAITE));
        stats.setMessagesArchives(compterParStatut(StatutMessage.ARCHIVE));

        // Par type
        stats.setContactsGeneraux(compterParType(TypeMessage.CONTACT_GENERAL));
        stats.setPreInscriptions(compterParType(TypeMessage.PRE_INSCRIPTION));

        // Urgences et anciens
        stats.setMessagesUrgents(compterMessagesUrgents());
        stats.setMessagesAnciens(compterMessagesAnciens());

        // Évolution temporelle
        LocalDateTime maintenant = LocalDateTime.now();
        stats.setMessagesDuJour(compterDepuis(maintenant.minusDays(1)));
        stats.setMessagesDeLaSemaine(compterDepuis(maintenant.minusWeeks(1)));
        stats.setMessagesDuMois(compterDepuis(maintenant.minusMonths(1)));

        // TODO: Top formations et performances (requêtes complexes)
        stats.setTopFormations(List.of()); // À implémenter avec des requêtes natives
        stats.setTauxReponse24h(85.0); // Mock - à calculer réellement
        stats.setTempsReponseMovenHeures(4.2); // Mock

        return stats;
    }

    public MessageDetailDto changerStatut(Long id, ChangeStatutDto dto, String admin) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Message introuvable ID: " + id));

        StatutMessage nouveauStatut = StatutMessage.valueOf(dto.getNouveauStatut());

        // Validation transition
        if (!message.getStatut().peutEvoluerVers(nouveauStatut)) {
            throw new IllegalStateException(
                    String.format("Transition impossible: %s -> %s",
                            message.getStatut(), nouveauStatut)
            );
        }

        // Application du changement avec utilitaires
        switch (nouveauStatut) {
            case LU -> MessageUtils.marquerCommeLu(message, admin);
            case TRAITE -> MessageUtils.marquerCommeTraite(message, admin);
            case ARCHIVE -> MessageUtils.archiverMessage(message, admin);
        }

        // Ajout commentaire si fourni
        if (dto.getCommentaire() != null && !dto.getCommentaire().trim().isEmpty()) {
            // TODO: Ajouter système de commentaires/notes internes
            log.info("Commentaire admin sur message {}: {}", id, dto.getCommentaire());
        }

        Message saved = messageRepository.save(message);
        log.info("Statut message {} changé vers {} par {}", id, nouveauStatut, admin);

        return messageMapper.toDetailDto(saved);
    }

    public void marquerCommeLu(Long id, String admin) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Message introuvable ID: " + id));

        MessageUtils.marquerCommeLu(message, admin);
        messageRepository.save(message);
    }

    private void enrichirMessage(Message message, String sourceVisite, String adresseIP, String userAgent) {
        message.setSourceVisite(sourceVisite);
        message.setAdresseIP(adresseIP);
        message.setUserAgent(userAgent);
        message.setDateCreation(LocalDateTime.now());

        // Formatage téléphone si fourni
        if (message.getTelephone() != null) {
            message.setTelephone(MessageUtils.formaterTelephone(message.getTelephone()));
        }

        // Sauvegarde nom formation si relation existe
        MessageUtils.sauvegarderNomFormation(message);
    }


    private void envoyerNotifications(Message message) {
        try {
            // Priorité à WhatsApp si numéro disponible
            if (message.getTelephone() != null && !message.getTelephone().isEmpty()) {
                // Envoi différent selon le type de message
                WhatsAppResponseDto response;

                if (message.getType() == TypeMessage.PRE_INSCRIPTION) {
                    response = whatsAppService.envoyerNotificationPreInscription(message);
                } else {
                    response = whatsAppService.envoyerNotificationContactGeneral(message);
                }

                if (response != null) {
                    message.setWhatsappNotificationEnvoye(true);
                    message.setDateWhatsappNotification(LocalDateTime.now());
                }
            }

            // Email toujours envoyé comme backup formel
            emailService.envoyerConfirmationContact(message);
            message.setEmailConfirmationEnvoye(true);
            message.setDateEmailConfirmation(LocalDateTime.now());

            // Notification admin par email
            emailService.notifierNouveauMessage(message);

            // Notification WhatsApp pour admin si message urgent
            if (message.getType().isUrgent() || message.getType().necessiteNotificationWhatsApp()) {
                whatsAppService.envoyerAlertAdminNouveauMessage(message);
            }

        } catch (Exception e) {
            log.error("Erreur envoi notifications pour message {}: {}",
                    message.getId(), e.getMessage());
        }
    }
    private Specification<Message> buildSpecification(MessageFilterDto filtre) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtre.getStatut() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("statut"), StatutMessage.valueOf(filtre.getStatut())));
            }

            if (filtre.getType() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("type"), TypeMessage.valueOf(filtre.getType())));
            }

            if (filtre.getEmail() != null) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("email")), "%" + filtre.getEmail().toLowerCase() + "%"));
            }

            if (filtre.getFormationNom() != null) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("formationNom")), "%" + filtre.getFormationNom().toLowerCase() + "%"));
            }

            if (filtre.getDateDebut() != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("dateCreation"), filtre.getDateDebut()));
            }

            if (filtre.getDateFin() != null) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("dateCreation"), filtre.getDateFin()));
            }

            if (filtre.getSourceVisite() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("sourceVisite"), filtre.getSourceVisite()));
            }

            return predicates;
        };
    }

    // Méthodes de comptage pour statistiques
    private long compterParStatut(StatutMessage statut) {
        return messageRepository.count((root, query, cb) ->
                cb.equal(root.get("statut"), statut));
    }

    private long compterParType(TypeMessage type) {
        return messageRepository.count((root, query, cb) ->
                cb.equal(root.get("type"), type));
    }

    private long compterMessagesUrgents() {
        return messageRepository.count((root, query, cb) ->
                cb.and(
                        cb.equal(root.get("type"), TypeMessage.PRE_INSCRIPTION),
                        cb.in(root.get("statut")).value(List.of(StatutMessage.NON_LU, StatutMessage.LU))
                ));
    }

    private long compterMessagesAnciens() {
        LocalDateTime seuil = LocalDateTime.now().minusHours(24);
        return messageRepository.count((root, query, cb) ->
                cb.and(
                        cb.lessThan(root.get("dateCreation"), seuil),
                        cb.in(root.get("statut")).value(List.of(StatutMessage.NON_LU, StatutMessage.LU))
                ));
    }

    private long compterDepuis(LocalDateTime depuis) {
        return messageRepository.count((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("dateCreation"), depuis));
    }


}
