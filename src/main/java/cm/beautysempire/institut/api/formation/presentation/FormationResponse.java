package cm.beautysempire.institut.api.formation.presentation;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private BigDecimal prixAvecReduction; // Calculé par le domaine
    private String categorie;

    // --- NOUVEAUX CHAMPS AJOUTÉS POUR LA PAGE DÉTAIL ---
    private BigDecimal fraisInscription;
    private Boolean certificatDelivre;
    private String nomCertificat;
    private String programme;
    private String objectifs;
    private String materielFourni;
    private String horaires;
    private String frequence;
    private BigDecimal pourcentageReduction;

    private LocalDateTime dateFinPromo;
    // ---------------------------------------------------

    private LocalDate dateDemarrage;
    private Integer nombrePlaces;
    private Integer placesRestantesAffichees; // Calculé par le domaine

    private String photoPrincipale;
    private List<String> photosGalerie;

    private Boolean isPromoActive; // Calculé par le domaine
    private String slug;

    private Boolean active;
}