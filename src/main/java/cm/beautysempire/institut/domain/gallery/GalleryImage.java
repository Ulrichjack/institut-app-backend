package cm.beautysempire.institut.domain.gallery;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalleryImage {
    private Long id;
    private String titre;
    private String description;
    private String url; // L'URL publique de l'image
    private String cloudinaryPublicId; // L'ID secret pour pouvoir la supprimer plus tard
    private String categorie; // Ex: "ONGLES", "COIFFURE", "MAQUILLAGE"
    private Boolean isPublic;
    private Long formationId; // Optionnel (si l'image est liée à une formation)
    private LocalDateTime dateCreation;

    public void initialiserCreation() {
        this.dateCreation = LocalDateTime.now();
        if (this.isPublic == null) {
            this.isPublic = true;
        }
    }
}