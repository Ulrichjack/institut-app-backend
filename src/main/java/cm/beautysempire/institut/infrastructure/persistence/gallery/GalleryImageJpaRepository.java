package cm.beautysempire.institut.infrastructure.persistence.gallery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalleryImageJpaRepository extends JpaRepository<GalleryImageJpaEntity, Long> {

    Page<GalleryImageJpaEntity> findByIsPublicTrue(Pageable pageable);
}
