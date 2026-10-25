package cm.beautysempire.institut.infrastructure.config;

import cm.beautysempire.institut.application.service.FormationUseCase;
import cm.beautysempire.institut.domain.formation.Formation;
import cm.beautysempire.institut.domain.formation.FormationRepositoryPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FormationSeederTest {

    @Test
    void uneBaseVideRecoitLes53FormationsSansDoublonAuRedemarrage() {
        FormationRepositoryPort repository = mock(FormationRepositoryPort.class);
        Map<String, Formation> catalogue = new HashMap<>();
        when(repository.count()).thenAnswer(call -> (long) catalogue.size());
        when(repository.existsByNom(anyString())).thenAnswer(call -> catalogue.containsKey(call.getArgument(0)));
        when(repository.save(any(Formation.class))).thenAnswer(call -> {
            Formation formation = call.getArgument(0);
            catalogue.put(formation.getNom(), formation);
            return formation;
        });
        FormationSeeder seeder = new FormationSeeder(repository, new FormationUseCase(repository));

        seeder.run();
        assertThat(catalogue).hasSize(53).containsKeys("Infographie", "Sage-femme", "Élevage");

        seeder.run();
        assertThat(catalogue).hasSize(53);
        verify(repository, times(53)).save(any(Formation.class));
    }

    @Test
    void ajouteLesFormationsDeLAfficheSansInventerLesTarifsDansUneBaseExistante() {
        FormationRepositoryPort repository = mock(FormationRepositoryPort.class);
        when(repository.count()).thenReturn(23L);
        FormationSeeder seeder = new FormationSeeder(repository, new FormationUseCase(repository));

        seeder.run();

        ArgumentCaptor<Formation> formations = ArgumentCaptor.forClass(Formation.class);
        verify(repository, times(30)).save(formations.capture());
        assertThat(formations.getAllValues()).allSatisfy(formation -> {
            assertThat(formation.getDescription()).isNotBlank();
            assertThat(formation.getPrix()).isNull();
            assertThat(formation.getFraisInscription()).isNull();
            assertThat(formation.getDuree()).isNull();
            assertThat(formation.getNombrePlaces()).isNull();
        });
        assertThat(formations.getAllValues()).extracting(Formation::getSlug).doesNotHaveDuplicates();
        assertThat(formations.getAllValues()).filteredOn(f -> f.getCategorie().equals("Gestion")).hasSize(8);
        assertThat(formations.getAllValues()).filteredOn(f -> f.getCategorie().equals("Santé")).hasSize(11);
        assertThat(formations.getAllValues()).filteredOn(f -> f.getCategorie().equals("Informatique")).hasSize(4);
        assertThat(formations.getAllValues()).filteredOn(f -> f.getCategorie().equals("Industrie")).hasSize(6);
    }

    @Test
    void unRedemarrageNeDupliquePasLesFormationsEtConserveLesPrixAdmin() {
        FormationRepositoryPort repository = mock(FormationRepositoryPort.class);
        when(repository.count()).thenReturn(23L);
        Map<String, Formation> catalogue = new HashMap<>();
        when(repository.existsByNom(anyString())).thenAnswer(call -> catalogue.containsKey(call.getArgument(0)));
        when(repository.save(any(Formation.class))).thenAnswer(call -> {
            Formation formation = call.getArgument(0);
            catalogue.put(formation.getNom(), formation);
            return formation;
        });
        FormationSeeder seeder = new FormationSeeder(repository, new FormationUseCase(repository));
        seeder.run();
        Formation infographie = catalogue.get("Infographie");
        infographie.setPrix(new BigDecimal("123456"));

        seeder.run();

        verify(repository, times(30)).save(any(Formation.class));
        assertThat(catalogue).hasSize(30);
        assertThat(catalogue.get("Infographie").getPrix()).isEqualByComparingTo("123456");
    }

    @Test
    void conserveUneFormationRenommeeParAdminAvecLeMemeSlug() {
        FormationRepositoryPort repository = mock(FormationRepositoryPort.class);
        when(repository.count()).thenReturn(23L);
        when(repository.findBySlug("infographie")).thenReturn(Optional.of(Formation.builder()
                .nom("Infographie et design graphique").slug("infographie").build()));

        new FormationSeeder(repository, new FormationUseCase(repository)).run();

        ArgumentCaptor<Formation> formations = ArgumentCaptor.forClass(Formation.class);
        verify(repository, times(29)).save(formations.capture());
        assertThat(formations.getAllValues()).extracting(Formation::getSlug).doesNotContain("infographie");
    }
}
