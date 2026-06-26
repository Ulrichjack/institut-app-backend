package cm.beautysempire.institut.domain.gallery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GalleryImageRepositoryPort {
    GalleryImage save(GalleryImage image);
    Optional<GalleryImage> findById(Long id);
    void deleteById(Long id);
    Page<GalleryImage> findAllPublic(Pageable pageable);
    Page<GalleryImage> findAll(Pageable pageable); // Pour l'admin
}