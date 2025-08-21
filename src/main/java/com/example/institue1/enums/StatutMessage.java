package com.example.institue1.enums;

import lombok.Getter;

@Getter
public enum StatutMessage {
    NON_LU("Non lu", "Message non encore consulté", "#ef4444"), // Rouge
    LU("Lu", "Message consulté mais pas traité", "#f59e0b"), // Orange
    TRAITE("Traité", "Message traité, réponse envoyée", "#10b981"), // Vert
    ARCHIVE("Archivé", "Message archivé", "#6b7280"); // Gris

    private final String libelle;
    private final String description;
    private final String couleurHex; // Pour UI admin

    StatutMessage(String libelle, String description, String couleurHex) {
        this.libelle = libelle;
        this.description = description;
        this.couleurHex = couleurHex;
    }

    //Détermine si le message nécessite une action
    public boolean necessiteAction() {
        return this == NON_LU || this == LU;
    }

    //Détermine si le statut peut être changé vers ce nouveau statut
    public boolean peutEvoluerVers(StatutMessage nouveauStatut) {
        return switch (this) {
            case NON_LU -> nouveauStatut != NON_LU;
            case LU -> nouveauStatut != NON_LU;
            case TRAITE -> nouveauStatut == ARCHIVE;
            case ARCHIVE -> false; // Pas de retour arrière
        };
    }
}