package com.example.institue1.dto.gallery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryImageDto {
    private Long id;
    private String titre;
    private String description;
    private String categorie;
    private String url;
    private String thumbnailUrl;
    private Integer width;
    private Integer height;
    private LocalDateTime dateCreation;
    private Long formationId;
    private String formationNom;
}