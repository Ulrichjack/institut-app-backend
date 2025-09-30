package com.example.institue1.controller;

import com.example.institue1.dto.gallery.GalleryUploadDto;
import com.example.institue1.dto.gallery.GalleryImageDto; // Import DTO
import com.example.institue1.model.GalleryImage;
import com.example.institue1.service.GalleryImageService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gallery")
public class GalleryImageController {
    private final GalleryImageService galleryImageService;

    public GalleryImageController(GalleryImageService galleryImageService) {
        this.galleryImageService = galleryImageService;
    }

    // Liste toutes les images (Retourne DTO)
    @GetMapping
    public List<GalleryImageDto> listAllImages() {
        return galleryImageService.listAllImages();
    }

    // Récupère une image par son id (Garde l'entité, mais vous pouvez aussi la mapper à un DTO si vous voulez)
    @GetMapping("/{id}")
    public ResponseEntity<GalleryImage> getImage(@PathVariable Long id) {
        return galleryImageService.getImageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Ajoute une image
    @PostMapping
    public GalleryImage addImage(@RequestBody GalleryUploadDto dto) {
        return galleryImageService.addImage(dto, dto.getUrl());
    }

    // Modifie une image
    @PutMapping("/{id}")
    public ResponseEntity<GalleryImage> updateImage(@PathVariable Long id, @RequestBody GalleryUploadDto dto) {
        return galleryImageService.updateImage(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Supprime une image
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        boolean deleted = galleryImageService.deleteImage(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Liste paginée pour la galerie (Retourne Page<DTO>)
    @GetMapping("/paged")
    public Page<GalleryImageDto> listImagesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return galleryImageService.listImagesPaged(page, size);
    }

    // Pour la page d'accueil (Retourne List<DTO>)
    @GetMapping("/home-images")
    public List<GalleryImageDto> getHomeImages() {
        // Les 9 plus récentes. On utilise getContent() sur la Page<DTO>
        return galleryImageService.listImagesPaged(0, 9).getContent();
    }

    // Filtre non paginé (Retourne List<DTO>)
    @GetMapping("/by-formation-nom")
    public List<GalleryImageDto> getImagesByFormationNom(@RequestParam String nomFormation) {
        return galleryImageService.listImagesByFormationNom(nomFormation);
    }

    // Variante paginée (Retourne Page<DTO>)
    @GetMapping("/by-formation-nom-paged")
    public Page<GalleryImageDto> getImagesByFormationNomPaged(
            @RequestParam String nomFormation,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return galleryImageService.listImagesByFormationNomPaged(nomFormation, page, size);
    }
}