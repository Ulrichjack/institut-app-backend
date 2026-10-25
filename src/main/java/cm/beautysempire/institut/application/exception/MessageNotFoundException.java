package cm.beautysempire.institut.application.exception;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(Long id) {
        super("Message introuvable : " + id);
    }
}
