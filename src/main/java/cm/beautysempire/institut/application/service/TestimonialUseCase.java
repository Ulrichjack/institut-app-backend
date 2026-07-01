// application/service/TestimonialUseCase.java
package cm.beautysempire.institut.application.service;

import cm.beautysempire.institut.domain.testimonial.Testimonial;
import cm.beautysempire.institut.domain.testimonial.TestimonialRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class TestimonialUseCase {
    private final TestimonialRepositoryPort port;


    public Testimonial creer(Testimonial t) {
        t.initialiserCreation();
        return port.save(t);
    }

    public Testimonial togglePublication(Long id) {
        Testimonial t = port.findById(id).orElseThrow(() -> new RuntimeException("Introuvable"));
        t.togglePublication();
        return port.save(t);
    }

    public void supprimer(Long id) {
        port.deleteById(id);
    }



    public Page<Testimonial> listerPublies(int page, int size) {
        return port.findByPublieTrue(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation")));
    }

    public Page<Testimonial> listerTous(int page, int size) {
        return port.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation")));
    }
}