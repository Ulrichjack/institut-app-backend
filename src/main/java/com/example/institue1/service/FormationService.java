package com.example.institue1.service;


import com.example.institue1.dto.formation.*;
import com.example.institue1.exception.FormationNotFoundException;
import com.example.institue1.mapper.FormationMapper;
import com.example.institue1.model.Formation;
import com.example.institue1.repository.FormationRepository;
import com.example.institue1.repository.FormationSpecifications;
import com.example.institue1.utils.FormationUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class FormationService {
    private  final FormationRepository formationRepository;
    private final FormationMapper formationMapper;
    private final FormationUtils formationUtils;

    public FormationService(FormationRepository formationRepository, FormationMapper formationMapper, FormationUtils formationUtils) {
        this.formationRepository = formationRepository;
        this.formationMapper = formationMapper;
        this.formationUtils = formationUtils;
    }

    public FormationDetailDto createFormation(FormationCreateDto createDto, String adminCreateur ){
        log.info("Création d'une nouvelle formation : {}", createDto.getNom());

        Formation formation = formationMapper.fromCreateDto(createDto);

        formation.setCreeParAdmin(adminCreateur);
        formation.setModifiePar(adminCreateur);

        Formation formationSauvee = formationRepository.save(formation);
        log.info("Formation créée avec succès - ID: {}, Slug: {}",
                formationSauvee.getId(), formationSauvee.getSlug());

        return formationMapper.toDetailDto(formationSauvee);
    }

    @Transactional(readOnly = false)
    public Optional<FormationDetailDto> getFormationById(Long id, boolean incrementerVues) {
        log.debug("Recherche formation par ID : {}", id);

        return formationRepository.findById(id)
                .map(formation -> {
                    // Incrémenter les vues si demandé
                    if (incrementerVues) {
                        formationUtils.incrementerVues(formation);
                        formationRepository.save(formation);
                    }

                    return formationMapper.toDetailDto(formation);
                });
    }

    @Transactional(readOnly = false)
    public Optional<FormationDetailDto> getFormationBySlug(String slug, boolean incrementerVues) {
        log.debug("Recherche formation par slug : {}", slug);

        return formationRepository.findBySlugAndActiveTrue(slug)
                .map(formation -> {
                    if (incrementerVues) {
                        formationUtils.incrementerVues(formation);
                        formationRepository.save(formation);
                    }

                    return formationMapper.toDetailDto(formation);
                });
    }

    public Optional<FormationDetailDto> updateFormation(Long id, FormationUpdateDto updateDto, String adminModificateur) {
        log.info("Mise à jour formation ID : {}", id);

        return formationRepository.findById(id)
                .map(formation -> {
                    // Utiliser ton mapper pour la mise à jour
                    formationMapper.updateFromDto(formation, updateDto, adminModificateur);

                    // Sauvegarder
                    Formation formationMiseAJour = formationRepository.save(formation);

                    log.info("Formation mise à jour avec succès : {}", id);
                    return formationMapper.toDetailDto(formationMiseAJour);
                });
    }

    public void deleteFormation(Long id) {
        log.info("Suppression formation ID : {}", id);

        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new FormationNotFoundException("Formation non trouvée avec l'ID : " + id));
        log.info("Formation désactivée : {}", id);
        formation.setActive(false);
        formationRepository.save(formation);
    }


    // === MÉTHODES DE LISTING ===

    @Transactional(readOnly = true)
    public Page<FormationListDto> listActiveFormations(Pageable pageable) {
        log.debug("Listing des formations actives - Page: {}, Taille: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Formation> formations = formationRepository.findByActiveTrueOrderByDateCreationDesc(pageable);

        // Convertir avec ton mapper
        List<FormationListDto> formationsDto = formationMapper.toListDto(formations.getContent());

        return new PageImpl<>(formationsDto, pageable, formations.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<FormationAdminDto> listAllFormations(Pageable pageable) {
        log.debug("Listing admin de toutes les formations");

        Page<Formation> formations = formationRepository.findAllByOrderByDateCreationDesc(pageable);

        List<FormationAdminDto> formationsDto = formationMapper.toAdminDto(formations.getContent());

        return new PageImpl<>(formationsDto, pageable, formations.getTotalElements());
    }



    public Optional<SocialProofDto> updateSocialProof(Long id, SocialProofDto socialProofDto) {
        log.info("Mise à jour social proof formation ID : {}", id);

        return formationRepository.findById(id)
                .map(formation -> {
                    formation.setNombreInscritsAffiche(socialProofDto.getNombreInscritsAffiche());
                    formation.setSocialProofActif(socialProofDto.getSocialProofActif());

                    Formation formationMiseAJour = formationRepository.save(formation);

                    return formationMapper.toSocialProofDto(formationMiseAJour);
                });
    }

    public void recordInfoRequest(Long formationId) {
        formationRepository.findById(formationId)
                .ifPresent(formation -> {
                    formationUtils.incrementerDemandesInfo(formation);
                    formationRepository.save(formation);
                    log.info("Demande d'info enregistrée pour formation : {}", formationId);
                });
    }

    public boolean recordEnrollment(Long formationId) {
        return formationRepository.findById(formationId)
                .map(formation -> {
                    // Vérifier si inscription possible
                    if (!formationUtils.peutSInscrire(formation)) {
                        log.warn("Inscription impossible pour formation : {}", formationId);
                        return false;
                    }
                    // Ajouter l'inscription
                    formationUtils.ajouterInscriptionReelle(formation);
                    formationRepository.save(formation);

                    log.info("Inscription enregistrée pour formation : {}", formationId);
                    return true;
                })
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Page<FormationListDto> searchFormations(String searchTerm, Pageable pageable) {
        log.debug("Recherche unifiée avec terme : '{}'", searchTerm);

        // Utiliser la specification simplifiée
        Specification<Formation> spec = FormationSpecifications.recherchePublique(searchTerm);
        Page<Formation> formations = formationRepository.findAll(spec, pageable);

        List<FormationListDto> formationsDto = formationMapper.toListDto(formations.getContent());
        return new PageImpl<>(formationsDto, pageable, formations.getTotalElements());
    }

    //pour les messages

    @Transactional(readOnly = true)
    public Optional<Formation> getFormationEntityById(Long id) {
        return formationRepository.findByIdAndActiveTrue(id);
    }

    @Transactional(readOnly = true)
    public Optional<String> getFormationNomById(Long id) {
        return formationRepository.findById(id)
                .map(Formation::getNom);
    }

    @Transactional(readOnly = true)
    public boolean formationExiste(Long id) {
        return formationRepository.existsByIdAndActiveTrue(id);
    }

    @Transactional(readOnly = true)
    public List<FormationSimpleDto> getFormationsForSelection() {
        log.debug("Récupération formations pour sélection dropdown");

        // Requête optimisée - seulement formations actives, triées par nom
        List<Formation> formations = formationRepository.findByActiveTrueOrderByNomAsc();

        // Filtrer si nécessaire (formations avec places disponibles pour pré-inscriptions)
        List<Formation> formationsDisponibles = formations.stream()
                .filter(f -> formationUtils.peutRecevoirPreInscriptions(f))
                .toList();

        log.info("Formations disponibles pour sélection : {}", formationsDisponibles.size());

        return formationMapper.toSimpleDto(formationsDisponibles);
    }

    @Transactional(readOnly = true)
    public List<FormationSimpleDto> getToutesFormationsForSelection() {
        log.debug("Récupération toutes formations actives pour sélection");

        List<Formation> formations = formationRepository.findByActiveTrueOrderByNomAsc();
        return formationMapper.toSimpleDto(formations);
    }
}