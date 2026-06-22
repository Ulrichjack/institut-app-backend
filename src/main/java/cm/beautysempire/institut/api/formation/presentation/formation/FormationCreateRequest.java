package cm.beautysempire.institut.api.formation.presentation.formation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class FormationCreateRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotBlank(message = "La durée est obligatoire")
    private String duree;

    @NotNull(message = "Les frais d'inscription sont obligatoires")
    @Min(value = 0, message = "Les frais d'inscription doivent être positifs")
    private BigDecimal fraisInscription;

    @NotNull(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix doit être positif")
    private BigDecimal prix;

    @NotBlank(message = "La catégorie est obligatoire")
    private String categorie;

    // --- VALEURS PAR DÉFAUT POUR ÉVITER LES NULL ---
    @Builder.Default
    private Boolean certificatDelivre = true;

    private String nomCertificat;
    private String programme;
    private String objectifs;
    private String materielFourni;

    private LocalDate dateDemarrage;
    private LocalDate dateFinInscription;
    private String joursFormation;
    private String horaires;
    private String frequence;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Il faut au moins 1 place")
    private Integer nombrePlaces;

    @Builder.Default
    private Boolean socialProofActif = false;

    @Builder.Default
    private Integer nombreInscritsAffiche = 0;

    private String photoPrincipale;
    private List<String> photosGalerie;

    @Builder.Default
    private Boolean enPromotion = false;

    @Builder.Default
    private BigDecimal pourcentageReduction = BigDecimal.ZERO;

    private LocalDateTime dateDebutPromo;
    private LocalDateTime dateFinPromo;
}