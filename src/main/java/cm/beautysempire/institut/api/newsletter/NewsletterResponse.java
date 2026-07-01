package cm.beautysempire.institut.api.newsletter;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NewsletterResponse {
    private Long id;
    private String telephone;
    private String email;
    private LocalDateTime dateInscription;
    private Boolean contacte;
    private String whatsappCatalogueLink;
}