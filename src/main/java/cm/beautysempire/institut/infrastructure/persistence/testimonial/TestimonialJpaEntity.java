// infrastructure/persistence/testimonial/TestimonialJpaEntity.java
package cm.beautysempire.institut.infrastructure.persistence.testimonial;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "testimonials")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TestimonialJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100) private String nomEtudiant;
    @Column(nullable = false, length = 100) private String formationSuivie;
    @Column(columnDefinition = "TEXT", nullable = false) private String temoignage;
    private Integer note;
    private String photoUrl;
    @Column(nullable = false) private Boolean publie;
    @Column(nullable = false, updatable = false) private LocalDateTime dateCreation;
}