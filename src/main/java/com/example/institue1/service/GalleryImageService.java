package com.example.institue1.service;

import com.example.institue1.dto.gallery.GalleryUploadDto;
import com.example.institue1.dto.gallery.GalleryImageDto; // Import DTO
import com.example.institue1.model.GalleryImage;
import com.example.institue1.model.Formation;
import com.example.institue1.repository.GalleryImageRepository;
import com.example.institue1.repository.FormationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Nouvel import

@Service
public class GalleryImageService {
    private final GalleryImageRepository galleryImageRepository;
    private final FormationRepository formationRepository;

    public GalleryImageService(GalleryImageRepository galleryImageRepository, FormationRepository formationRepository) {
        this.galleryImageRepository = galleryImageRepository;
        this.formationRepository = formationRepository;
    }

    // --- MAPPER : Entité vers DTO ---
    private GalleryImageDto mapToDto(GalleryImage image) {
        return GalleryImageDto.builder()
                .id(image.getId())
                .titre(image.getTitre())
                .description(image.getDescription())
                .url(image.getUrl())
                .categorie(image.getCategorie())
                .isPublic(image.getIsPublic())
                // On récupère uniquement l'ID de la formation
                .formationId(image.getFormation() != null ? image.getFormation().getId() : null)
                .build();
    }
    // ---------------------------------

    // Ajouter une image (Utilise l'entité car c'est une opération d'écriture)
    public GalleryImage addImage(GalleryUploadDto dto, String url) {
        Formation formation = null;
        if (dto.getFormationId() != null) {
            formation = formationRepository.findById(dto.getFormationId()).orElse(null);
        }
        GalleryImage image = GalleryImage.builder()
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .categorie(dto.getCategorie())
                .url(url)
                .isPublic(dto.getIsPublic() == null ? true : dto.getIsPublic())
                .formation(formation)
                .build();
        return galleryImageRepository.save(image);
    }

    // Lister toutes les images (Retourne List<DTO>)
    public List<GalleryImageDto> listAllImages() {
        return galleryImageRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Récupérer une image par ID (Garde l'entité pour les opérations CRUD internes)
    public Optional<GalleryImage> getImageById(Long id) {
        return galleryImageRepository.findById(id);
    }

    // Modifier une image (Utilise l'entité)
    public Optional<GalleryImage> updateImage(Long id, GalleryUploadDto dto) {
        Optional<GalleryImage> opt = galleryImageRepository.findById(id);
        if (opt.isEmpty()) return Optional.empty();
        GalleryImage image = opt.get();
        image.setTitre(dto.getTitre());
        image.setDescription(dto.getDescription());
        image.setCategorie(dto.getCategorie());
        image.setIsPublic(dto.getIsPublic() == null ? true : dto.getIsPublic());
        if (dto.getFormationId() != null) {
            Formation formation = formationRepository.findById(dto.getFormationId()).orElse(null);
            image.setFormation(formation);
        } else {
            image.setFormation(null);
        }
        // *** AJOUTE CETTE LIGNE ***
        if (dto.getUrl() != null && !dto.getUrl().isBlank()) {
            image.setUrl(dto.getUrl());
        }
        // *** POUR CLOUDINARY filename si tu veux le garder aussi ***
        if (dto.getFilename() != null && !dto.getFilename().isBlank()) {
            image.setTitre(dto.getFilename());
        }
        return Optional.of(galleryImageRepository.save(image));
    }

    // Supprimer une image
    public boolean deleteImage(Long id) {
        if (!galleryImageRepository.existsById(id)) return false;
        galleryImageRepository.deleteById(id);
        return true;
    }

    // Liste paginée (Retourne Page<DTO>)
    public Page<GalleryImageDto> listImagesPaged(int page, int size) {
        // Tri personnalisé : formation IS NULL d'abord, puis par dateCreation DESC
        Sort sort = Sort.by(
                Sort.Order.asc("formation.id").nullsFirst(), // Images sans formation d'abord
                Sort.Order.desc("dateCreation") // Puis par date décroissante
        );

        Page<GalleryImage> entityPage = galleryImageRepository.findAll(
                PageRequest.of(page, size, sort)
        );

        return entityPage.map(this::mapToDto);
    }

    //pour filtre (Retourne List<DTO>)
    public List<GalleryImageDto> listImagesByFormationNom(String nomFormation) {
        return galleryImageRepository.findByFormationNomContaining(nomFormation).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Variante paginée (Retourne Page<DTO>)
    public Page<GalleryImageDto> listImagesByFormationNomPaged(String nomFormation, int page, int size) {
        Page<GalleryImage> entityPage = galleryImageRepository.findByFormationNomContainingPaged(nomFormation, PageRequest.of(page, size));
        // Mappe la Page d'entités en Page de DTOs
        return entityPage.map(this::mapToDto);
    }
}