//package model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.antlr.v4.runtime.misc.NotNull;
//
//import java.math.BigDecimal;
//
//@Entity
//@Table(name="formations")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class Formation {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY) //a demande les autre strategy
//    private Long id;
//
//    // === INFORMATIONS DE BASE ===
//    @NotBlank(message = "Le nom de la formation est obligatoire")
//    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
//    @Column(nullable = false, length = 100)
//    private String nom;
//
//    @NotBlank(message = "La description est obligatoire")
//    @Size(min = 50, max = 2000, message = "La description doit contenir entre 50 et 2000 caractères")
//    @Column(columnDefinition = "TEXT")
//    private String description;
//
//    @NotBlank(message = "La durée est obligatoire")
//    @Column(nullable = false, length = 50)
//    private String duree; // Ex: "3 mois", "6 semaines"
//
//    @NotNull(message = "Le prix est obligatoire")
//    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal prix; // En FCFA
//
//
//    // === CATÉGORIE ET CLASSIFICATION ===
//    @NotBlank(message = "La catégorie est obligatoire")
//    @Column(nullable = false, length = 50)
//    private String categorie; // "Maquillage", "Esthétique", "Onglerie", "Coiffure"
//
//    @Column(length = 50)
//    private String niveau;
//
//
//    // === CERTIFICAT ET PRÉREQUIS ===
//    @Column(nullable = false)
//    private Boolean certificatDelivre = true;
//
//    @Column(length = 100)
//    private String nomCertificat; // "Certificat en Maquillage Professionnel IBE"
//
//    //a retirer peut etre
//    @Column(columnDefinition = "TEXT")
//    private String prerequis; // "Aucun prérequis", "Bases en esthétique recommandées"
//
//    // === CONTENU PÉDAGOGIQUE ===
//    @Column(columnDefinition = "TEXT")
//    private String programme; // Programme détaillé cours par cours
//
//    @Column(columnDefinition = "TEXT")
//    private String objectifs; // Ce que l'étudiant va apprendre
//
//    @Column(columnDefinition = "TEXT")
//    private String materielFourni; // "Kit complet maquillage, produits professionnels"
//
//
//
//
//
//}
