package com.example.institue1.service;

import com.example.institue1.model.Message;
import com.example.institue1.utils.MessageUtils;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.email.from}")
    private String emailFrom;

    @Value("${app.email.admin}")
    private String emailAdmin;

    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${app.name}")
    private String appName;

    @Value("${app.website}")
    private String websiteUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    // === EMAILS UTILISATEURS ===

    public void envoyerConfirmationPreInscription(Message message) {
        if (!emailEnabled) {
            log.debug("Emails désactivés - Skip confirmation pré-inscription");
            return;
        }

        Context context = creerContextPreInscription(message);
        String contenu = templateEngine.process("email/confirmation-preinscription", context);

        envoyerEmail(
                message.getEmail(),
                "Confirmation de votre pré-inscription - " + appName,
                contenu
        );

        log.info("Email confirmation pré-inscription envoyé à: {}", message.getEmail());
    }

    public void envoyerConfirmationContact(Message message) {
        if (!emailEnabled) {
            log.debug("Emails désactivés - Skip confirmation contact");
            return;
        }

        Context context = creerContextContact(message);
        String contenu = templateEngine.process("email/confirmation-contact", context);

        envoyerEmail(
                message.getEmail(),
                "Confirmation de réception - " + appName,
                contenu
        );

        log.info("Email confirmation contact envoyé à: {}", message.getEmail());
    }

    // === EMAILS ADMIN ===

    public void notifierNouvellePreInscription(Message message) {
        if (!emailEnabled) {
            log.debug("Emails désactivés - Skip notification admin");
            return;
        }

        Context context = creerContextNotificationAdmin(message, "Pré-inscription");
        String contenu = templateEngine.process("email/notification-admin", context);

        envoyerEmail(
                emailAdmin,
                "🚨 Nouvelle pré-inscription - " + message.getFormationNom(),
                contenu
        );

        log.info("Notification admin pré-inscription envoyée pour ID: {}", message.getId());
    }

    public void notifierNouveauContact(Message message) {
        if (!emailEnabled) {
            log.debug("Emails désactivés - Skip notification admin");
            return;
        }

        Context context = creerContextNotificationAdmin(message, "Contact général");
        String contenu = templateEngine.process("email/notification-admin", context);

        envoyerEmail(
                emailAdmin,
                "📩 Nouveau message - " + message.getSujet(),
                contenu
        );

        log.info("Notification admin contact envoyée pour ID: {}", message.getId());
    }

    // === UTILITAIRES PRIVÉS ===

    private void envoyerEmail(String destinataire, String sujet, String contenuHtml) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(emailFrom);
            helper.setTo(destinataire);
            helper.setSubject(sujet);
            helper.setText(contenuHtml, true);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            log.error("Erreur envoi email vers {}: {}", destinataire, e.getMessage());
            throw new RuntimeException("Erreur envoi email", e);
        }
    }

    private Context creerContextPreInscription(Message message) {
        Context context = new Context(Locale.FRENCH);
        context.setVariable("nom", message.getNom());
        context.setVariable("formationNom", message.getFormationNom());
        context.setVariable("appName", appName);
        context.setVariable("websiteUrl", websiteUrl);
        context.setVariable("dateMessage", message.getDateCreation().format(DATE_FORMATTER));
        return context;
    }

    private Context creerContextContact(Message message) {
        Context context = new Context(Locale.FRENCH);
        context.setVariable("nom", message.getNom());
        context.setVariable("sujet", message.getSujet());
        context.setVariable("appName", appName);
        context.setVariable("websiteUrl", websiteUrl);
        context.setVariable("dateMessage", message.getDateCreation().format(DATE_FORMATTER));
        return context;
    }

    private Context creerContextNotificationAdmin(Message message, String typeLibelle) {
        Context context = new Context(Locale.FRENCH);
        context.setVariable("message", message);
        context.setVariable("typeLibelle", typeLibelle);
        context.setVariable("appName", appName);
        context.setVariable("dateMessage", message.getDateCreation().format(DATE_FORMATTER));
        context.setVariable("isUrgent", MessageUtils.isUrgent(message));
        context.setVariable("adminUrl", websiteUrl + "/admin/messages/" + message.getId());
        return context;
    }

    // === TESTS ET MONITORING ===

    public boolean testerConfiguration() {
        if (!emailEnabled) {
            log.warn("Service email désactivé");
            return false;
        }

        try {
            envoyerEmail(emailAdmin, "Test email - " + appName, "Test de configuration réussi");
            log.info("Test email envoyé avec succès");
            return true;

        } catch (Exception e) {
            log.error("Test email échoué: {}", e.getMessage());
            return false;
        }
    }
}