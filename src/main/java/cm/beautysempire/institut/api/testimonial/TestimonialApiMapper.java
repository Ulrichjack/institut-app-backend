package cm.beautysempire.institut.api.testimonial;

import cm.beautysempire.institut.domain.testimonial.Testimonial;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TestimonialApiMapper {
    Testimonial toDomain(TestimonialCreateRequest request);
    TestimonialResponse toResponse(Testimonial domain);
}