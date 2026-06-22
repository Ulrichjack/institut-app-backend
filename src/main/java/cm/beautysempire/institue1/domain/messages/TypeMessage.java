package cm.beautysempire.institue1.domain.messages;


import lombok.Getter;

@Getter
public enum TypeMessage {

    //valeur
    CONTACT_GENERAL("Contact Général", "Demande d'information classique ou partenariat", false),
    PRE_INSCRIPTION("Pré-inscription", "Demande d'inscription à une formation de l'institut", true);

    private final String libelle;
    private final String description;
    private final boolean urgent;


    TypeMessage(String libelle, String description, Boolean urgent) {
            this.libelle = libelle;
            this.description = description;
            this.urgent = urgent;
    }

    public boolean necessiteFormation() {
            return this == PRE_INSCRIPTION;
    }

}
