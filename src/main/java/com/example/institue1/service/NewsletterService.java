package com.example.institue1.service;

import com.example.institue1.model.NewsletterSubscription;
import com.example.institue1.repository.NewsletterSubscriptionRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final NewsletterSubscriptionRepository repository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    // Chemin RELATIF dans le JAR/production/dev
    @Value("${app.newsletter.catalogue-path:static/catalogue/MODALITES DE FORMATION - IBE - TBZ.pdf}")
    private String cataloguePath;

    @Value("${appName:Institut Beauty's Empire}")
    private String appName;

    @Value("${websiteUrl:https://institut-beautysempire.com/formations}")
    private String websiteUrl;

    public String subscribeAndSendCatalogue(String email) {
        NewsletterSubscription sub = repository.findByEmail(email).orElse(null);

        if (sub == null) {
            sub = new NewsletterSubscription();
            sub.setEmail(email);
            sub.setDateInscription(LocalDateTime.now());
            sub.setCatalogueEnvoye(false);
            repository.save(sub);
            sendCatalogueEmail(sub);
            return "Inscription réussie, catalogue envoyé.";
        } else if (!sub.getCatalogueEnvoye()) {
            sendCatalogueEmail(sub);
            return "Catalogue envoyé à l'email déjà inscrit.";
        } else {
            return "Vous êtes déjà inscrit, le catalogue a déjà été envoyé.";
        }
    }

    private void sendCatalogueEmail(NewsletterSubscription sub) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(sub.getEmail());
            helper.setSubject("Catalogue des Formations - " + appName);

            // Préparation du template avec Thymeleaf
            Context ctx = new Context();
            ctx.setVariable("nom",  "Cher(e) abonné(e)");
            ctx.setVariable("dateInscription", sub.getDateInscription());
            ctx.setVariable("appName", appName);
            ctx.setVariable("websiteUrl", websiteUrl);

            String htmlBody = templateEngine.process("email/confirmation-newsletter.html", ctx);
            helper.setText(htmlBody, true);

            // Ajout du PDF en pièce jointe (chemin relatif)
            helper.addAttachment("Catalogue-Formations.pdf", new ClassPathResource(cataloguePath));

            mailSender.send(message);

            sub.setCatalogueEnvoye(true);
            sub.setDateCatalogueEnvoye(LocalDateTime.now());
            repository.save(sub);
        } catch (Exception e) {
            throw new RuntimeException("Échec de l'envoi du PDF: " + e.getMessage(), e);
        }
    }
}