package cm.beautysempire.institut.infrastructure.persistence.formation;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "formations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version // Pour le verrou optimiste (Flyway V3)
    @Column(nullable = false)
    @Builder.Default
    private Long version = 0L;

    private String nom;
    private String description;
    private String duree;
    private BigDecimal fraisInscription;
    private BigDecimal prix;
    private String categorie;

    private Boolean certificatDelivre;
    private String nomCertificat;

    private String programme;
    private String objectifs;
    private String materielFourni;

    private LocalDate dateDemarrage;
    private LocalDate dateFinInscription;
    private String joursFormation;
    private String horaires;
    private String frequence;

    private Integer nombrePlaces;
    private Integer nombreInscritsReel;
    private Integer nombreInscritsAffiche;
    private Boolean socialProofActif;

    private String photoPrincipale;

    @ElementCollection
    @CollectionTable(name = "formation_photos", joinColumns = @JoinColumn(name = "formation_id"))
    @Column(name = "photo_url")
    private List<String> photosGalerie;

    private Boolean active;
    private Boolean enPromotion;
    private BigDecimal pourcentageReduction;
    private LocalDateTime dateDebutPromo;
    private LocalDateTime dateFinPromo;

    private LocalDateTime dateCreation;

    @Column(name = "date_mise_a_jour")
    private LocalDateTime dateMiseAJour;

    private String creeParAdmin;
    private String modifiePar;
    private Integer nombreVues;
    private Integer nombreDemandesInfo;
    private Integer nombreInscriptions;

    private String metaTitle;
    private String metaDescription;
    private String slug;
}