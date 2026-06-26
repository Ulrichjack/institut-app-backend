package cm.beautysempire.institut.infrastructure.persistence.gallery;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "gallery_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryImageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String url;

    @Column(name = "cloudinary_public_id", length = 200)
    private String cloudinaryPublicId;

    @Column(nullable = false)
    private String categorie;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(name = "formation_id")
    private Long formationId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;
}