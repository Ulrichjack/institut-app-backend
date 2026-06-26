package cm.beautysempire.institut.application.exception;

public class FormationNotFoundException extends RuntimeException {
    public FormationNotFoundException(Long id) {
        super("La formation avec l'ID " + id + " est introuvable.");
    }

    public FormationNotFoundException(String slug) {
        super("La formation avec le slug " + slug + " est introuvable.");
    }

}