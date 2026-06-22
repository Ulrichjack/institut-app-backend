package cm.beautysempire.institut.infrastructure.config;

import cm.beautysempire.institut.application.service.FormationUseCase;
import cm.beautysempire.institut.domain.formation.FormationRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FormationConfig {

    @Bean
    public FormationUseCase formationUseCase(FormationRepositoryPort formationRepositoryPort) {
        return new FormationUseCase(formationRepositoryPort);
    }
}
