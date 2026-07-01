package cm.beautysempire.institut.domain.testimonial;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Testimonial {
    private Long id;
    private String nomEtudiant;
    private String formationSuivie;
    private String temoignage;
    private Integer note; // 1 à 5
    private String photoUrl;
    private Boolean publie;
    private LocalDateTime dateCreation;

    public void initialiserCreation() {
        this.dateCreation = LocalDateTime.now();
        if (this.publie == null) this.publie = false; // Par défaut non publié (admin doit valider)
    }

    public void togglePublication() {
        this.publie = !this.publie;
    }
}