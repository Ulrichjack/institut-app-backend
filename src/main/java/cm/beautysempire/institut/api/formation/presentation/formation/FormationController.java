// Fichier : src/main/java/cm/beautysempire/institut/api/formation/presentation/formation/FormationController.java

package cm.beautysempire.institut.api.formation.presentation.formation;

import cm.beautysempire.institut.application.service.FormationUseCase;
import cm.beautysempire.institut.domain.formation.Formation;
import cm.beautysempire.institut.api.formation.presentation.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}