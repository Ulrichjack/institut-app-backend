package cm.beautysempire.institut.api.gallery;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GalleryUploadRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    @NotBlank(message = "La catégorie est obligatoire")
    private String categorie;

    private Boolean isPublic = true;

    private Long formationId; // Optionnel
}