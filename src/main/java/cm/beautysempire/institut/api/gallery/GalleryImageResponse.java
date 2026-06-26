package cm.beautysempire.institut.api.gallery;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GalleryImageResponse {
    private Long id;
    private String titre;
    private String description;
    private String url; // L'URL publique Cloudinary
    private String categorie;
    private Boolean isPublic;
    private Long formationId;
    private LocalDateTime dateCreation;
}