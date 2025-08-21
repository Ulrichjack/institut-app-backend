package com.example.institue1.mapper;


import com.example.institue1.dto.formation.*;
import com.example.institue1.model.Formation;
import com.example.institue1.utils.FormationUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FormationMapper {


    private final FormationUtils formationUtils;

    public FormationMapper(FormationUtils formationUtils){
        this.formationUtils =formationUtils;
    }

    //conversion de Formation vers FormationListDto
    public FormationListDto toListDto(Formation formation){
        return FormationListDto.builder()
                .id(formation.getId())
                .nom(formation.getNom())
                .description(formationUtils.getDescriptionCourte(formation.getDescription(), 150))
                .duree(formation.getDuree())
                .fraisInscription(formation.getFraisInscription())
                .prix(formation.getPrix())
                .prixAvecReduction(formationUtils.getPrixAvecReduction(formation))
                .categorie(formation.getCategorie())
                .photoPrincipale(formation.getPhotoPrincipale())
                .nombrePlaces(formation.getNombrePlaces())
                .nombreInscritsAffiche(formationUtils.getNombreInscritsAffichage(formation))
                .placesRestantes(formationUtils.getPlacesRestantesAffichees(formation))
                .tauxRemplissage(formationUtils.getTauxRemplissageAffiche(formation))
                .messageSocialProof(formationUtils.getMessageSocialProof(formation))
                .enPromotion(formation.getEnPromotion())
                .pourcentageReduction(formation.getPourcentageReduction())
                .promoActive(formationUtils.isPromoActive(formation))
                .certificatDelivre(formation.getCertificatDelivre())
                .slug(formation.getSlug())
                .active(formation.getActive())
                .nombreVues(formation.getNombreVues())
                .build();
    }

    //* Conversion vers FormationDetailDto
    public FormationDetailDto toDetailDto(Formation formation) {
        return FormationDetailDto.builder()
                .id(formation.getId())
                .nom(formation.getNom())
                .description(formation.getDescription())
                .duree(formation.getDuree())
                .fraisInscription(formation.getFraisInscription())
                .prix(formation.getPrix())
                .prixAvecReduction(formationUtils.getPrixAvecReduction(formation))
                .categorie(formation.getCategorie())
                .certificatDelivre(formation.getCertificatDelivre())
                .nomCertificat(formation.getNomCertificat())
                .programme(formation.getProgramme())
                .objectifs(formation.getObjectifs())
                .materielFourni(formation.getMaterielFourni())
                .horaires(formation.getHoraires())
                .frequence(formation.getFrequence())
                .nombrePlaces(formation.getNombrePlaces())
                .nombreInscritsAffiche(formationUtils.getNombreInscritsAffichage(formation))
                .placesRestantes(formationUtils.getPlacesRestantesAffichees(formation))
                .tauxRemplissage(formationUtils.getTauxRemplissageAffiche(formation))
                .messageSocialProof(formationUtils.getMessageSocialProof(formation))
                .formationComplete(formationUtils.isFormationComplete(formation))
                .photoPrincipale(formation.getPhotoPrincipale())
                .photosGalerie(formation.getPhotosGalerie())
                .videoPresentation(formation.getVideoPresentation())
                .enPromotion(formation.getEnPromotion())
                .pourcentageReduction(formation.getPourcentageReduction())
                .promoActive(formationUtils.isPromoActive(formation))
                .dateDebutPromo(formation.getDateDebutPromo())
                .dateFinPromo(formation.getDateFinPromo())
                .metaTitle(formation.getMetaTitle())
                .metaDescription(formation.getMetaDescription())
                .slug(formation.getSlug())
                .nombreVues(formation.getNombreVues())
                .nombreDemandesInfo(formation.getNombreDemandesInfo())
                .nombreInscriptions(formation.getNombreInscriptions())
                .dateCreation(formation.getDateCreation())
                .dateMiseAJour(formation.getDateMiseAJour())
                .build();
    }

    //Conversion vers FormationAdminDto
    public FormationAdminDto toAdminDto(Formation formation) {
        return FormationAdminDto.builder()
                .id(formation.getId())
                .nom(formation.getNom())
                .description(formation.getDescription())
                .duree(formation.getDuree())
                .fraisInscription(formation.getFraisInscription())
                .prix(formation.getPrix())
                .prixAvecReduction(formationUtils.getPrixAvecReduction(formation))
                .categorie(formation.getCategorie())
                .certificatDelivre(formation.getCertificatDelivre())
                .nomCertificat(formation.getNomCertificat())
                .programme(formation.getProgramme())
                .objectifs(formation.getObjectifs())
                .materielFourni(formation.getMaterielFourni())
                .horaires(formation.getHoraires())
                .frequence(formation.getFrequence())
                .nombrePlaces(formation.getNombrePlaces())
                .nombreInscritsReel(formation.getNombreInscritsReel()) // Données réelles pour admin
                .nombreInscritsAffiche(formation.getNombreInscritsAffiche())
                .socialProofActif(formation.getSocialProofActif())
                .placesRestantesReelles(formationUtils.getPlacesRestantesReelles(formation))
                .placesRestantesAffichees(formationUtils.getPlacesRestantesAffichees(formation))
                .tauxRemplissageReel(formationUtils.getTauxRemplissageReel(formation))
                .tauxRemplissageAffiche(formationUtils.getTauxRemplissageAffiche(formation))
                .messageSocialProof(formationUtils.getMessageSocialProof(formation))
                .formationComplete(formationUtils.isFormationComplete(formation))
                .photoPrincipale(formation.getPhotoPrincipale())
                .photosGalerie(formation.getPhotosGalerie())
                .videoPresentation(formation.getVideoPresentation())
                .enPromotion(formation.getEnPromotion())
                .pourcentageReduction(formation.getPourcentageReduction())
                .promoActive(formationUtils.isPromoActive(formation))
                .dateDebutPromo(formation.getDateDebutPromo())
                .dateFinPromo(formation.getDateFinPromo())
                .active(formation.getActive())
                .metaTitle(formation.getMetaTitle())
                .metaDescription(formation.getMetaDescription())
                .slug(formation.getSlug())
                .nombreVues(formation.getNombreVues())
                .nombreDemandesInfo(formation.getNombreDemandesInfo())
                .nombreInscriptions(formation.getNombreInscriptions())
                .dateCreation(formation.getDateCreation())
                .dateMiseAJour(formation.getDateMiseAJour())
                .creeParAdmin(formation.getCreeParAdmin())
                .modifiePar(formation.getModifiePar())
                .build();
    }

    /**
     * Conversion vers SocialProofDto
     */
    public SocialProofDto toSocialProofDto(Formation formation) {
        return SocialProofDto.builder()
                .formationId(formation.getId())
                .nombreInscritsAffiche(formation.getNombreInscritsAffiche())
                .socialProofActif(formation.getSocialProofActif())
                .nombreInscritsReel(formation.getNombreInscritsReel())
                .placesRestantesReelles(formationUtils.getPlacesRestantesReelles(formation))
                .placesRestantesAffichees(formationUtils.getPlacesRestantesAffichees(formation))
                .messageSocialProof(formationUtils.getMessageSocialProof(formation))
                .tauxRemplissageReel(formationUtils.getTauxRemplissageReel(formation))
                .tauxRemplissageAffiche(formationUtils.getTauxRemplissageAffiche(formation))
                .build();
    }

    // === CONVERSION DEPUIS DTOS ===

    /**
     * Conversion depuis FormationCreateDto vers Formation
     */
    public Formation fromCreateDto(FormationCreateDto dto) {
        Formation formation = Formation.builder()
                .nom(dto.getNom())
                .description(dto.getDescription())
                .duree(dto.getDuree())
                .fraisInscription(dto.getFraisInscription())
                .prix(dto.getPrix())
                .categorie(dto.getCategorie())
                .certificatDelivre(dto.getCertificatDelivre())
                .nomCertificat(dto.getNomCertificat())
                .programme(dto.getProgramme())
                .objectifs(dto.getObjectifs())
                .materielFourni(dto.getMaterielFourni())
                .horaires(dto.getHoraires())
                .frequence(dto.getFrequence())
                .nombrePlaces(dto.getNombrePlaces())
                .nombreInscritsReel(0) // Toujours 0 au début
                .nombreInscritsAffiche(dto.getNombreInscritsAffiche())
                .socialProofActif(dto.getSocialProofActif())
                .photoPrincipale(dto.getPhotoPrincipale())
                .photosGalerie(dto.getPhotosGalerie())
                .videoPresentation(dto.getVideoPresentation())
                .enPromotion(dto.getEnPromotion())
                .pourcentageReduction(dto.getPourcentageReduction())
                .dateDebutPromo(dto.getDateDebutPromo())
                .dateFinPromo(dto.getDateFinPromo())
                .metaTitle(dto.getMetaTitle())
                .metaDescription(dto.getMetaDescription())
                .active(dto.getActive())
                .nombreVues(0)
                .nombreDemandesInfo(0)
                .nombreInscriptions(0)
                .build();

        // Générer slug si pas fourni
        if (dto.getSlug() == null || dto.getSlug().isEmpty()) {
            formation.setSlug(formationUtils.generateSlug(dto.getNom()));
        } else {
            formation.setSlug(dto.getSlug());
        }

        return formation;
    }

    /**
     * Met à jour une Formation existante avec FormationUpdateDto
     */
    public void updateFromDto(Formation formation, FormationUpdateDto dto, String modifiePar) {
        if (dto.getNom() != null) formation.setNom(dto.getNom());
        if (dto.getDescription() != null) formation.setDescription(dto.getDescription());
        if (dto.getDuree() != null) formation.setDuree(dto.getDuree());
        if(dto.getFraisInscription() != null) formation.setFraisInscription(dto.getFraisInscription());
        if (dto.getPrix() != null) formation.setPrix(dto.getPrix());
        if (dto.getCategorie() != null) formation.setCategorie(dto.getCategorie());
        if (dto.getCertificatDelivre() != null) formation.setCertificatDelivre(dto.getCertificatDelivre());
        if (dto.getNomCertificat() != null) formation.setNomCertificat(dto.getNomCertificat());
        if (dto.getProgramme() != null) formation.setProgramme(dto.getProgramme());
        if (dto.getObjectifs() != null) formation.setObjectifs(dto.getObjectifs());
        if (dto.getMaterielFourni() != null) formation.setMaterielFourni(dto.getMaterielFourni());
        if (dto.getHoraires() != null) formation.setHoraires(dto.getHoraires());
        if (dto.getFrequence() != null) formation.setFrequence(dto.getFrequence());
        if (dto.getNombrePlaces() != null) formation.setNombrePlaces(dto.getNombrePlaces());
        if (dto.getNombreInscritsAffiche() != null) formation.setNombreInscritsAffiche(dto.getNombreInscritsAffiche());
        if (dto.getSocialProofActif() != null) formation.setSocialProofActif(dto.getSocialProofActif());
        if (dto.getPhotoPrincipale() != null) formation.setPhotoPrincipale(dto.getPhotoPrincipale());
        if (dto.getPhotosGalerie() != null) formation.setPhotosGalerie(dto.getPhotosGalerie());
        if (dto.getVideoPresentation() != null) formation.setVideoPresentation(dto.getVideoPresentation());
        if (dto.getEnPromotion() != null) formation.setEnPromotion(dto.getEnPromotion());
        if (dto.getPourcentageReduction() != null) formation.setPourcentageReduction(dto.getPourcentageReduction());
        if (dto.getDateDebutPromo() != null) formation.setDateDebutPromo(dto.getDateDebutPromo());
        if (dto.getDateFinPromo() != null) formation.setDateFinPromo(dto.getDateFinPromo());
        if (dto.getMetaTitle() != null) formation.setMetaTitle(dto.getMetaTitle());
        if (dto.getMetaDescription() != null) formation.setMetaDescription(dto.getMetaDescription());
        if (dto.getSlug() != null) formation.setSlug(dto.getSlug());
        if (dto.getActive() != null) formation.setActive(dto.getActive());

        // Mettre à jour les métadonnées
        formation.setModifiePar(modifiePar);
    }

    // === MÉTHODES UTILITAIRES ===

    /**
     * Conversion liste vers FormationListDto
     */
    public List<FormationListDto> toListDto(List<Formation> formations) {
        return formations.stream()
                .map(this::toListDto)
                .collect(Collectors.toList());
    }

    /**
     * Conversion liste vers FormationAdminDto
     */
    public List<FormationAdminDto> toAdminDto(List<Formation> formations) {
        return formations.stream()
                .map(this::toAdminDto)
                .collect(Collectors.toList());
    }


    public FormationResumeDto toResumeDto(Formation formation){
           return FormationResumeDto.builder()
                .id(formation.getId())
                .nom(formation.getNom())
                .prix(formation.getPrix())
                .nombreInscritsAffiche(formation.getNombreInscritsAffiche())
                .build();
    }

    public FormationSimpleDto toSimpleDto(Formation formation) {
        if (formation == null) return null;

        return FormationSimpleDto.builder()
                .id(formation.getId())
                .nom(formation.getNom())
                .categorie(formation.getCategorie())
                .prix(formation.getPrix())
                .duree(formation.getDuree())
                .active(formation.getActive())
                .slug(formation.getSlug())
                .build();
    }

    // Liste formations pour dropdown dans Messages
    public List<FormationSimpleDto> toSimpleDto(List<Formation> formations) {
        return formations.stream()
                .map(this::toSimpleDto)
                .toList();
    }

}
