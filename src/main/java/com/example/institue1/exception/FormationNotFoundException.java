package com.example.institue1.exception;

public class FormationNotFoundException extends RuntimeException {
    public FormationNotFoundException(String message) {
        super(message);
    }

    public FormationNotFoundException(Long id) {
        super("Formation avec l'ID " + id + " non trouvée");
    }

    public FormationNotFoundException(String field, String value) {
        super("Formation avec " + field + " '" + value + "' non trouvée");
    }
}
