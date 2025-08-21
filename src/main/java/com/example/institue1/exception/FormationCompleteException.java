package com.example.institue1.exception;

public class FormationCompleteException extends RuntimeException {
    public FormationCompleteException() {
        super("Cette formation est complète, aucune place disponible");
    }

    public FormationCompleteException(String nomFormation) {
        super("La formation '" + nomFormation + "' est complète");
    }
}


