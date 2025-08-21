package com.example.institue1.dto.formation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormationSimpleDto {
    private Long id;
    private String nom;
    private String categorie;
    private BigDecimal prix;
    private String duree;
    private Boolean active;
    private String slug;

    // Constructeur pour éviter les requêtes N+1
    public FormationSimpleDto(Long id, String nom, String categorie) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
    }
}
