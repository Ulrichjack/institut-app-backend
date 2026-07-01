package cm.beautysempire.institut.infrastructure.persistence.testimonial;

import cm.beautysempire.institut.domain.testimonial.Testimonial;
import cm.beautysempire.institut.domain.testimonial.TestimonialRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository @RequiredArgsConstructor
public class TestimonialRepositoryAdapter implements TestimonialRepositoryPort {
    private final TestimonialJpaRepository jpaRepository;
    private final TestimonialPersistenceMapper mapper;

    @Override public Testimonial save(Testimonial t) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(t)));
    }
    @Override public Optional<Testimonial> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    @Override public void deleteById(Long id) { jpaRepository.deleteById(id); }
    @Override public Page<Testimonial> findAll(Pageable p) {
        return jpaRepository.findAll(p).map(mapper::toDomain);
    }
    @Override public Page<Testimonial> findByPublieTrue(Pageable p) {
        return jpaRepository.findByPublieTrue(p).map(mapper::toDomain);
    }
    @Override
    public long count() {
        return jpaRepository.count();
    }
}