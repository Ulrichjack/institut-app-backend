package cm.beautysempire.institut.application.service;

import cm.beautysempire.institut.application.exception.FormationCompleteException;
import cm.beautysempire.institut.application.exception.FormationNotFoundException;
import cm.beautysempire.institut.domain.formation.Formation;
import cm.beautysempire.institut.domain.formation.FormationRepositoryPort;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class FormationUseCase {

    private final FormationRepositoryPort formationRepositoryPort;

    public Formation creerFormation(Formation formation, String admin){
            formation.initialiserCreation(admin);
            return formationRepositoryPort.save(formation);
    }

    public void enregistrerInscription(Long formationId) {
        Formation formation = formationRepositoryPort.findById(formationId)
                .orElseThrow(() -> new FormationNotFoundException(formationId));

        // L'entité valide ses propres règles métier
        if (!formation.peutSInscrire()) {
            throw new FormationCompleteException("Impossible de s'inscrire : la formation est complète ou inactive.");
        }

        formation.ajouterInscriptionReelle();
        formationRepositoryPort.save(formation);
    }

}
