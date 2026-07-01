// api/testimonial/TestimonialController.java
package cm.beautysempire.institut.api.testimonial;

import cm.beautysempire.institut.api.shared.ApiResponse;
import cm.beautysempire.institut.application.service.TestimonialUseCase;
import cm.beautysempire.institut.domain.testimonial.Testimonial;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/testimonials")
@RequiredArgsConstructor
public class TestimonialController {
    private final TestimonialUseCase useCase;
    private final TestimonialApiMapper mapper;

    @PostMapping
    public ResponseEntity<ApiResponse<TestimonialResponse>> creer(@Valid @RequestBody TestimonialCreateRequest request) {
        Testimonial saved = useCase.creer(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(mapper.toResponse(saved), "Témoignage créé"));
    }

    @GetMapping // Public
    public ResponseEntity<ApiResponse<Page<TestimonialResponse>>> listerPublies(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<TestimonialResponse> response = useCase.listerPublies(page, size).map(mapper::toResponse);
        return ResponseEntity.ok(ApiResponse.success(response, "Témoignages publics"));
    }

    @GetMapping("/admin") // Admin
    public ResponseEntity<ApiResponse<Page<TestimonialResponse>>> listerTous(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<TestimonialResponse> response = useCase.listerTous(page, size).map(mapper::toResponse);
        return ResponseEntity.ok(ApiResponse.success(response, "Tous les témoignages"));
    }

    @PatchMapping("/{id}/publier")
    public ResponseEntity<ApiResponse<TestimonialResponse>> togglePublication(@PathVariable Long id) {
        Testimonial updated = useCase.togglePublication(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(updated), "Statut mis à jour"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimer(@PathVariable Long id) {
        useCase.supprimer(id);
        return ResponseEntity.ok(ApiResponse.success("Témoignage supprimé"));
    }
}