package cm.beautysempire.institut.infrastructure.persistence.message;

import cm.beautysempire.institut.domain.messages.StatutMessage;
import cm.beautysempire.institut.domain.messages.TypeMessage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeMessage type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatutMessage statut;

    private String nom;
    private String email;
    private String telephone;
    private String ville;

    private String sujet;
    private String message;
    private String disponibilites;

    @Column(name = "formation_id")
    private Long formationId;

    @Column(name = "formation_nom_snapshot")
    private String formationNom;

    private LocalDateTime dateCreation;
    private LocalDateTime dateLecture;
    private LocalDateTime dateTraitement;
    private String traiteParAdmin;
    private String sourceVisite;
    private String adresseIp;
    private String userAgent;

    private Boolean emailConfirmationEnvoye;
    private Boolean whatsappNotificationEnvoye;
    private LocalDateTime dateEmailConfirmation;
    private LocalDateTime dateWhatsappNotification;
}