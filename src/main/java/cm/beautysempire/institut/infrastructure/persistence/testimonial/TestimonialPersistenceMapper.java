// infrastructure/persistence/testimonial/TestimonialPersistenceMapper.java
package cm.beautysempire.institut.infrastructure.persistence.testimonial;

import cm.beautysempire.institut.domain.testimonial.Testimonial;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TestimonialPersistenceMapper {
    Testimonial toDomain(TestimonialJpaEntity entity);
    TestimonialJpaEntity toEntity(Testimonial domain);
}