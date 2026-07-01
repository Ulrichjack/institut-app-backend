package cm.beautysempire.institut.domain.testimonial;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface TestimonialRepositoryPort {
    Testimonial save(Testimonial testimonial);
    Optional<Testimonial> findById(Long id);
    void deleteById(Long id);
    Page<Testimonial> findAll(Pageable pageable);
    Page<Testimonial> findByPublieTrue(Pageable pageable);
    long count();
}