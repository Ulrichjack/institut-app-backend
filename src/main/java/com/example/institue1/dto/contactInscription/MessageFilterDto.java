package com.example.institue1.dto.contactInscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageFilterDto {
    private String statut; // NON_LU, LU, TRAITE, ARCHIVE
    private String type; // CONTACT_GENERAL, PRE_INSCRIPTION
    private String formationNom;
    private String email;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Boolean urgent;
    private String sourceVisite;
    private Integer scoreSpamMin;
}
