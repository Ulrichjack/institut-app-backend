package com.example.institue1.dto.contactInscription;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatsDto {
    // Compteurs globaux
    private long totalMessages;
    private long messagesNonLus;
    private long messagesTraites;
    private long messagesArchives;

    // Par type
    private long contactsGeneraux;
    private long preInscriptions;

    // Urgences
    private long messagesUrgents;
    private long messagesAnciens; // > 24h

    // Évolution
    private long messagesDuJour;
    private long messagesDeLaSemaine;
    private long messagesDuMois;

    // Top formations
    private List<FormationMessageStatsDto> topFormations;

    // Performances
    private double tauxReponse24h;
    private double tempsReponseMovenHeures;

    @Data
    @AllArgsConstructor
    public static class FormationMessageStatsDto {
        private String formationNom;
        private long nombreMessages;
        private long preInscriptions;
        private long contacts;
    }
}
