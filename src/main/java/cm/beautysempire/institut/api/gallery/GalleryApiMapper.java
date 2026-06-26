package cm.beautysempire.institut.api.gallery;

import cm.beautysempire.institut.domain.gallery.GalleryImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GalleryApiMapper {

    GalleryImage toDomain(GalleryUploadRequest request);

    GalleryImageResponse toResponse(GalleryImage domain);
}