// Fichier : src/main/java/cm/beautysempire/institut/api/formation/presentation/formation/FormationController.java

package cm.beautysempire.institut.api.formation.presentation.formation;

import cm.beautysempire.institut.application.service.FormationUseCase;
import cm.beautysempire.institut.domain.formation.Formation;
import cm.beautysempire.institut.api.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
public class FormationController {

    private final FormationUseCase formationUseCase;
    private final FormationApiMapper formationApiMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<FormationResponse>> creerFormation(@Valid @RequestBody FormationCreateRequest request) {
        Formation formation = formationApiMapper.toDomain(request);

        // "admin" sera remplacé plus tard par l'utilisateur connecté via Spring Security
        Formation createdFormation = formationUseCase.creerFormation(formation, "admin");

        FormationResponse response = formationApiMapper.toResponse(createdFormation);

        // 🔥 On utilise ApiResponse.created() ici !
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Formation créée avec succès"));
    }

    @PostMapping("/{formationId}/inscriptions")
    public ResponseEntity<ApiResponse<Void>> enregistrerInscription(@PathVariable Long formationId) {
        formationUseCase.enregistrerInscription(formationId);
        return ResponseEntity.ok(ApiResponse.success("Inscription enregistrée avec succès"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FormationResponse>> modifierFormation(
            @PathVariable Long id,
            @Valid @RequestBody FormationCreateRequest request){
        Formation formation = formationApiMapper.toDomain(request);

        Formation updateFormation = formationUseCase.modifierFormation(id, formation, "admin");

        FormationResponse response = formationApiMapper.toResponse(updateFormation);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(response, "Formation mise a jour avec succès"));
    }

    @PatchMapping("/{id}/social-proof")
    public ResponseEntity<ApiResponse<FormationResponse>> mettreAJourSocialProof(
            @PathVariable Long id,
            @RequestParam Integer nombreAffiche,
            @RequestParam Boolean actif) {

        Formation updateSocialProof = formationUseCase.mettreAjourSocialProof(id, nombreAffiche, actif, "admin");
        FormationResponse response = formationApiMapper.toResponse(updateSocialProof);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(response, "Mise a jour Social Proof realise avec succès"));
    }

    @GetMapping("/actives")
    public ResponseEntity<ApiResponse<Page<FormationResponse>>> listerFormationsActives(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Page<Formation> formationsPage = formationUseCase.listerFormationsActivesPaginees(page, size);

        // La méthode .map() de l'objet Page permet de convertir chaque élément facilement !
        Page<FormationResponse> responsePage = formationsPage.map(formationApiMapper::toResponse);

        return ResponseEntity.ok(ApiResponse.success(responsePage, "Liste des formations affichée avec succès"));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimerFormation(@PathVariable Long id) {
        formationUseCase.supprimerFormation(id, "admin");
        return ResponseEntity.ok(ApiResponse.success("Formation désactivée avec succès"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<FormationResponse>>> rechercherFormations(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Page<Formation> formationsPage = formationUseCase.rechercherFormations(q, page, size);
        Page<FormationResponse> responsePage = formationsPage.map(formationApiMapper::toResponse);

        return ResponseEntity.ok(ApiResponse.success(responsePage, "Résultats de la recherche"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FormationResponse>> obtenirFormationParId(@PathVariable Long id) {
        Formation formation = formationUseCase.obtenirFormationParId(id);
        FormationResponse response = formationApiMapper.toResponse(formation);
        return ResponseEntity.ok(ApiResponse.success(response, "Formation récupérée avec succès"));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<FormationResponse>> obtenirFormationParSlug(@PathVariable String slug) {
        Formation formation = formationUseCase.obtenirFormationParSlug(slug);
        FormationResponse response = formationApiMapper.toResponse(formation);
        return ResponseEntity.ok(ApiResponse.success(response, "Formation récupérée avec succès"));
    }

    @PatchMapping("/{id}/activer")
    public ResponseEntity<ApiResponse<Void>> activerFormation(@PathVariable Long id) {
        formationUseCase.activerFormation(id, "admin");
        return ResponseEntity.ok(ApiResponse.success("Formation réactivée avec succès"));
    }

}