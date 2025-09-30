package com.example.institue1.dto.gallery;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryImageDto {
    private Long id;
    private String titre;
    private String description;
    private String url;
    private String categorie;
    private Boolean isPublic;
    private Long formationId;
}