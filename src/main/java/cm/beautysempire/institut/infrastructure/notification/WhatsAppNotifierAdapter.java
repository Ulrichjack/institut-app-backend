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
        String texte = message.getType() == TypeMessage.PRE_INSCRIPTION
                ? String.format("Bonjour IBE 😊 Je confirme ma pré-inscription à la formation *%s*. Mon nom : %s.",
                message.getFormationNom(), message.getNom())
                : String.format("Bonjour IBE 😊 Suite à mon message sur le site (%s), mon nom : %s.",
                message.getSujet() != null ? message.getSujet() : "demande d'info", message.getNom());
        return construireLien(adminNumber, texte);
    }

    private String construireLien(String phone, String texte) {
        if (phone == null) return "";
        String clean = phone.replaceAll("[^0-9]", "");
        String num = clean.startsWith("237") ? clean : "237" + clean;
        return "https://wa.me/" + num + "?text=" + URLEncoder.encode(texte, StandardCharsets.UTF_8);
    }
}