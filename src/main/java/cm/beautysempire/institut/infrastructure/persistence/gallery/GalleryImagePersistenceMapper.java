package cm.beautysempire.institut.infrastructure.persistence.gallery;

import cm.beautysempire.institut.domain.gallery.GalleryImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GalleryImagePersistenceMapper {
    GalleryImage toDomain(GalleryImageJpaEntity entity);
    GalleryImageJpaEntity toEntity(GalleryImage domain);
}