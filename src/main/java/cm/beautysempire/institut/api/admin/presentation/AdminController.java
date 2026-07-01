package cm.beautysempire.institut.api.admin.presentation;

import cm.beautysempire.institut.api.shared.ApiResponse;
import cm.beautysempire.institut.domain.formation.FormationRepositoryPort;
import cm.beautysempire.institut.domain.messages.MessageRepositoryPort;
import cm.beautysempire.institut.domain.messages.StatutMessage;
import cm.beautysempire.institut.domain.newsletter.NewsletterRepositoryPort;
import cm.beautysempire.institut.domain.testimonial.TestimonialRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final MessageRepositoryPort messageRepositoryPort;
    private final FormationRepositoryPort formationRepositoryPort;
    private final NewsletterRepositoryPort newsletterRepositoryPort;
    private final TestimonialRepositoryPort testimonialRepositoryPort;

    @GetMapping("/stats/overview")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        long nonLus = messageRepositoryPort.countByStatut(StatutMessage.NON_LU);
        long formationsActives = formationRepositoryPort.countByActiveTrue();
        long abonnes = newsletterRepositoryPort.count();
        long temoignages = testimonialRepositoryPort.count();

        DashboardStatsResponse stats = DashboardStatsResponse.builder()
                .messagesNonLus(nonLus)
                .formationsActives(formationsActives)
                .abonnesNewsletter(abonnes)
                .totalTemoignages(temoignages)
                .build();

        return ResponseEntity.ok(ApiResponse.success(stats, "Statistiques récupérées"));
    }
}
