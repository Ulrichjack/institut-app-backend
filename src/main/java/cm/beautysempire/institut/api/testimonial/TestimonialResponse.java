package cm.beautysempire.institut.api.testimonial;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TestimonialResponse {
    private Long id;
    private String nomEtudiant;
    private String formationSuivie;
    private String temoignage;
    private Integer note;
    private String photoUrl;
    private Boolean publie;
    private LocalDateTime dateCreation;
}