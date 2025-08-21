package com.example.institue1.controller;

import com.example.institue1.dto.*;
import com.example.institue1.dto.formation.*;
import com.example.institue1.exception.FormationNotFoundException;
import com.example.institue1.service.FormationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;

@Tag(name = "Formations", description = "API de gestion des formations")
@RestController
@RequestMapping("/api/formations")
@Slf4j

public class FormationController {

    private final FormationService formationService;

    public FormationController(FormationService formationService) {
        this.formationService = formationService;
    }

    // ========================================
    // ENDPOINTS PUBLICS (Visiteurs/Étudiants)
    // ========================================

    @Operation(
            summary = "Recherche formations",
            description = "Recherche dans nom, description, catégorie. Sans 'q' = liste toutes les formations actives"
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<FormationListDto>>> searchFormations(
            @RequestParam(required = false) String q, // Le terme de recherche
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("Recherche formations - q:'{}', page:{}, size:{}", q, page, size);

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, validateSortField(sortBy)));

        Page<FormationListDto> formations = formationService.searchFormations(q, pageable);

        String message = (q == null || q.trim().isEmpty()) ?
                formations.getTotalElements() + " formation(s) active(s)" :
                formations.getTotalElements() + " résultat(s) pour '" + q + "'";

        ApiResponse<Page<FormationListDto>> response = ApiResponse.success(formations, message);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Liste des formations active",
            description = "Recherche dans nom, description, catégorie. Sans 'q' = liste toutes les formations actives"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<FormationListDto>>> listAllFormationsActive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, validateSortField(sortBy)));

        Page<FormationListDto> formations = formationService.listActiveFormations(pageable);

        ApiResponse<Page<FormationListDto>> response = ApiResponse.success(
                formations,
                "Liste administration - " + formations.getTotalElements() + " formation(s)"
        );

        return ResponseEntity.ok(response);
    }



    @Operation(
            summary = "Récupère formation par ID",
            description = "Incrémente automatiquement le compteur de vues si demandé"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FormationDetailDto>> getFormationById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean incrementerVues) {

        log.debug("GET /formations/{} - incrementer vues: {}", id, incrementerVues);

        Optional<FormationDetailDto> formation = formationService.getFormationById(id, incrementerVues);

        if (formation.isPresent()) {
            ApiResponse<FormationDetailDto> response = ApiResponse.success(
                    formation.get(),
                    "Formation récupérée avec succès"
            );
            return ResponseEntity.ok(response);
        } else {
            throw new FormationNotFoundException(id);
        }
    }

    @Operation(
            summary = "Récupère formation par slug SEO",
            description = "URL SEO-friendly, formations actives uniquement"
    )
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<FormationDetailDto>> getFormationBySlug(
            @PathVariable String slug,
            @RequestParam(defaultValue = "true") boolean incrementerVues) {

        log.debug("GET /formations/slug/{}", slug);

        Optional<FormationDetailDto> formation = formationService.getFormationBySlug(slug, incrementerVues);

        if (formation.isPresent()) {
            ApiResponse<FormationDetailDto> response = ApiResponse.success(
                    formation.get(),
                    "Formation récupérée avec succès"
            );
            return ResponseEntity.ok(response);
        } else {
            throw new FormationNotFoundException("slug", slug);
        }
    }



    @Operation(
            summary = "Enregistre demande d'information",
            description = "Incrémente le compteur de demandes d'info pour analytics"
    )
    @PostMapping("/{id}/info-requests")
    public ResponseEntity<ApiResponse<Object>> recordInfoRequest(@PathVariable Long id) {
        formationService.recordInfoRequest(id);

        ApiResponse<Object> response = ApiResponse.success("Demande d'information enregistrée avec succès");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Enregistre une inscription",
            description = "Vérifie les places disponibles avant inscription"
    )
    @PostMapping("/{id}/enrollments")
    public ResponseEntity<ApiResponse<Object>> recordEnrollment(@PathVariable Long id) {
        boolean inscriptionReussie = formationService.recordEnrollment(id);

        if (inscriptionReussie) {
            ApiResponse<Object> response = ApiResponse.success("Inscription enregistrée avec succès");
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Object> response = ApiResponse.badRequest(
                    "Inscription impossible - Formation complète ou indisponible"
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==========================================
    // ENDPOINTS ADMINISTRATION (Rôle ADMIN requis)
    // ==========================================

    @Operation(
            summary = "Crée nouvelle formation (ADMIN)",
            description = "Génère automatiquement slug et timestamps. Accès admin requis."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<FormationDetailDto>> createFormation(
            @Valid @RequestBody FormationCreateDto createDto,
            @RequestHeader(value = "X-Admin-User", defaultValue = "system") String adminCreateur) {

        log.info("Création formation par admin: {}", adminCreateur);

        FormationDetailDto nouvelleFormation = formationService.createFormation(createDto, adminCreateur);

        ApiResponse<FormationDetailDto> response = ApiResponse.created(
                nouvelleFormation,
                "Formation '" + nouvelleFormation.getNom() + "' créée avec succès"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Modifie formation existante (ADMIN)",
            description = "Met à jour timestamp et admin modificateur. Accès admin requis."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FormationDetailDto>> updateFormation(
            @PathVariable Long id,
            @Valid @RequestBody FormationUpdateDto updateDto,
            @RequestHeader(value = "X-Admin-User", defaultValue = "system") String adminModificateur) {

        log.info("Modification formation {} par admin: {}", id, adminModificateur);

        Optional<FormationDetailDto> formationModifiee =
                formationService.updateFormation(id, updateDto, adminModificateur);

        if (formationModifiee.isPresent()) {
            ApiResponse<FormationDetailDto> response = ApiResponse.success(
                    formationModifiee.get(),
                    "Formation modifiée avec succès"
            );
            return ResponseEntity.ok(response);
        } else {
            throw new FormationNotFoundException(id);
        }
    }

    @Operation(
            summary = "Désactive formation (ADMIN)",
            description = "Suppression logique - passe 'active' à false. Accès admin requis."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteFormation(@PathVariable Long id) {
        log.info("Suppression formation: {}", id);

        formationService.deleteFormation(id);

        ApiResponse<Object> response = ApiResponse.success("Formation désactivée avec succès");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Liste formations pour admin",
            description = "Inclut formations actives ET inactives. Vue administrative complète."
    )
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<FormationAdminDto>>> listAllFormationsAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, validateSortField(sortBy)));

        Page<FormationAdminDto> formations = formationService.listAllFormations(pageable);

        ApiResponse<Page<FormationAdminDto>> response = ApiResponse.success(
                formations,
                "Liste administration - " + formations.getTotalElements() + " formation(s)"
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Met à jour social proof (ADMIN)",
            description = "Gère affichage nombre inscrits pour marketing. Fonctionnalité business critique."
    )
    @PutMapping("/{id}/social-proof")
    public ResponseEntity<ApiResponse<SocialProofDto>> updateSocialProof(
            @PathVariable Long id,
            @Valid @RequestBody SocialProofDto socialProofDto) {

        log.info("Mise à jour social proof formation: {}", id);

        Optional<SocialProofDto> socialProofMisAJour =
                formationService.updateSocialProof(id, socialProofDto);

        if (socialProofMisAJour.isPresent()) {
            ApiResponse<SocialProofDto> response = ApiResponse.success(
                    socialProofMisAJour.get(),
                    "Social proof mis à jour avec succès"
            );
            return ResponseEntity.ok(response);
        } else {
            throw new FormationNotFoundException(id);
        }
    }

    @GetMapping("/selection")
    public ResponseEntity<ApiResponse<List<FormationSimpleDto>>> getFormationsForSelection(
            @RequestParam(defaultValue = "false") boolean inscriptionUniquement) {

        log.debug("GET /formations/selection - inscription uniquement: {}", inscriptionUniquement);

        List<FormationSimpleDto> formations;
        String message;

        if (inscriptionUniquement) {
            // Pour pré-inscriptions : formations avec places disponibles
            formations = formationService.getFormationsForSelection();
            message = formations.size() + " formation(s) avec places disponibles";
        } else {
            // Pour contact général : toutes formations actives
            formations = formationService.getToutesFormationsForSelection();
            message = formations.size() + " formation(s) active(s)";
        }

        ApiResponse<List<FormationSimpleDto>> response = ApiResponse.success(formations, message);
        return ResponseEntity.ok(response);
    }


    // ==========================================
    // MÉTHODES UTILITAIRES PRIVÉES
    // ==========================================

    /**
     * Valide et sécurise les champs de tri
     */
    private String validateSortField(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "nom" -> "nom";
            case "prix" -> "prix";
            case "categorie" -> "categorie";
            case "datecreation", "date_creation" -> "dateCreation";
            case "datemiseajour", "date_mise_a_jour" -> "dateMiseAJour";
            case "nombreinscriptions", "nombre_inscriptions" -> "nombreInscriptions";
            case "nombrevues", "nombre_vues" -> "nombreVues";
            case "pourcentagereduction", "pourcentage_reduction" -> "pourcentageReduction";
            default -> "dateCreation";
        };
    }
}