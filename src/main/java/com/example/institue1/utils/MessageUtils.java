package com.example.institue1.utils;

import com.example.institue1.enums.StatutMessage;
import com.example.institue1.enums.TypeMessage;
import com.example.institue1.model.Message;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@UtilityClass
@Slf4j
public class MessageUtils {

    //Marque le message comme lu par un admin/
    public  void marquerCommeLu(Message message, String admin) {
        if (message.getStatut() == StatutMessage.NON_LU) {
            message.setStatut(StatutMessage.LU);
            message.setDateLecture(LocalDateTime.now());
            message.setTraiteParAdmin(admin);
            log.info("Message {} marqué comme lu par {}", message.getId(), admin);
        }
    }

    /**
     * Marque le message comme traité par un admin
     */
    public  void marquerCommeTraite(Message message, String admin) {
        if (message.getStatut().peutEvoluerVers(StatutMessage.TRAITE)) {
            message.setStatut(StatutMessage.TRAITE);
            message.setDateTraitement(LocalDateTime.now());
            message.setTraiteParAdmin(admin);

            // Si pas encore marqué comme lu
            if (message.getDateLecture() == null) {
                message.setDateLecture(LocalDateTime.now());
            }

            log.info("Message {} marqué comme traité par {}", message.getId(), admin);
        }
    }

    /**
     * Archive un message traité
     */
    public  void archiverMessage(Message message, String admin) {
        if (message.getStatut().peutEvoluerVers(StatutMessage.ARCHIVE)) {
            message.setStatut(StatutMessage.ARCHIVE);
            message.setTraiteParAdmin(admin);
            log.info("Message {} archivé par {}", message.getId(), admin);
        }
    }

    // === CALCULS MÉTIER ===

    /**
     * Détermine si le message est urgent (pré-inscription non lue/non traitée)
     */
    public  boolean isUrgent(Message message) {
        return message.getType().isUrgent() && message.getStatut().necessiteAction();
    }

    /**
     * Calcule l'âge du message en heures
     */
    public  long getAgeEnHeures(Message message) {
        return ChronoUnit.HOURS.between(message.getDateCreation(), LocalDateTime.now());
    }

    /**
     * Détermine si le message est ancien (>24h non traité)
     */
    public boolean isAncien(Message message) {
        return getAgeEnHeures(message) > 24 && message.getStatut().necessiteAction();
    }

    /**
     * Calcule la priorité du message (1=max, 5=min)
     */
    public  int calculerPriorite(Message message) {
        if (isUrgent(message)) {
            long age = getAgeEnHeures(message);
            if (age > 24) return 1; // Très urgent
            if (age > 12) return 2; // Urgent
            return 3; // Important
        }
        return message.getType() == TypeMessage.CONTACT_GENERAL ? 4 : 5;
    }

    // === GÉNÉRATION AUTOMATIQUE ===

    /**
     * Génère un sujet automatique basé sur le type
     */
    public String genererSujetAuto(TypeMessage type, String formationNom) {
        return switch (type) {
            case CONTACT_GENERAL -> "Demande d'information générale";
            case PRE_INSCRIPTION -> formationNom != null ?
                    "Pré-inscription : " + formationNom :
                    "Demande de pré-inscription";
        };
    }

    /**
     * Sauvegarde le nom de la formation pour historique
     */
    public  void sauvegarderNomFormation(Message message) {
        if (message.getFormationInteresse() != null && message.getFormationNom() == null) {
            message.setFormationNom(message.getFormationInteresse().getNom());
            log.debug("Nom formation sauvegardé : {}", message.getFormationNom());
        }
    }

    // === VALIDATION SIMPLE ET EFFICACE ===

    /**
     * Formate téléphone en retirant espaces/caractères parasites
     * Pas de validation stricte - on fait confiance à l'utilisateur
     */
    public  String formaterTelephone(String telephone) {
        if (telephone == null || telephone.trim().isEmpty()) {
            return null;
        }

        // Juste nettoyer : supprimer espaces, tirets, parenthèses
        String tel = telephone.replaceAll("[\\s\\-\\(\\)]", "");


        return tel;
    }

    // === ANTI-SPAM ===

    /**
     * Calcule score de spam (0-100, 100=spam certain)
     */
    public  int calculerScoreSpam(Message message) {
        int score = 0;

        String msg = message.getMessage().toLowerCase();

        // Liens suspects
        if (msg.contains("http") || msg.contains("www.") || msg.contains(".com")) {
            score += 30;
        }

        // Mots spam courants
        String[] motsSuspects = {"gratuit", "free", "urgent", "promotion", "limited time", "click here"};
        for (String mot : motsSuspects) {
            if (msg.contains(mot)) score += 15;
        }

        // Message trop court ou trop long
        if (message.getMessage().length() < 20) score += 20;
        if (message.getMessage().length() > 1500) score += 10;

        // Email suspect - on fait confiance à @Email de Bean Validation
        // Juste vérifier si complètement vide ou null
        if (message.getEmail() == null || message.getEmail().trim().isEmpty()) {
            score += 50; // Email vide = très suspect
        }

        return Math.min(score, 100);
    }

    /**
     * Extrait informations du User Agent
     */
    public  String extraireInfosNavigateur(String userAgent) {
        if (userAgent == null) return "Inconnu";

        if (userAgent.contains("Mobile")) return "Mobile";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        if (userAgent.contains("Edge")) return "Edge";

        return "Autre";
    }
}
