package com.example.institue1.service;

import com.example.institue1.model.Message;
import com.example.institue1.repository.MessageRepository;
import com.example.institue1.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// Service centralisé pour orchestrer toutes les notifications
// Responsabilité: logique métier des notifications, retry, priorisation
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;
    private final MessageRepository messageRepository;

    @Value("${app.notifications.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${app.notifications.email.retry-attempts:3}")
    private int maxRetryAttempts;

    @Value("${app.notifications.email.retry-delay-seconds:2}")
    private int retryDelaySeconds;

    // === ORCHESTRATION PRINCIPALES ===

    @Async
    public void notifierNouvellePreInscription(Message message) {
        log.info("Notifications pré-inscription ID: {}", message.getId());

        try {
            // 1. Email utilisateur (priorité haute)
            if (emailEnabled) {
                envoyerAvecRetry(() -> emailService.envoyerConfirmationPreInscription(message), message.getId(), "confirmation");
                marquerEmailEnvoye(message);
            }

            // 2. Notification admin
            if (emailEnabled) {
                envoyerAvecRetry(() -> emailService.notifierNouvellePreInscription(message), message.getId(), "admin");
            }

            // 3. Notifications urgentes supplémentaires
            if (MessageUtils.isUrgent(message)) {
                traiterMessageUrgent(message);
            }

            log.info("Notifications pré-inscription terminées ID: {}", message.getId());

        } catch (Exception e) {
            log.error("Erreur notifications pré-inscription ID {}: {}", message.getId(), e.getMessage());
        }
    }

    @Async
    public void notifierNouveauContact(Message message) {
        log.info("Notifications contact ID: {}", message.getId());

        try {
            // 1. Email utilisateur
            if (emailEnabled) {
                envoyerAvecRetry(() -> emailService.envoyerConfirmationContact(message), message.getId(), "confirmation");
                marquerEmailEnvoye(message);
            }

            // 2. Notification admin (priorité normale)
            if (emailEnabled) {
                emailService.notifierNouveauContact(message);
            }

            log.info("Notifications contact terminées ID: {}", message.getId());

        } catch (Exception e) {
            log.error("Erreur notifications contact ID {}: {}", message.getId(), e.getMessage());
        }
    }

    // === LOGIQUE MÉTIER ===

    private void traiterMessageUrgent(Message message) {
        log.info("Message urgent détecté ID: {} - notifications renforcées", message.getId());

        // Logique future: SMS admin, Slack, etc.
        // if (isHeuresOuvertes() && hasAdminPhoneNumber()) {
        //     smsService.notifierAdminUrgent(message);
        // }
    }

    private void envoyerAvecRetry(Runnable emailAction, Long messageId, String type) {
        int tentative = 1;

        while (tentative <= maxRetryAttempts) {
            try {
                emailAction.run();
                log.info("Email {} envoyé (tentative {}) pour message {}", type, tentative, messageId);
                return;

            } catch (Exception e) {
                log.warn("Échec email {} tentative {} pour message {}: {}",
                        type, tentative, messageId, e.getMessage());

                if (tentative < maxRetryAttempts) {
                    attendreAvantRetry(tentative);
                }
                tentative++;
            }
        }

        log.error("Échec définitif email {} pour message {}", type, messageId);
    }

    private void attendreAvantRetry(int tentative) {
        try {
            Thread.sleep(retryDelaySeconds * 1000L * tentative); // Délai exponentiel
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interruption pendant attente retry");
        }
    }

    private void marquerEmailEnvoye(Message message) {
        try {
            message.setEmailConfirmationEnvoye(true);
            message.setDateEmailConfirmation(LocalDateTime.now());
            messageRepository.save(message);

        } catch (Exception e) {
            log.warn("Erreur sauvegarde statut email pour message {}: {}", message.getId(), e.getMessage());
        }
    }

    // === SERVICES AVANCÉS ===

    @Async
    public void envoyerRappelAdmin(Message message) {
        if (!MessageUtils.isAncien(message)) {
            return; // Pas encore ancien
        }

        try {
            log.info("Rappel admin pour message ancien ID: {}", message.getId());
            // emailService.envoyerRappelAdmin(message); // À implémenter

        } catch (Exception e) {
            log.error("Erreur rappel admin ID {}: {}", message.getId(), e.getMessage());
        }
    }

    public boolean testerToutesLesNotifications() {
        log.info("Test configurations notifications");

        boolean emailOk = emailEnabled && emailService.testerConfiguration();

        log.info("Résultats tests: email={}", emailOk);
        return emailOk;
    }

    // === UTILITAIRES PRIVÉS ===

    private boolean isHeuresOuvertes() {
        LocalDateTime now = LocalDateTime.now();
        int heure = now.getHour();
        int jour = now.getDayOfWeek().getValue();

        // Lun-Ven 8h-18h
        return jour <= 5 && heure >= 8 && heure <= 18;
    }

    private boolean isWeekend() {
        return LocalDateTime.now().getDayOfWeek().getValue() >= 6;
    }
}