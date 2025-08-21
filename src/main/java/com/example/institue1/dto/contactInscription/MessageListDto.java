package com.example.institue1.dto.contactInscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageListDto {
    private Long id;
    private String type;
    private String statut;
    private String nom;
    private String email;
    private String telephone;
    private String sujet;
    private String formationNom;
    private LocalDateTime dateCreation;
    private LocalDateTime dateLecture;
    private String traiteParAdmin;

    // Champs calculés
    private boolean urgent;
    private long ageEnHeures;
    private int priorite;



}