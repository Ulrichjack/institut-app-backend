package cm.beautysempire.institut.domain.formation;

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

    private Boolean certificatDelivre;
    private String nomCertificat;

    private String programme;
    private String objectifs;
    private String materielFourni;

    @Builder.Default
    private Long version = 0L;

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
    private List<String> photosGalerie;

    private Boolean active;
    private Boolean enPromotion;
    private BigDecimal pourcentageReduction;
    private LocalDateTime dateDebutPromo;
    private LocalDateTime dateFinPromo;

    private LocalDateTime dateCreation;
    private LocalDateTime dateMiseAJour;
    private String creeParAdmin;
    private String modifiePar;
    private Integer nombreVues;
    private Integer nombreDemandesInfo;
    private Integer nombreInscriptions;

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

    public void initialiserCreation(String admin) {
        this.dateCreation = LocalDateTime.now();
        this.creeParAdmin = admin;
        this.modifiePar = admin;
        this.active = true;
        this.version = 0L;

        // Sécurité absolue contre les NULL pour la Base de données
        if (this.nombreInscritsReel == null) this.nombreInscritsReel = 0;
        if (this.nombreInscritsAffiche == null) this.nombreInscritsAffiche = 0;
        if (this.nombreInscriptions == null) this.nombreInscriptions = 0;
        if (this.nombreVues == null) this.nombreVues = 0;
        if (this.nombreDemandesInfo == null) this.nombreDemandesInfo = 0;
        if (this.certificatDelivre == null) this.certificatDelivre = true;
        if (this.socialProofActif == null) this.socialProofActif = false;
        if (this.enPromotion == null) this.enPromotion = false;
        if (this.pourcentageReduction == null) this.pourcentageReduction = BigDecimal.ZERO;

        if (this.slug == null || this.slug.trim().isEmpty()) {
            this.slug = genererSlug(this.nom);
        }
    }
    private String genererSlug(String nom) {
        if (nom == null) return "formation-" + System.currentTimeMillis();
        return nom.toLowerCase()
                .replaceAll("[éèêë]", "e")
                .replaceAll("[àâä]", "a")
                .replaceAll("[îï]", "i")
                .replaceAll("[ôö]", "o")
                .replaceAll("[ùûü]", "u")
                .replaceAll("[ç]", "c")
                .replaceAll("[^a-z0-9\\s-]", "") // Retire les caractères spéciaux
                .replaceAll("\\s+", "-") // Remplace les espaces par des tirets
                .replaceAll("-+", "-") // Évite les doubles tirets
                + "-" + (System.currentTimeMillis() % 10000); // Ajoute un ID unique à la fin
    }


    public void mettreAJourInfos(Formation nouvellesInfos, String admin){
        this.nom = nouvellesInfos.getNom();
        this.description = nouvellesInfos.getDescription();
        this.duree = nouvellesInfos.getDuree();
        this.fraisInscription = nouvellesInfos.getFraisInscription();
        this.prix = nouvellesInfos.getPrix();
        this.categorie = nouvellesInfos.getCategorie();
        this.certificatDelivre = nouvellesInfos.getCertificatDelivre();
        this.nomCertificat = nouvellesInfos.getNomCertificat();
        this.programme = nouvellesInfos.getProgramme();
        this.objectifs = nouvellesInfos.getObjectifs();
        this.materielFourni = nouvellesInfos.getMaterielFourni();
        this.dateDemarrage = nouvellesInfos.getDateDemarrage();
        this.dateFinInscription = nouvellesInfos.getDateFinInscription();
        this.joursFormation = nouvellesInfos.getJoursFormation();
        this.horaires = nouvellesInfos.getHoraires();
        this.frequence = nouvellesInfos.getFrequence();
        this.nombrePlaces = nouvellesInfos.getNombrePlaces();
        this.photoPrincipale = nouvellesInfos.getPhotoPrincipale();
        this.photosGalerie = nouvellesInfos.getPhotosGalerie();

        this.enPromotion = nouvellesInfos.getEnPromotion();
        this.pourcentageReduction = nouvellesInfos.getPourcentageReduction();
        this.dateDebutPromo = nouvellesInfos.getDateDebutPromo();
        this.dateFinPromo = nouvellesInfos.getDateFinPromo();

        this.dateMiseAJour = LocalDateTime.now();
        this.modifiePar = admin;
    }

    public void mettreAJourSocialProof(Integer nouveauNombreAffiche, Boolean actif, String admin){
        this.nombreInscritsAffiche = nouveauNombreAffiche;
        this.socialProofActif = actif;
        this.dateMiseAJour = LocalDateTime.now();
        this.modifiePar = admin;
    }

    public void desactiver(String admin) {
        this.active = false;
        this.dateMiseAJour = LocalDateTime.now();
        this.modifiePar = admin;
    }

    public void activer(String admin) {
        this.active = true;
        this.dateMiseAJour = LocalDateTime.now();
        this.modifiePar = admin;
    }

}