package cm.beautysempire.institut.application.service;

import cm.beautysempire.institut.application.exception.FormationCompleteException;
import cm.beautysempire.institut.application.exception.FormationNotFoundException;
import cm.beautysempire.institut.domain.formation.Formation;
import cm.beautysempire.institut.domain.formation.FormationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;


@RequiredArgsConstructor
public class FormationUseCase {

    private final FormationRepositoryPort formationRepositoryPort;

    public Formation creerFormation(Formation formation, String admin) {
        // 🔥 On vérifie si le nom existe déjà
        if (formationRepositoryPort.existsByNom(formation.getNom())) {
            throw new IllegalArgumentException("Une formation avec le nom '" + formation.getNom() + "' existe déjà.");
        }

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

    public Formation modifierFormation(Long id, Formation nouvellesInfos, String admin){

        Formation formationExistante = formationRepositoryPort.findById(id)
                .orElseThrow(() -> new FormationNotFoundException(id));

        formationExistante.mettreAJourInfos(nouvellesInfos, admin);
        return formationRepositoryPort.save(formationExistante);
    }

    public Formation mettreAjourSocialProof(Long id, Integer nouveauNombre, Boolean actif, String admin){
        Formation formationExistante = formationRepositoryPort.findById(id)
                .orElseThrow(() -> new FormationNotFoundException(id));
        formationExistante.mettreAJourSocialProof(nouveauNombre, actif, admin);
        return formationRepositoryPort.save(formationExistante);
    }

    public void supprimerFormation(Long id, String admin) {
        Formation formation = formationRepositoryPort.findById(id)
                .orElseThrow(() -> new FormationNotFoundException(id));

        formation.desactiver(admin);
        formationRepositoryPort.save(formation);
    }

    public Page<Formation> rechercherFormations(String motCle, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));
        return formationRepositoryPort.searchActiveFormations(motCle, pageRequest);
    }


    public Page<Formation> listerFormationsActivesPaginees(int page, int size) {
        // On trie par date de création décroissante (les plus récentes en premier)
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));
        return formationRepositoryPort.findAllActivePaginated(pageRequest);
    }

    public Formation obtenirFormationParId(Long id) {
        return formationRepositoryPort.findById(id)
                .orElseThrow(() -> new FormationNotFoundException(id));
    }

    public Formation obtenirFormationParSlug(String slug) {
        Formation formation = formationRepositoryPort.findBySlug(slug)
                .orElseThrow(() -> new FormationNotFoundException(slug));

        // On incrémente les vues à chaque fois qu'on charge la page par le slug
        formation.incrementerVues();
        return formationRepositoryPort.save(formation);
    }


    public void activerFormation(Long id, String admin) {
        Formation formation = formationRepositoryPort.findById(id)
                .orElseThrow(() -> new FormationNotFoundException(id));

        formation.activer(admin);
        formationRepositoryPort.save(formation);
    }





}
