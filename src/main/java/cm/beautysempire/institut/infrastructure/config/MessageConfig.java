package cm.beautysempire.institut.infrastructure.config;

import cm.beautysempire.institut.application.port.WhatsAppNotifierPort;
import cm.beautysempire.institut.application.service.MessageUseCase;
import cm.beautysempire.institut.domain.formation.FormationRepositoryPort;
import cm.beautysempire.institut.domain.messages.MessageRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfig {

    @Bean
    public MessageUseCase messageUseCase(
            MessageRepositoryPort messageRepositoryPort,
            FormationRepositoryPort formationRepositoryPort,
            WhatsAppNotifierPort whatsAppNotifierPort) {
        return new MessageUseCase(messageRepositoryPort, formationRepositoryPort, whatsAppNotifierPort);
    }
}