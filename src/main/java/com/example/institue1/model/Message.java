package com.example.institue1.model;

import com.example.institue1.enums.StatutMessage;
import com.example.institue1.enums.TypeMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_message_statut_type", columnList = "statut, type"),
        @Index(name = "idx_message_email_formation", columnList = "email, formation_id"),
        @Index(name = "idx_message_date_creation", columnList = "date_creation"),
        @Index(name = "idx_message_formation_id", columnList = "formation_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === TYPE ET STATUT ===
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeMessage type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private StatutMessage statut = StatutMessage.NON_LU;

    // === INFORMATIONS CONTACT ===
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Column(nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    @Column(nullable = false, length = 150)
    private String email;


    @Column(length = 15)
    private String telephone;

    @Size(max = 50, message = "Le nom de la ville ne peut dépasser 50 caractères")
    @Column(length = 50)
    private String ville;

    // === CONTENU MESSAGE ===
    @Size(max = 100, message = "Le sujet ne peut dépasser 100 caractères")
    @Column(length = 100)
    private String sujet;

    @NotBlank(message = "Le message est obligatoire")
    @Size(min = 10, max = 2000, message = "Le message doit contenir entre 10 et 2000 caractères")
    @Column(columnDefinition = "TEXT")
    private String message;

    // === RELATION FORMATION (Approche hybride) ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id", nullable = true)
    private Formation formationInteresse;

    @Column(length = 100, name = "formation_nom_snapshot")
    private String formationNom; // Backup historique

    // === SPÉCIFIQUE PRÉ-INSCRIPTION ===
    @Column(columnDefinition = "TEXT")
    private String disponibilites; // "Lundi-Vendredi matin", "Weekend uniquement"

    // === MÉTADONNÉES TRACKING ===
    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateLecture;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTraitement;

    @Column(length = 50)
    private String traiteParAdmin;

    @Column(length = 100)
    private String sourceVisite; // "Google", "Facebook", "Direct", etc.

    @Column(length = 45)
    private String adresseIP;

    @Column(columnDefinition = "TEXT")
    private String userAgent;

    // === NOTIFICATIONS ===
    @Column(nullable = false)
    @Builder.Default
    private Boolean emailConfirmationEnvoye = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean whatsappNotificationEnvoye = false;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateEmailConfirmation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateWhatsappNotification;

    // === LIFECYCLE CALLBACKS ===

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
}