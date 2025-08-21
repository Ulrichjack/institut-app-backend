package com.example.institue1.dto.contactInscription;

import com.example.institue1.dto.formation.FormationSimpleDto;
import com.example.institue1.model.Message;
import com.example.institue1.utils.MessageUtils;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatutDto {
    @NotNull
    private String nouveauStatut; // LU, TRAITE, ARCHIVE

    private String commentaire; // Optionnel




}
