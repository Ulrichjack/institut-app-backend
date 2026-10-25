package cm.beautysempire.institut.api.formation.presentation;

import cm.beautysempire.institut.application.service.FormationUseCase;
import cm.beautysempire.institut.domain.formation.Formation;
import cm.beautysempire.institut.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FormationController.class)
@Import({SecurityConfig.class, FormationApiMapperImpl.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "app.admin.username=test-admin", "app.admin.password=test-password",
        "app.cors.allowed-origins=http://localhost:4200"
})
class FormationMiseEnAvantControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private FormationUseCase useCase;

    private static final String AUTH = "Basic " + Base64.getEncoder()
            .encodeToString("test-admin:test-password".getBytes(StandardCharsets.UTF_8));

    @Test
    void laSelectionEstPubliqueEtExposeLaPosition() throws Exception {
        when(useCase.listerFormationsMisesEnAvant()).thenReturn(List.of(Formation.builder()
                .id(1L).nom("Infographie").active(true).miseEnAvant(true).ordreAffichage(2)
                .nombreInscritsReel(0).build()));

        mvc.perform(get("/api/formations/mises-en-avant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nom").value("Infographie"))
                .andExpect(jsonPath("$.data[0].miseEnAvant").value(true))
                .andExpect(jsonPath("$.data[0].ordreAffichage").value(2));
    }

    @Test
    void unVisiteurNePeutPasModifierLaSelection() throws Exception {
        mvc.perform(patch("/api/formations/1/mise-en-avant").contentType(MediaType.APPLICATION_JSON)
                .content("{\"miseEnAvant\":true,\"ordreAffichage\":1}"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(useCase);
    }

    @Test
    void adminPeutSelectionnerSansCompleterLaFicheNiLesPrix() throws Exception {
        when(useCase.mettreAJourMiseEnAvant(1L, true, 1, "admin")).thenReturn(Formation.builder()
                .id(1L).miseEnAvant(true).ordreAffichage(1).nombreInscritsReel(0).build());

        mvc.perform(patch("/api/formations/1/mise-en-avant").header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"miseEnAvant\":true,\"ordreAffichage\":1}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.ordreAffichage").value(1));
    }

    @Test
    void rejetteLesDonneesInvalides() throws Exception {
        mvc.perform(patch("/api/formations/1/mise-en-avant").header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"miseEnAvant\":true,\"ordreAffichage\":7}"))
                .andExpect(status().isBadRequest());
        mvc.perform(patch("/api/formations/1/mise-en-avant").header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(useCase);
    }

    @Test
    void unePositionOccupeeRetourneUneErreurLisible() throws Exception {
        when(useCase.mettreAJourMiseEnAvant(1L, true, 1, "admin"))
                .thenThrow(new IllegalArgumentException("Cette position sur l'accueil est déjà occupée."));
        mvc.perform(patch("/api/formations/1/mise-en-avant").header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"miseEnAvant\":true,\"ordreAffichage\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cette position sur l'accueil est déjà occupée."));
    }
}
