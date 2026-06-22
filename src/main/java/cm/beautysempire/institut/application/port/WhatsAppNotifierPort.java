package cm.beautysempire.institut.application.port;


import cm.beautysempire.institut.domain.messages.Message;

public interface WhatsAppNotifierPort {

    String genererLienAdmin(Message message);

    String genererLienConfirmationClient(Message message);

}
