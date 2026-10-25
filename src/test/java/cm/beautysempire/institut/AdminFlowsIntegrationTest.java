package cm.beautysempire.institut;

import cm.beautysempire.institut.application.port.StoragePort;
import cm.beautysempire.institut.domain.messages.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Tests réels PostgreSQL : définir IBE_TEST_DATABASE_URL et SPRING_DATASOURCE_URL. */
@EnabledIfEnvironmentVariable(named = "IBE_TEST_DATABASE_URL", matches = ".+")
@SpringBootTest(properties = {
        "spring.datasource.url=${IBE_TEST_DATABASE_URL}",
        "app.admin.username=integration-admin", "app.admin.password=integration-password",
        "app.cors.allowed-origins=http://localhost:4415", "app.whatsapp.admin-number=237600000000",
        "cloudinary.cloud-name=integration", "cloudinary.api-key=integration", "cloudinary.api-secret=integration"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminFlowsIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private MessageRepositoryPort messages;
    @MockBean private StoragePort storage;

    private static final String AUTH = "Basic " + Base64.getEncoder().encodeToString(
            "integration-admin:integration-password".getBytes(StandardCharsets.UTF_8));

    @Test
    void toutesLesListesAdminSontProtegees() throws Exception {
        for (String url : new String[]{"/api/formations/admin", "/api/messages", "/api/newsletter",
                "/api/testimonials/admin", "/api/admin/stats/overview"}) {
            mvc.perform(get(url)).andExpect(status().isUnauthorized());
        }
        mvc.perform(get("/api/formations/actives")).andExpect(status().isOk());
        mvc.perform(get("/api/formations/mises-en-avant")).andExpect(status().isOk());
        mvc.perform(get("/api/messages").header("Authorization", "Basic " + Base64.getEncoder()
                        .encodeToString("integration-admin:incorrect".getBytes(StandardCharsets.UTF_8))))
                .andExpect(status().isUnauthorized()).andExpect(header().doesNotExist("WWW-Authenticate"));
    }

    @Test
    void ficheSansTarifPeutEtreCreeeEtModifieeSansInventerDeMontant() throws Exception {
        String body = "{\"nom\":\"Formation sans tarif\",\"description\":\"À compléter\",\"categorie\":\"Gestion\",\"enPromotion\":true,\"pourcentageReduction\":20}";
        JsonNode created = data(mvc.perform(post("/api/formations").header("Authorization", AUTH)
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        long id = created.path("id").asLong();
        assertThat(created.path("prix").isNull()).isTrue();
        assertThat(created.path("prixAvecReduction").isNull()).isTrue();
        assertThat(created.path("isPromoActive").asBoolean()).isFalse();
        mvc.perform(put("/api/formations/" + id).header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.categorie").value("Gestion"));
        mvc.perform(post("/api/formations").header("Authorization", AUTH).contentType(MediaType.APPLICATION_JSON)
                        .content(body.replace("Formation sans tarif", "Tarif invalide").replace("\"Gestion\"", "\"Gestion\",\"prix\":-1")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void lesReglagesDeLaFicheSontRenvoyesEtConserves() throws Exception {
        String body = """
                {"nom":"Formation complète","description":"Description","categorie":"Industrie","prix":100000,
                 "fraisInscription":10000,"nombrePlaces":20,"joursFormation":"Lundi, mercredi",
                 "dateFinInscription":"2026-12-01","enPromotion":true,"pourcentageReduction":10,
                 "dateDebutPromo":"2026-01-01T00:00:00","dateFinPromo":"2027-01-01T00:00:00",
                 "socialProofActif":true,"nombreInscritsAffiche":4,
                 "photosGalerie":["https://example.invalid/a.jpg","https://example.invalid/b.jpg"]}
                """;
        JsonNode created = data(mvc.perform(post("/api/formations").header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        long id = created.path("id").asLong();
        JsonNode loaded = data(mvc.perform(get("/api/formations/" + id)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
        assertThat(loaded.path("joursFormation").asText()).isEqualTo("Lundi, mercredi");
        assertThat(loaded.path("dateFinInscription").asText()).isEqualTo("2026-12-01");
        assertThat(loaded.path("enPromotion").asBoolean()).isTrue();
        assertThat(loaded.path("socialProofActif").asBoolean()).isTrue();
        assertThat(loaded.path("nombreInscritsAffiche").asInt()).isEqualTo(4);
        mvc.perform(put("/api/formations/" + id).header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(loaded)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.photosGalerie.length()").value(2))
                .andExpect(jsonPath("$.data.enPromotion").value(true))
                .andExpect(jsonPath("$.data.prixAvecReduction").value(90000));
    }

    @Test
    void rechercheGlobaleFiltreAvantPaginationEtRetourneLeVraiTexte() throws Exception {
        for (int i = 0; i < 24; i++) {
            Message message = Message.builder().type(TypeMessage.CONTACT_GENERAL).nom("Contact " + i)
                    .telephone("237600000001").message(i == 0 ? "Demande très spécifique : horaires du soir" : "Autre demande")
                    .statut(i == 0 ? StatutMessage.NON_LU : StatutMessage.LU)
                    .dateCreation(LocalDateTime.of(2026, 10, 1, 10, 0).plusMinutes(i)).build();
            messages.save(message);
        }
        mvc.perform(get("/api/messages").header("Authorization", AUTH)
                        .param("q", "spécifique").param("statut", "NON_LU").param("type", "CONTACT_GENERAL")
                        .param("page", "0").param("size", "5"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].nom").value("Contact 0"))
                .andExpect(jsonPath("$.data.content[0].message").value("Demande très spécifique : horaires du soir"));
        mvc.perform(get("/api/messages").header("Authorization", AUTH).param("page", "1").param("size", "5"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.totalElements").value(24))
                .andExpect(jsonPath("$.data.content.length()").value(5));
        mvc.perform(get("/api/messages").header("Authorization", AUTH).param("statut", "INCONNU"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/messages").header("Authorization", AUTH).param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void contactReelPuisLectureTraitementEtArchivageSansEnvoiAutomatique() throws Exception {
        String body = """
                {"nom":"Demande test","telephone":"237600000001","message":"Mon texte exact, sans résumé.",
                 "email":"demo@example.invalid","ville":"Yaoundé","quartier":"Tsinga"}
                """;
        JsonNode saved = data(mvc.perform(post("/api/messages/contact").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        assertThat(saved.path("whatsappConfirmationLink").asText()).startsWith("https://wa.me/");
        long id = saved.path("id").asLong();
        mvc.perform(patch("/api/messages/" + id + "/lu").header("Authorization", AUTH))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.message").value("Mon texte exact, sans résumé."))
                .andExpect(jsonPath("$.data.email").value("demo@example.invalid"));
        mvc.perform(patch("/api/messages/" + id + "/traite").header("Authorization", AUTH))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.statut").value("TRAITE"));
        mvc.perform(patch("/api/messages/" + id + "/archive").header("Authorization", AUTH))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.statut").value("ARCHIVE"));
        mvc.perform(patch("/api/messages/" + id + "/lu").header("Authorization", AUTH))
                .andExpect(status().isBadRequest());
        mvc.perform(patch("/api/messages/99999999/lu").header("Authorization", AUTH))
                .andExpect(status().isNotFound());
    }

    @Test
    void preInscriptionSansTexteLibreEtAbonneCatalogue() throws Exception {
        JsonNode course = data(mvc.perform(post("/api/formations").header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Formation test inscription\",\"description\":\"Description\",\"categorie\":\"Santé\",\"nombrePlaces\":10}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        mvc.perform(post("/api/messages/pre-inscription").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Test inscription\",\"telephone\":\"237600000002\",\"formationId\":" + course.path("id").asLong() + "}"))
                .andExpect(status().isCreated());
        JsonNode sub = data(mvc.perform(post("/api/newsletter/subscribe").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"telephone\":\"237600000003\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        mvc.perform(patch("/api/newsletter/" + sub.path("id").asLong() + "/contacte").header("Authorization", AUTH))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.contacte").value(true));
    }

    @Test
    void galerieEtTemoignagesAvecStockageExterneSimule() throws Exception {
        when(storage.uploadImage(any(), anyString())).thenReturn(new String[]{"https://example.invalid/demo.jpg", "demo-id"});
        MockMultipartFile image = new MockMultipartFile("file", "demo.jpg", "image/jpeg", new byte[]{1, 2, 3});
        JsonNode uploaded = data(mvc.perform(multipart("/api/gallery/upload").file(image)
                        .param("titre", "Image test").param("categorie", "FORMATION").param("isPublic", "true")
                        .header("Authorization", AUTH)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        mvc.perform(get("/api/gallery")).andExpect(status().isOk()).andExpect(jsonPath("$.data.totalElements").value(1));
        mvc.perform(delete("/api/gallery/" + uploaded.path("id").asLong()).header("Authorization", AUTH)).andExpect(status().isOk());
        verify(storage).deleteImage("demo-id");
        JsonNode review = data(mvc.perform(post("/api/testimonials").header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomEtudiant\":\"Avis test\",\"formationSuivie\":\"Onglerie\",\"temoignage\":\"Texte de test\",\"note\":5,\"publie\":true}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        long id = review.path("id").asLong();
        mvc.perform(patch("/api/testimonials/" + id + "/publier").header("Authorization", AUTH))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.publie").value(false));
        mvc.perform(get("/api/testimonials")).andExpect(status().isOk()).andExpect(jsonPath("$.data.totalElements").value(0));
        mvc.perform(delete("/api/testimonials/" + id).header("Authorization", AUTH)).andExpect(status().isOk());
    }

    private JsonNode data(String response) throws Exception {
        return json.readTree(response).path("data");
    }
}
