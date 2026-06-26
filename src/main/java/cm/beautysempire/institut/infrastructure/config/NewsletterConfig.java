package cm.beautysempire.institut.infrastructure.config;

import cm.beautysempire.institut.application.service.NewsletterUseCase;
import cm.beautysempire.institut.domain.newsletter.NewsletterRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NewsletterConfig {

    @Bean
    public NewsletterUseCase newsletterUseCase(
            NewsletterRepositoryPort newsletterRepositoryPort
    ){
        return new NewsletterUseCase(newsletterRepositoryPort);
    }
}
