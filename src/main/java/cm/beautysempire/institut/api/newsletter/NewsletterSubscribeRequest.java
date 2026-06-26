// Fichier : src/main/java/cm/beautysempire/institut/api/newsletter/NewsletterSubscribeRequest.java
package cm.beautysempire.institut.api.newsletter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class NewsletterSubscribeRequest {

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Format de téléphone invalide (ex: +2376XXXXXXXX)")
    private String telephone;

    // L'email est optionnel, donc pas de @NotBlank
    private String email;
}