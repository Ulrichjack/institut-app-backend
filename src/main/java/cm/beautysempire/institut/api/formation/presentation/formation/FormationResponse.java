package cm.beautysempire.institut.api.formation.presentation.formation;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormationResponse {
    private Long id;
    private String nom;
    private String description;
    private String duree;
    private BigDecimal prix;
    private BigDecimal prixAvecReduction; // Champ calculé par le domaine !
    private String categorie;

    private LocalDate dateDemarrage;

    private Integer nombrePlaces;
    private Integer placesRestantesAffichees; // Champ calculé par le domaine !

    private String photoPrincipale;
    private List<String> photosGalerie;

    private Boolean isPromoActive; // Champ calculé par le domaine !
    private String slug;
}