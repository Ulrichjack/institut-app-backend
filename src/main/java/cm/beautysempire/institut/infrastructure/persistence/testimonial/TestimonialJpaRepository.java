// infrastructure/persistence/testimonial/TestimonialJpaRepository.java
package cm.beautysempire.institut.infrastructure.persistence.testimonial;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestimonialJpaRepository extends JpaRepository<TestimonialJpaEntity, Long> {
    Page<TestimonialJpaEntity> findByPublieTrue(Pageable pageable);
}