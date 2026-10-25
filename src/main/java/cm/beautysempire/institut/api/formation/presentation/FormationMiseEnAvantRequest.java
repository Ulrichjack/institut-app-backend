package cm.beautysempire.institut.api.formation.presentation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FormationMiseEnAvantRequest(
        @NotNull(message = "Le choix de mise en avant est obligatoire") Boolean miseEnAvant,
        @Min(value = 1, message = "La position doit être comprise entre 1 et 6")
        @Max(value = 6, message = "La position doit être comprise entre 1 et 6") Integer ordreAffichage) {
}
