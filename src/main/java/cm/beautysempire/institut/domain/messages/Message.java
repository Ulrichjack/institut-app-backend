package cm.beautysempire.institut.domain.messages;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    //Base
    private Long id;
    private TypeMessage type;

    @Builder.Default
    private StatutMessage statut = StatutMessage.NON_LU;

    //Contact
    private String nom;
    private String email;
    private String telephone;
    private String ville;
    private String quartier;

    //Contenue
    private String sujet;
    private String message;
    private String disponibilites;

    //liens avec la formation
    private Long formationId;
    private String formationNom;

    //Tracking
    private LocalDateTime dateCreation;
    private LocalDateTime dateLecture;
    private LocalDateTime dateTraitement;
    private String traiteParAdmin;
    private String sourceVisite;
    private String adresseIp;
    private String userAgent;

    // === NOTIFICATIONS ===
    @Builder.Default
    private Boolean emailConfirmationEnvoye = false; // 🔥 AJOUTE @Builder.Default et = false

    @Builder.Default
    private Boolean whatsappNotificationEnvoye = false; // 🔥 AJOUTE @Builder.Default et = false

    private LocalDateTime dateEmailConfirmation;
    private LocalDateTime dateWhatsappNotification;


    //Logique metier
    public void marquerCommeLu(String admin) {
        validerEtChangerStatut(StatutMessage.LU, admin);
        this.dateLecture = LocalDateTime.now();
    }

    public void initialiserCreation() {
        this.dateCreation = LocalDateTime.now();
        this.statut = StatutMessage.NON_LU;
        this.emailConfirmationEnvoye = false;
        this.whatsappNotificationEnvoye = false;


    }


    public void marquerCommeTraite(String admin) {
        validerEtChangerStatut(StatutMessage.TRAITE, admin);
        this.dateTraitement = LocalDateTime.now();
    }

    public void marquerCommeArchive(String admin) {
        validerEtChangerStatut(StatutMessage.ARCHIVE, admin);
    }

    //Centraliser la verification de la transition de statut pour éviter la duplication de code
    private void validerEtChangerStatut(StatutMessage nouveauStatut, String admin) {
        if (!this.statut.peutEvoluerVers(nouveauStatut)) {
            throw new IllegalStateException(
                    String.format("Action impossible : Le message ne peut pas passer de %s à %s", this.statut, nouveauStatut)
            );
        }
        this.statut = nouveauStatut;
        this.traiteParAdmin = admin;
    }


}
