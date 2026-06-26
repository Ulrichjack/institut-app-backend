package cm.beautysempire.institut.infrastructure.persistence.gallery;

import cm.beautysempire.institut.domain.gallery.GalleryImage;
import cm.beautysempire.institut.domain.gallery.GalleryImageRepositoryPort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // 🔥 C'est cette annotation qui dit à Spring "Je suis le Bean que tu cherches !"
@RequiredArgsConstructor
public class GalleryImageRepositoryAdapter implements GalleryImageRepositoryPort {

    private final GalleryImageJpaRepository jpaRepository;
    private final GalleryImagePersistenceMapper mapper;

    @Override
    @Transactional
    public GalleryImage save(GalleryImage image) {
        GalleryImageJpaEntity entity = mapper.toEntity(image);
        GalleryImageJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<GalleryImage> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<GalleryImage> findAllPublic(Pageable pageable) {
        return jpaRepository.findByIsPublicTrue(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<GalleryImage> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }
}