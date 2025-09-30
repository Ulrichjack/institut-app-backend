package com.example.institue1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "newsletter_subscriptions", indexes = {
        @Index(name = "idx_newsletter_email", columnList = "email")
})
@Data
@NoArgsConstructor
public class NewsletterSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateInscription = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean catalogueEnvoye = false;

    @Column
    private LocalDateTime dateCatalogueEnvoye;
}