package com.example.institue1.dto.gallery;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryUploadDto {
    private String titre;
    private String description;
    private String categorie;
    private Long formationId;
    private Boolean isPublic;
    private String url;
    private String filename; // Ajouté !

}