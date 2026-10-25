package cm.beautysempire.institut.application.service;

import cm.beautysempire.institut.application.exception.FormationNotFoundException;
import cm.beautysempire.institut.domain.formation.Formation;
import cm.beautysempire.institut.domain.formation.FormationRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FormationMiseEnAvantTest {
    private final FormationRepositoryPort repository = mock(FormationRepositoryPort.class);
    private final FormationUseCase useCase = new FormationUseCase(repository);

    @Test
    void selectionneUneFormationActiveSansChangerSesAutresInformations() {
        Formation formation = Formation.builder().id(1L).nom("Infographie").active(true).build();
        when(repository.findById(1L)).thenReturn(Optional.of(formation));
        when(repository.save(formation)).thenReturn(formation);

        Formation result = useCase.mettreAJourMiseEnAvant(1L, true, 3, "admin");

        assertThat(result.getMiseEnAvant()).isTrue();
        assertThat(result.getOrdreAffichage()).isEqualTo(3);
        assertThat(result.getNom()).isEqualTo("Infographie");
        assertThat(result.getPrix()).isNull();
        assertThat(result.getModifiePar()).isEqualTo("admin");
        verify(repository).existsMiseEnAvantAtPosition(3, 1L);
    }

    @Test
    void refuseUnePositionOccupee() {
        when(repository.findById(1L)).thenReturn(Optional.of(Formation.builder().id(1L).active(true).build()));
        when(repository.existsMiseEnAvantAtPosition(2, 1L)).thenReturn(true);

        assertThatIllegalArgumentException().isThrownBy(() -> useCase.mettreAJourMiseEnAvant(1L, true, 2, "admin"))
                .withMessageContaining("déjà occupée");
        verify(repository, never()).save(any());
    }

    @Test
    void refuseUneFormationInactive() {
        when(repository.findById(1L)).thenReturn(Optional.of(Formation.builder().active(false).build()));
        assertThatIllegalArgumentException().isThrownBy(() -> useCase.mettreAJourMiseEnAvant(1L, true, 1, "admin"));
        verify(repository, never()).save(any());
    }

    @Test
    void refuseLesPositionsHorsDesSixEmplacements() {
        Formation formation = Formation.builder().active(true).build();
        for (Integer position : new Integer[]{null, 0, -1, 7}) {
            assertThatIllegalArgumentException().isThrownBy(() -> formation.mettreAJourMiseEnAvant(true, position, "admin"));
        }
    }

    @Test
    void retireLaSelectionEtLibereLaPosition() {
        Formation formation = Formation.builder().id(1L).active(true).miseEnAvant(true).ordreAffichage(1).build();
        when(repository.findById(1L)).thenReturn(Optional.of(formation));

        useCase.mettreAJourMiseEnAvant(1L, false, null, "admin");

        assertThat(formation.getMiseEnAvant()).isFalse();
        assertThat(formation.getOrdreAffichage()).isNull();
        verify(repository).save(formation);
    }

    @Test
    void desactiverRetireDeLAccueilEtReactiverNeReselectionnePas() {
        Formation formation = Formation.builder().active(true).miseEnAvant(true).ordreAffichage(2).build();
        formation.desactiver("admin");
        assertThat(formation.getMiseEnAvant()).isFalse();
        assertThat(formation.getOrdreAffichage()).isNull();
        formation.activer("admin");
        assertThat(formation.getMiseEnAvant()).isFalse();
    }

    @Test
    void modifierLaFicheSansLesNouveauxChampsConserveLaSelection() {
        Formation formation = Formation.builder().miseEnAvant(true).ordreAffichage(4).build();
        formation.mettreAJourInfos(Formation.builder().nom("Nouveau nom").build(), "admin");
        assertThat(formation.getMiseEnAvant()).isTrue();
        assertThat(formation.getOrdreAffichage()).isEqualTo(4);
    }

    @Test
    void retourneUneListeVideSansSelectionAutomatique() {
        when(repository.findMisesEnAvant()).thenReturn(List.of());
        assertThat(useCase.listerFormationsMisesEnAvant()).isEmpty();
        verify(repository, never()).findAllActivePaginated(any());
    }

    @Test
    void refuseUnIdentifiantInexistant() {
        assertThatThrownBy(() -> useCase.mettreAJourMiseEnAvant(99L, true, 1, "admin"))
                .isInstanceOf(FormationNotFoundException.class);
        verify(repository, never()).save(any());
    }
}
