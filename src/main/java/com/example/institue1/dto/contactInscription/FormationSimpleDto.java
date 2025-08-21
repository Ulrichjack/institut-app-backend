package com.example.institue1.dto.contactInscription;

import com.example.institue1.model.Formation;
import com.example.institue1.utils.FormationUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormationSimpleDto {
    private Long id;
    private String nom;
    private String categorie;
    private BigDecimal prix;
    private String duree;
    private int placesRestantes;
    private boolean active;


}