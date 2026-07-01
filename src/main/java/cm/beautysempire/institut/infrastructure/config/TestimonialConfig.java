// infrastructure/config/TestimonialConfig.java
package cm.beautysempire.institut.infrastructure.config;

import cm.beautysempire.institut.application.service.TestimonialUseCase;
import cm.beautysempire.institut.domain.testimonial.TestimonialRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestimonialConfig {
    @Bean
    public TestimonialUseCase testimonialUseCase(TestimonialRepositoryPort port) {
        return new TestimonialUseCase(port);
    }
}