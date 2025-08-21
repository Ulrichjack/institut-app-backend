package com.example.institue1.dto.formation;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialProofDto {

    private Long formationId;

    @Min(value = 0, message = "Le nombre d'inscrits affiché ne peut être négatif")
    private Integer nombreInscritsAffiche;

    private Boolean socialProofActif;

    // Données calculées en lecture seule
    private Integer nombreInscritsReel;
    private Integer placesRestantesReelles;
    private Integer placesRestantesAffichees;
    private String messageSocialProof;
    private Double tauxRemplissageReel;
    private Double tauxRemplissageAffiche;
}