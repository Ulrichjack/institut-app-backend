package cm.beautysempire.institut.infrastructure.notification;

import cm.beautysempire.institut.application.port.WhatsAppNotifierPort;
import cm.beautysempire.institut.domain.messages.Message;
import cm.beautysempire.institut.domain.messages.TypeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class WhatsAppNotifierAdapter implements WhatsAppNotifierPort {

    @Value("${app.whatsapp.admin-number}")
    private String adminNumber;

    @Override
    public String genererLienAdmin(Message message) {
        String texte = String.format(
                "🔔 Nouvelle %s IBE%n👤 %s%n📱 %s%n📚 %s%n🏙️ %s%n💬 %s",
                message.getType().getLibelle(),
                message.getNom(),
                message.getTelephone(),
                message.getFormationNom() != null ? message.getFormationNom() : "Contact général",
                message.getVille() != null ? message.getVille() : "Non renseigné",
                message.getMessage() != null ? message.getMessage() : ""
        );
        return construireLien(adminNumber, texte);
    }

    @Override
    public String genererLienConfirmationClient(Message message) {
        String texte;

        if (message.getType() == TypeMessage.PRE_INSCRIPTION) {
            texte = String.format(
                    "Bonjour l'Institut Beauty's Empire ! \n\n" +
                            "Je m'appelle *%s* et je viens de finaliser ma pré-inscription sur votre site web pour la formation : *%s*.\n\n" +
                            "Pouvez-vous me confirmer la réception de ma demande et m'indiquer les prochaines étapes ?\n\n" +
                            "Merci d'avance ! ",
                    message.getNom(),
                    message.getFormationNom()
            );
        } else {
            texte = String.format(
                    "Bonjour l'Institut Beauty's Empire ! \n\n" +
                            "Je m'appelle *%s* et je vous contacte depuis votre site web concernant : *%s*.\n\n" +
                            "J'aimerais avoir plus d'informations s'il vous plaît.\n\n" +
                            "Merci ! ",
                    message.getNom(),
                    message.getSujet() != null ? message.getSujet() : "une demande de renseignements"
            );
        }

        return construireLien(adminNumber, texte);
    }

    @Override
    public String genererLienCatalogue() {
        String texte = "Bonjour l'Institut Beauty's Empire ! Je viens de m'inscrire à la newsletter sur votre site et j'aimerais recevoir le catalogue PDF s'il vous plaît. ";
        return construireLien(adminNumber, texte);
    }

    private String construireLien(String phone, String texte) {
        if (phone == null) return "";
        String clean = phone.replaceAll("[^0-9]", "");
        String num = clean.startsWith("237") ? clean : "237" + clean;
        return "https://wa.me/" + num + "?text=" + URLEncoder.encode(texte, StandardCharsets.UTF_8);
    }
}