package com.example.institue1.service;

import com.example.institue1.model.Message;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine){
        this.mailSender =mailSender;
        this.templateEngine = templateEngine;
    }

    @Value("${app.email.from:contact@beautysempire.cm}")
    private String fromEmail;

    @Value("${app.email.admin:admin@beautysempire.cm}")
    private String adminEmail;

    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${app.name:Institut Beauty's Empire}")
    private String appName;

    @Value("${app.website:https://beautysempire.cm}")
    private String websiteUrl;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    // === EMAILS CLIENTS ===
    @Async
    public void envoyerConfirmationContact(Message message) {
        if (!emailEnabled) {
            log.debug("Envoi emails désactivé - confirmation contact ignorée");
            return;
        }

        try {
            Context context = new Context(Locale.FRENCH);
            context.setVariable("nom", message.getNom());
            context.setVariable("sujet", message.getSujet() != null ? message.getSujet() : "Votre demande");
            context.setVariable("appName", appName);
            context.setVariable("websiteUrl", websiteUrl);
            context.setVariable("dateMessage", message.getDateCreation().format(FORMATTER));
            context.setVariable("isPreInscription", message.getType().name().equals("PRE_INSCRIPTION"));
            context.setVariable("formationNom", message.getFormationNom());

            String templateName = message.getType().name().equals("PRE_INSCRIPTION")
                    ? "email/confirmation-preinscription"
                    : "email/confirmation-contact";

            String htmlContent = templateEngine.process(templateName, context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, appName);
            helper.setTo(message.getEmail());
            helper.setSubject(generateConfirmationSubject(message));
            helper.setText(htmlContent, true);

            // AJOUT DU LOGO
            try {
                ClassPathResource logo = new ClassPathResource("static/Beauty's Empire.png");
                if (logo.exists()) {
                    helper.addInline("logo", logo);
                    log.debug("Logo ajouté à l'email de confirmation");
                } else {
                    log.warn("Logo non trouvé : static/Beauty's Empire.png");
                }
            } catch (Exception e) {
                log.warn("Impossible d'ajouter le logo : {}", e.getMessage());
            }

            mailSender.send(mimeMessage);
            log.info("Email confirmation envoyé à : {}", message.getEmail());

        } catch (Exception e) {
            log.error("Erreur envoi email confirmation à {}: {}", message.getEmail(), e.getMessage());
        }
    }

    // === EMAILS ADMIN ===
    @Async
    public void notifierNouveauMessage(Message message) {
        if (!emailEnabled) {
            log.debug("Envoi emails désactivé - notification admin ignorée");
            return;
        }

        try {
            Context context = new Context(Locale.FRENCH);
            context.setVariable("message", message);
            context.setVariable("appName", appName);
            context.setVariable("websiteUrl", websiteUrl);
            context.setVariable("dateMessage", message.getDateCreation().format(FORMATTER));
            context.setVariable("typeLibelle", message.getType().getLibelle());
            context.setVariable("isUrgent", message.getType().isUrgent());
            context.setVariable("adminUrl", websiteUrl + "/admin/messages/" + message.getId());

            String htmlContent = templateEngine.process("email/notification-admin", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, appName);
            helper.setTo(adminEmail);
            helper.setSubject(generateNotificationSubject(message));
            helper.setText(htmlContent, true);

            // AJOUT DU LOGO
            try {
                ClassPathResource logo = new ClassPathResource("static/Beauty's Empire.png");
                if (logo.exists()) {
                    helper.addInline("logo", logo);
                    log.debug("Logo ajouté à l'email admin");
                } else {
                    log.warn("Logo non trouvé : static/Beauty's Empire.png");
                }
            } catch (Exception e) {
                log.warn("Impossible d'ajouter le logo : {}", e.getMessage());
            }

            // Marquer comme prioritaire si urgent
            if (message.getType().isUrgent()) {
                helper.setPriority(1);
            }

            mailSender.send(mimeMessage);
            log.info("Notification admin envoyée pour message ID: {}", message.getId());

        } catch (Exception e) {
            log.error("Erreur envoi notification admin pour message {}: {}", message.getId(), e.getMessage());
        }
    }

    @Async
    public void envoyerRelancePreInscription(Message message) {
        if (!emailEnabled || !message.getType().name().equals("PRE_INSCRIPTION")) {
            return;
        }

        try {
            Context context = new Context(Locale.FRENCH);
            context.setVariable("nom", message.getNom());
            context.setVariable("formationNom", message.getFormationNom());
            context.setVariable("appName", appName);
            context.setVariable("websiteUrl", websiteUrl);
            context.setVariable("telephone", getContactTelephone());

            String htmlContent = templateEngine.process("email/relance-preinscription", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, appName);
            helper.setTo(message.getEmail());
            helper.setSubject("🎓 Suite à votre pré-inscription - " + message.getFormationNom());
            helper.setText(htmlContent, true);

            // AJOUT DU LOGO
            try {
                ClassPathResource logo = new ClassPathResource("static/Beauty's Empire.png");
                if (logo.exists()) {
                    helper.addInline("logo", logo);
                    log.debug("Logo ajouté à l'email de relance");
                } else {
                    log.warn("Logo non trouvé : static/Beauty's Empire.png");
                }
            } catch (Exception e) {
                log.warn("Impossible d'ajouter le logo : {}", e.getMessage());
            }

            mailSender.send(mimeMessage);
            log.info("Email relance envoyé à : {} pour formation: {}", message.getEmail(), message.getFormationNom());

        } catch (Exception e) {
            log.error("Erreur envoi relance à {}: {}", message.getEmail(), e.getMessage());
        }
    }

    private String generateConfirmationSubject(Message message) {
        if (message.getType().name().equals("PRE_INSCRIPTION")) {
            return "✅ Pré-inscription reçue - " + (message.getFormationNom() != null ? message.getFormationNom() : "Formation");
        }
        return "✅ Votre message a été reçu - " + appName;
    }

    private String generateNotificationSubject(Message message) {
        String prefix = message.getType().isUrgent() ? "🔴 URGENT - " : "📩 ";
        String type = message.getType().getLibelle();
        String formation = message.getFormationNom() != null ? " - " + message.getFormationNom() : "";

        return prefix + type + formation + " (" + message.getNom() + ")";
    }

    private String getContactTelephone() {
        return "+237 6XX XX XX XX"; // À configurer via properties
    }
}