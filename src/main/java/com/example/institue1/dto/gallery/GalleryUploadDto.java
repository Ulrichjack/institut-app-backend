package com.example.institue1.dto.gallery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryUploadDto {
    @NotBlank(message = "Le titre de l'image est obligatoire")
    @Size(max = 100, message = "Le titre ne peut dépasser 100 caractères")
    private String titre;

    @Size(max = 500, message = "La description ne peut dépasser 500 caractères")
    private String description;

    @NotBlank(message = "La catégorie est obligatoire")
    private String categorie;

    private Long formationId;

    private Boolean public_ = true;
}