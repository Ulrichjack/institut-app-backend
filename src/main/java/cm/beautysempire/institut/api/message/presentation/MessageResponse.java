package cm.beautysempire.institut.api.message.presentation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponse {
    private Long id;
    private String nom;
    private String telephone;
    private String formationNom;

    // 🔥 C'est ce lien que le frontend va utiliser pour le bouton "Confirmer sur WhatsApp"
    private String whatsappConfirmationLink;
}