package cm.beautysempire.institue1.domain.messages;


import lombok.Getter;

@Getter
public enum StatutMessage {

    NON_LU("Message non lu"),
    LU("Message lu"),
    TRAITE("Message traité"),
    ARCHIVE("Message archivé");

    private final String libelle;

    StatutMessage(String libelle){
        this.libelle = libelle;
    }

    public boolean necessiteAction(){
        return this == NON_LU || this == LU;
    }

    boolean peutEvoluerVers(StatutMessage nouveauStatut){
        if (nouveauStatut == null) return false;

        return switch (this) {
            case NON_LU -> true;
            case LU -> nouveauStatut != NON_LU;
            case TRAITE -> nouveauStatut == ARCHIVE;
            case ARCHIVE -> false;
        };
    }

}
