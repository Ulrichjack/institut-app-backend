package cm.beautysempire.institut.infrastructure.config;

import cm.beautysempire.institut.application.port.StoragePort;
import cm.beautysempire.institut.application.service.GalleryUseCase;
import cm.beautysempire.institut.domain.gallery.GalleryImageRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GalleryConfig {

    @Bean
    public GalleryUseCase galleryUseCase(
            GalleryImageRepositoryPort galleryRepositoryPort,
            StoragePort storagePort) {
        return new GalleryUseCase(galleryRepositoryPort, storagePort);
    }
}