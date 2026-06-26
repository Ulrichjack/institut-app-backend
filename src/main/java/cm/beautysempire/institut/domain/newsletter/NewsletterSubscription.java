package cm.beautysempire.institut.domain.newsletter;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterSubscription {
    private Long id;
    private String telephone;
    private String email;
    private LocalDateTime dateInscription;
    private Boolean contacte;

    public void initialiser() {
        this.dateInscription = LocalDateTime.now();
        this.contacte = false;
    }

    public void marquerCommeContacte() {
        this.contacte = true;
    }
}