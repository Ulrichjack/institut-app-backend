package com.example.institue1.service;

import com.example.institue1.dto.gallery.GalleryImageDto;
import com.example.institue1.dto.gallery.GalleryUploadDto;
import com.example.institue1.model.Formation;
import com.example.institue1.model.GalleryImage;
import com.example.institue1.repository.FormationRepository;
import com.example.institue1.repository.GalleryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final FormationRepository formationRepository;
    private final ImageService imageService;

    public GalleryService(GalleryRepository galleryRepository, FormationRepository formationRepository, ImageService imageService) {
        this.galleryRepository = galleryRepository;
        this.formationRepository = formationRepository;
        this.imageService = imageService;
    }

    /**
     * Télécharge une image dans la galerie
     */
    public GalleryImageDto uploadImage(MultipartFile file, GalleryUploadDto dto, String adminUser) {
        log.info("Upload image galerie par {}: {}", adminUser, dto.getTitre());

        try {
            // Sauvegarder l'image et obtenir infos
            Map<String, Object> imageInfo = imageService.saveImage(file);

            // Préparer entité GalleryImage
            GalleryImage image = GalleryImage.builder()
                    .titre(dto.getTitre())
                    .description(dto.getDescription())
                    .categorie(dto.getCategorie())
                    .filename((String) imageInfo.get("filename"))
                    .url((String) imageInfo.get("url"))
                    .thumbnailUrl((String) imageInfo.get("thumbnailUrl"))
                    .width((Integer) imageInfo.get("width"))
                    .height((Integer) imageInfo.get("height"))
                    .fileSize((Long) imageInfo.get("size"))
                    .public_(dto.getPublic_())
                    .uploadedBy(adminUser)
                    .build();

            // Associer à une formation si demandé
            if (dto.getFormationId() != null) {
                Formation formation = formationRepository.findById(dto.getFormationId())
                        .orElse(null);
                if (formation != null) {
                    image.setFormation(formation);
                }
            }

            // Sauvegarder en base
            GalleryImage savedImage = galleryRepository.save(image);

            // Retourner DTO
            return mapToDto(savedImage);

        } catch (IOException e) {
            log.error("Erreur lors de l'upload de l'image: {}", e.getMessage());
            throw new RuntimeException("Échec de l'upload d'image: " + e.getMessage());
        }
    }

    /**
     * Liste les images publiques
     */
    @Transactional(readOnly = true)
    public Page<GalleryImageDto> listPublicImages(String categorie, Pageable pageable) {
        log.debug("Liste images publiques - catégorie: {}", categorie);

        Page<GalleryImage> imagesPage;
        if (categorie != null && !categorie.isEmpty()) {
            imagesPage = galleryRepository.findByCategorieAndPublic_(categorie, true, pageable);
        } else {
            imagesPage = galleryRepository.findByPublic_(true, pageable);
        }

        List<GalleryImageDto> imageDtos = imagesPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(imageDtos, pageable, imagesPage.getTotalElements());
    }

    /**
     * Liste toutes les images (admin)
     */
    @Transactional(readOnly = true)
    public Page<GalleryImageDto> listAllImages(String categorie, Pageable pageable) {
        log.debug("Liste toutes images (admin) - catégorie: {}", categorie);

        Page<GalleryImage> imagesPage;
        if (categorie != null && !categorie.isEmpty()) {
            imagesPage = galleryRepository.findByCategorie(categorie, pageable);
        } else {
            imagesPage = galleryRepository.findAll(pageable);
        }

        List<GalleryImageDto> imageDtos = imagesPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(imageDtos, pageable, imagesPage.getTotalElements());
    }

    /**
     * Récupère une image par son ID
     */
    @Transactional(readOnly = true)
    public GalleryImageDto getImageById(Long id) {
        log.debug("Récupération image ID: {}", id);

        GalleryImage image = galleryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image non trouvée ID: " + id));

        return mapToDto(image);
    }

    /**
     * Supprime une image
     */
    @Transactional
    public boolean deleteImage(Long id) {
        log.info("Suppression image ID: {}", id);

        GalleryImage image = galleryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image non trouvée ID: " + id));

        // Supprimer fichier physique
        boolean fileDeleted = imageService.deleteImage(image.getFilename());

        // Supprimer entrée en base
        galleryRepository.delete(image);

        return fileDeleted;
    }

    /**
     * Récupère les images d'une formation
     */
    @Transactional(readOnly = true)
    public List<GalleryImageDto> getFormationImages(Long formationId) {
        log.debug("Récupération images formation ID: {}", formationId);

        List<GalleryImage> images = galleryRepository.findByFormationIdAndPublic_(formationId, true);

        return images.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les images récentes pour la page d'accueil
     */
    @Transactional(readOnly = true)
    public List<GalleryImageDto> getRecentImages() {
        List<GalleryImage> images = galleryRepository.findTop6ByPublic_OrderByDateCreationDesc(true);

        return images.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une entité en DTO
     */
    private GalleryImageDto mapToDto(GalleryImage image) {
        GalleryImageDto dto = new GalleryImageDto();
        dto.setId(image.getId());
        dto.setTitre(image.getTitre());
        dto.setDescription(image.getDescription());
        dto.setCategorie(image.getCategorie());
        dto.setUrl(image.getUrl());
        dto.setThumbnailUrl(image.getThumbnailUrl());
        dto.setWidth(image.getWidth());
        dto.setHeight(image.getHeight());
        dto.setDateCreation(image.getDateCreation());

        if (image.getFormation() != null) {
            dto.setFormationId(image.getFormation().getId());
            dto.setFormationNom(image.getFormation().getNom());
        }

        return dto;
    }
}