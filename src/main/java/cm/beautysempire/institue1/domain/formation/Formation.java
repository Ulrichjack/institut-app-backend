package cm.beautysempire.institue1.domain.formation;

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
public class Formation {

    private Long id;
    private String nom;
    private String description;
    private String duree;
    private BigDecimal fraisInscription;
    private BigDecimal prix;
    private String categorie;

    // Certificat
    private Boolean certificatDelivre;
    private String nomCertificat;

    // Pédagogie
    private String programme;
    private String objectifs;
    private String materielFourni;

    // Sessions (Les nouveaux champs !)
    private LocalDate dateDemarrage;
    private LocalDate dateFinInscription;
    private String joursFormation;
    private String horaires;
    private String frequence;

    // Places & Social Proof
    private Integer nombrePlaces;
    private Integer nombreInscritsReel;
    private Integer nombreInscritsAffiche;
    private Boolean socialProofActif;

    // Médias
    private String photoPrincipale;
    private List<String> photosGalerie;

    // Statut & Promo
    private Boolean active;
    private Boolean enPromotion;
    private BigDecimal pourcentageReduction;
    private LocalDateTime dateDebutPromo;
    private LocalDateTime dateFinPromo;

    // Métadonnées & Stats
    private LocalDateTime dateCreation;
    private LocalDateTime dateMiseAJour;
    private String creeParAdmin;
    private String modifiePar;
    private Integer nombreVues;
    private Integer nombreDemandesInfo;
    private Integer nombreInscriptions;

    // SEO
    private String metaTitle;
    private String metaDescription;
    private String slug;

    // ==========================================
    // LOGIQUE MÉTIER (BUSINESS LOGIC)
    // ==========================================

    public boolean peutSInscrire() {
        return this.active && getPlacesRestantesReelles() > 0;
    }

    public int getPlacesRestantesReelles() {
        if (nombrePlaces == null || nombreInscritsReel == null) return 0;
        return Math.max(0, nombrePlaces - nombreInscritsReel);
    }

    public int getPlacesRestantesAffichees() {
        if (nombrePlaces == null || nombreInscritsAffiche == null) return 0;
        return Math.max(0, nombrePlaces - getNombreInscritsAffichage());
    }

    public int getNombreInscritsAffichage() {
        return (socialProofActif != null && socialProofActif) ? nombreInscritsAffiche : nombreInscritsReel;
    }

    public void ajouterInscriptionReelle() {
        this.nombreInscritsReel++;
        this.nombreInscriptions++;
        if (this.socialProofActif == null || !this.socialProofActif) {
            this.nombreInscritsAffiche = this.nombreInscritsReel;
        }
    }

    public void incrementerVues() {
        this.nombreVues++;
    }

    public void incrementerDemandesInfo() {
        this.nombreDemandesInfo++;
    }

    public BigDecimal getPrixAvecReduction() {
        if (!isPromoActive() || pourcentageReduction == null || pourcentageReduction.equals(BigDecimal.ZERO)) {
            return prix;
        }
        BigDecimal reduction = prix.multiply(pourcentageReduction).divide(BigDecimal.valueOf(100));
        return prix.subtract(reduction);
    }

    public boolean isPromoActive() {
        if (enPromotion == null || !enPromotion) return false;
        LocalDateTime now = LocalDateTime.now();
        return (dateDebutPromo == null || now.isAfter(dateDebutPromo)) &&
                (dateFinPromo == null || now.isBefore(dateFinPromo));
    }
}