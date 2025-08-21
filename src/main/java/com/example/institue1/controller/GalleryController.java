package com.example.institue1.controller;

import com.example.institue1.dto.ApiResponse;
import com.example.institue1.dto.gallery.GalleryImageDto;
import com.example.institue1.dto.gallery.GalleryUploadDto;
import com.example.institue1.service.GalleryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Tag(name = "Galerie", description = "API de gestion des images")
@RestController
@RequestMapping("/api/gallery")
@Slf4j
public class GalleryController {

    private final GalleryService galleryService;

    public GalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @Operation(
            summary = "Liste des images publiques",
            description = "Récupère toutes les images publiques avec pagination"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<GalleryImageDto>>> listPublicImages(
            @RequestParam(required = false) String categorie,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<GalleryImageDto> images = galleryService.listPublicImages(categorie, pageable);

        ApiResponse<Page<GalleryImageDto>> response = ApiResponse.success(
                images,
                "Galerie d'images récupérée avec succès"
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Images récentes",
            description = "Récupère les images récentes pour affichage sur la page d'accueil"
    )
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<GalleryImageDto>>> getRecentImages() {
        List<GalleryImageDto> images = galleryService.getRecentImages();

        ApiResponse<List<GalleryImageDto>> response = ApiResponse.success(
                images,
                "Images récentes récupérées avec succès"
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Toutes les images (admin)",
            description = "Récupère toutes les images, publiques et privées (admin uniquement)"
    )
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<GalleryImageDto>>> listAllImages(
            @RequestParam(required = false) String categorie,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));

        Page<GalleryImageDto> images = galleryService.listAllImages(categorie, pageable);

        ApiResponse<Page<GalleryImageDto>> response = ApiResponse.success(
                images,
                "Liste complète d'images récupérée"
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Télécharger image",
            description = "Télécharge une nouvelle image dans la galerie (admin uniquement)"
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<GalleryImageDto>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute GalleryUploadDto dto,
            Principal principal) {

        String adminUser = (principal != null) ? principal.getName() : "system";
        log.info("Upload image par {}: {}", adminUser, dto.getTitre());

        GalleryImageDto uploadedImage = galleryService.uploadImage(file, dto, adminUser);

        ApiResponse<GalleryImageDto> response = ApiResponse.created(
                uploadedImage,
                "Image téléchargée avec succès"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Supprimer image",
            description = "Supprime une image de la galerie (admin uniquement)"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long id) {
        log.info("Suppression image ID: {}", id);

        boolean deleted = galleryService.deleteImage(id);

        ApiResponse<Void> response = ApiResponse.success(
                deleted ? "Image supprimée avec succès" : "Image supprimée de la base mais le fichier peut persister"
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Détail image",
            description = "Récupère les détails d'une image par son ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GalleryImageDto>> getImage(@PathVariable Long id) {
        GalleryImageDto image = galleryService.getImageById(id);

        ApiResponse<GalleryImageDto> response = ApiResponse.success(
                image,
                "Image récupérée avec succès"
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Images d'une formation",
            description = "Récupère les images associées à une formation spécifique"
    )
    @GetMapping("/formation/{formationId}")
    public ResponseEntity<ApiResponse<List<GalleryImageDto>>> getFormationImages(@PathVariable Long formationId) {
        List<GalleryImageDto> images = galleryService.getFormationImages(formationId);

        ApiResponse<List<GalleryImageDto>> response = ApiResponse.success(
                images,
                "Images de la formation récupérées avec succès"
        );

        return ResponseEntity.ok(response);
    }
}