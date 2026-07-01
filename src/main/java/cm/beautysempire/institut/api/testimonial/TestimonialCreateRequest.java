package cm.beautysempire.institut.api.testimonial;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TestimonialCreateRequest {
    @NotBlank private String nomEtudiant;
    @NotBlank private String formationSuivie;
    @NotBlank private String temoignage;
    @Min(1) @Max(5) private Integer note;
    private String photoUrl;
    private Boolean publie = false;
}