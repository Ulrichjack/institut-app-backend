package com.example.institue1.dto.contactInscription;

import com.example.institue1.dto.formation.FormationSimpleDto;
import com.example.institue1.model.Message;
import com.example.institue1.utils.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDetailDto {
    private Long id;
    private String type;
    private String statut;

    // Contact
    private String nom;
    private String email;
    private String telephone;
    private String ville;

    // Contenu
    private String sujet;
    private String message;
    private String disponibilites;

    // Formation
    private Long formationId;
    private String formationNom;
    private FormationSimpleDto formation; // Infos formation si existe

    // Tracking
    private LocalDateTime dateCreation;
    private LocalDateTime dateLecture;
    private LocalDateTime dateTraitement;
    private String traiteParAdmin;
    private String sourceVisite;
    private String adresseIP;
    private String navigateur; // Info extraite du userAgent

    // Notifications
    private boolean emailConfirmationEnvoye;
    private boolean whatsappNotificationEnvoye;
    private LocalDateTime dateEmailConfirmation;
    private LocalDateTime dateWhatsappNotification;

    // Analytics
    private boolean urgent;
    private long ageEnHeures;
    private int priorite;
    private int scoreSpam;


}
