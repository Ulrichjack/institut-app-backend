package com.example.institue1.service;

import com.example.institue1.dto.contactInscription.*;
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

// Service métier pour la gestion des messages
// Responsabilité: CRUD messages, logique métier, statistiques
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final FormationRepository formationRepository;
    private final MessageMapper messageMapper;
    private final NotificationService notificationService;

    // === CRÉATION MESSAGES ===

    public MessageDetailDto creerContact(ContactCreateDto dto) {
        log.info("Création contact de: {}", dto.getEmail());

        // Mapping et enrichissement
        Message message = messageMapper.fromContactCreateDto(dto);
        message.setType(TypeMessage.CONTACT_GENERAL);
        enrichirMessage(message, dto.getSourceVisite(), dto.getAdresseIP(), dto.getUserAgent());

        // Formation optionnelle
        attacherFormation(message, dto.getFormationId());

        // Sauvegarde
        Message saved = messageRepository.save(message);

        // Notifications asynchrones (ne bloque pas la réponse)
        notificationService.notifierNouveauContact(saved);

        log.info("Contact créé ID: {}", saved.getId());
        return messageMapper.toDetailDto(saved);
    }

    public MessageDetailDto creerPreInscription(PreInscriptionCreateDto dto) {
        log.info("Création pré-inscription formation ID: {}", dto.getFormationId());

        // Formation obligatoire pour pré-inscription
        Formation formation = formationRepository.findById(dto.getFormationId())
                .orElseThrow(() -> new EntityNotFoundException("Formation introuvable ID: " + dto.getFormationId()));

        // Mapping et enrichissement
        Message message = messageMapper.fromPreInscriptionCreateDto(dto, formation);
        message.setType(TypeMessage.PRE_INSCRIPTION);
        message.setFormationNom(formation.getNom()); // Backup historique
        enrichirMessage(message, dto.getSourceVisite(), dto.getAdresseIP(), dto.getUserAgent());

        // Sujet automatique si manquant
        if (isBlank(message.getSujet())) {
            message.setSujet(MessageUtils.genererSujetAuto(TypeMessage.PRE_INSCRIPTION, formation.getNom()));
        }

        // Sauvegarde
        Message saved = messageRepository.save(message);

        // Notifications prioritaires asynchrones
        notificationService.notifierNouvellePreInscription(saved);

        log.info("Pré-inscription créée ID: {}", saved.getId());
        return messageMapper.toDetailDto(saved);
    }

    // === CONSULTATION ===

    @Transactional(readOnly = true)
    public Page<MessageListDto> listerMessages(MessageFilterDto filtre, Pageable pageable) {
        log.debug("Liste messages - filtres: {}", filtre);

        Specification<Message> spec = construireSpecification(filtre);
        Page<Message> messages = messageRepository.findAll(spec, pageable);

        return messages.map(messageMapper::toListDto);
    }

    @Transactional(readOnly = true)
    public MessageDetailDto obtenirMessage(Long id) {
        Message message = findMessageById(id);
        return messageMapper.toDetailDto(message);
    }

    // === GESTION STATUTS ===

    public MessageDetailDto changerStatut(Long id, ChangeStatutDto dto, String admin) {
        Message message = findMessageById(id);
        StatutMessage nouveauStatut = StatutMessage.valueOf(dto.getNouveauStatut());

        // Validation transition
        if (!message.getStatut().peutEvoluerVers(nouveauStatut)) {
            throw new IllegalStateException(
                    String.format("Transition impossible: %s -> %s", message.getStatut(), nouveauStatut)
            );
        }

        // Application changement
        appliquerNouveauStatut(message, nouveauStatut, admin);

        // Commentaire optionnel
        if (!isBlank(dto.getCommentaire())) {
            log.info("Commentaire admin message {}: {}", id, dto.getCommentaire());
            // TODO: Système commentaires internes
        }

        Message saved = messageRepository.save(message);
        log.info("Statut message {} -> {} par {}", id, nouveauStatut, admin);

        return messageMapper.toDetailDto(saved);
    }

    public void marquerCommeLu(Long id, String admin) {
        Message message = findMessageById(id);
        MessageUtils.marquerCommeLu(message, admin);
        messageRepository.save(message);
        log.debug("Message {} marqué lu par {}", id, admin);
    }

    // === STATISTIQUES ===

    @Transactional(readOnly = true)
    public MessageStatsDto obtenirStatistiques() {
        log.debug("Calcul statistiques messages");

        MessageStatsDto stats = new MessageStatsDto();
        LocalDateTime maintenant = LocalDateTime.now();

        // Compteurs globaux
        stats.setTotalMessages(messageRepository.count());
        stats.setMessagesNonLus(compterParStatut(StatutMessage.NON_LU));
        stats.setMessagesTraites(compterParStatut(StatutMessage.TRAITE));
        stats.setMessagesArchives(compterParStatut(StatutMessage.ARCHIVE));

        // Par type
        stats.setContactsGeneraux(compterParType(TypeMessage.CONTACT_GENERAL));
        stats.setPreInscriptions(compterParType(TypeMessage.PRE_INSCRIPTION));

        // Urgences et performance
        stats.setMessagesUrgents(compterMessagesUrgents());
        stats.setMessagesAnciens(compterMessagesAnciens());

        // Evolution temporelle
        stats.setMessagesDuJour(compterDepuis(maintenant.minusDays(1)));
        stats.setMessagesDeLaSemaine(compterDepuis(maintenant.minusWeeks(1)));
        stats.setMessagesDuMois(compterDepuis(maintenant.minusMonths(1)));

        // TODO: Métriques avancées avec requêtes natives
        stats.setTopFormations(List.of());
        stats.setTauxReponse24h(85.0); // À calculer réellement
        stats.setTempsReponseMovenHeures(4.2); // À calculer

        return stats;
    }

    // === MÉTHODES PRIVÉES ===

    private void enrichirMessage(Message message, String sourceVisite, String adresseIP, String userAgent) {
        message.setSourceVisite(sourceVisite);
        message.setAdresseIP(adresseIP);
        message.setUserAgent(userAgent);
        message.setDateCreation(LocalDateTime.now());

        // Formatage téléphone
        if (message.getTelephone() != null) {
            message.setTelephone(MessageUtils.formaterTelephone(message.getTelephone()));
        }

        // Sauvegarde nom formation pour historique
        MessageUtils.sauvegarderNomFormation(message);
    }

    private void attacherFormation(Message message, Long formationId) {
        if (formationId != null) {
            Formation formation = formationRepository.findById(formationId).orElse(null);
            message.setFormationInteresse(formation);
            if (formation != null) {
                message.setFormationNom(formation.getNom());
            }
        }
    }

    private void appliquerNouveauStatut(Message message, StatutMessage nouveauStatut, String admin) {
        switch (nouveauStatut) {
            case LU -> MessageUtils.marquerCommeLu(message, admin);
            case TRAITE -> MessageUtils.marquerCommeTraite(message, admin);
            case ARCHIVE -> MessageUtils.archiverMessage(message, admin);
        }
    }

    private Message findMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Message introuvable ID: " + id));
    }

    private Specification<Message> construireSpecification(MessageFilterDto filtre) {
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

            if (!isBlank(filtre.getEmail())) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("email")), "%" + filtre.getEmail().toLowerCase() + "%"));
            }

            if (!isBlank(filtre.getFormationNom())) {
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

            if (!isBlank(filtre.getSourceVisite())) {
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

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}