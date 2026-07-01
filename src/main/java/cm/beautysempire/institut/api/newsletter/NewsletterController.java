// Fichier : src/main/java/cm/beautysempire/institut/api/newsletter/NewsletterController.java
package cm.beautysempire.institut.api.newsletter;

import cm.beautysempire.institut.api.shared.ApiResponse;
import cm.beautysempire.institut.application.service.NewsletterUseCase;
import cm.beautysempire.institut.domain.newsletter.NewsletterSubscription;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/newsletter") // 💡 Convention REST : singulier pour la ressource principale
@RequiredArgsConstructor
public class NewsletterController {

    private final NewsletterUseCase newsletterUseCase;
    private final NewsletterApiMapper mapper;

    // 1. S'inscrire (Public)
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<NewsletterResponse>> souscrire(
            @Valid @RequestBody NewsletterSubscribeRequest request) {

        NewsletterSubscription subscription = mapper.toDomain(request);
        NewsletterSubscription savedSubscription = newsletterUseCase.souscrire(subscription);
        NewsletterResponse response = mapper.toResponse(savedSubscription);

        String lienWhatsapp = newsletterUseCase.genererLienWhatsAppCatalogue();
        response.setWhatsappCatalogueLink(lienWhatsapp);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Inscription à la newsletter réussie"));
    }

    // 2. Lister les abonnés (Admin)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NewsletterResponse>>> listerAbonnes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<NewsletterSubscription> abonnés = newsletterUseCase.listerAbonnes(page, size);
        Page<NewsletterResponse> response = abonnés.map(mapper::toResponse);

        return ResponseEntity.ok(ApiResponse.success(response, "Liste des abonnés récupérée"));
    }

    // 3. Marquer comme contacté (Admin)
    @PatchMapping("/{id}/contacte")
    public ResponseEntity<ApiResponse<NewsletterResponse>> marquerCommeContacte(@PathVariable Long id) {

        NewsletterSubscription updatedSubscription = newsletterUseCase.marquerCommeContacte(id);
        NewsletterResponse response = mapper.toResponse(updatedSubscription);

        return ResponseEntity.ok(ApiResponse.success(response, "Abonné marqué comme contacté"));
    }


}