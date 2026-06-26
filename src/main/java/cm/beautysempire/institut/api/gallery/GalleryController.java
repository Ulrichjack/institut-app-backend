// Fichier : src/main/java/cm/beautysempire/institut/api/gallery/GalleryController.java
package cm.beautysempire.institut.api.gallery;

import cm.beautysempire.institut.api.shared.ApiResponse;
import cm.beautysempire.institut.application.service.GalleryUseCase;
import cm.beautysempire.institut.domain.gallery.GalleryImage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryUseCase galleryUseCase;
    private final GalleryApiMapper mapper;

    // 1. UPLOAD (Admin) - Attention au "consumes" et au @ModelAttribute
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<GalleryImageResponse>> uploadImage(
            @RequestPart("file") MultipartFile file,
            @Valid @ModelAttribute GalleryUploadRequest request) {

        GalleryImage imageDetails = mapper.toDomain(request);
        GalleryImage savedImage = galleryUseCase.ajouterImage(file, imageDetails);
        GalleryImageResponse response = mapper.toResponse(savedImage);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Image uploadée avec succès"));
    }

    // 2. LISTER PUBLIQUES (Frontend public)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<GalleryImageResponse>>> listerImagesPubliques(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<GalleryImage> images = galleryUseCase.listerImagesPubliques(page, size);
        Page<GalleryImageResponse> response = images.map(mapper::toResponse);

        return ResponseEntity.ok(ApiResponse.success(response, "Galerie récupérée"));
    }

    // 3. SUPPRIMER (Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimerImage(@PathVariable Long id) {
        galleryUseCase.supprimerImage(id);
        return ResponseEntity.ok(ApiResponse.success("Image supprimée avec succès"));
    }
}