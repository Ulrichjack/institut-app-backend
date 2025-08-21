package com.example.institue1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "whatsapp_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsAppMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String destinataire; // Numéro de téléphone du destinataire

    @Column(nullable = false, length = 50)
    private String type; // NOTIFICATION, CONFIRMATION, RAPPEL, etc.

    @Column(columnDefinition = "TEXT")
    private String contenu;

    @Column(nullable = false)
    @Builder.Default
    private Boolean envoye = false;

    @Column
    private LocalDateTime dateEnvoi;

    @Column
    private String statut; // SENT, DELIVERED, READ, FAILED

    @Column
    private String erreur; // Message d'erreur si échec

    // Relation avec Message (pour traçabilité)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    // Métadonnées
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
}