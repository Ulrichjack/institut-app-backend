package com.example.institue1.enums;

import lombok.Getter;

@Getter
public enum TypeMessage {
    CONTACT_GENERAL("Contact Général", "Message ou question générale", false),
    PRE_INSCRIPTION("Pré-inscription", "Demande d'inscription à une formation", true);

    private final String libelle;
    private final String description;
    private final boolean urgent; // Pour priorisation

    TypeMessage(String libelle, String description, boolean urgent) {
        this.libelle = libelle;
        this.description = description;
        this.urgent = urgent;
    }

    //Détermine si ce type nécessite une formation obligatoire

    public boolean necessiteFormation() {
        return this == PRE_INSCRIPTION;
    }

    //Détermine si notification WhatsApp admin requise
    public boolean necessiteNotificationWhatsApp() {
        return this.urgent;
    }
}
