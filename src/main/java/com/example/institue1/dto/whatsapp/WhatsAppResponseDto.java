package com.example.institue1.dto.whatsapp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppResponseDto {
    private Long id;
    private String telephone;
    private boolean envoye;
    private LocalDateTime dateEnvoi;
    private String statut;
    private String message;
}