package cm.beautysempire.institut.infrastructure.config;

import cm.beautysempire.institut.application.service.FormationUseCase;
import cm.beautysempire.institut.domain.formation.Formation;
import cm.beautysempire.institut.domain.formation.FormationRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("prod") // S'exécute uniquement avec le profil prod
@RequiredArgsConstructor
@Slf4j
public class FormationSeeder implements CommandLineRunner {

    private final FormationRepositoryPort formationRepositoryPort;
    private final FormationUseCase formationUseCase;

    @Override
    public void run(String... args) {
        long nombreAvantInitialisation = formationRepositoryPort.count();
        if (nombreAvantInitialisation == 0) {
            log.info("Base de données vide : création des 23 formations et packs du catalogue initial...");
            createFormations();
        } else {
            log.info("Les formations existent déjà. Catalogue initial conservé.");
        }
        log.info("Vérification et ajout des 30 formations complémentaires de l'affiche...");
        createFormationsManquantes();
        long nombreApresInitialisation = formationRepositoryPort.count();
        log.info("Initialisation des formations terminée : {} ajoutées, {} au total en base.",
                nombreApresInitialisation - nombreAvantInitialisation, nombreApresInitialisation);
    }

    private void createFormationsManquantes() {
        // Informations issues de l'affiche ; tarifs, durées et places à renseigner par l'admin.
        creerFormationSiAbsente("Décoration intérieure", "Décoration", "decoration-interieure",
                "Découvrez les bases de l'aménagement et de la décoration des espaces intérieurs : association des couleurs, choix des matières, éclairage et disposition du mobilier.");

        creerFormationSiAbsente("Comptabilité informatisée de gestion", "Gestion", "comptabilite-informatisee-de-gestion",
                "Apprenez à utiliser des outils informatiques pour enregistrer les opérations comptables, organiser les pièces justificatives et préparer les états de suivi de gestion.");
        creerFormationSiAbsente("Comptabilité et gestion des entreprises", "Gestion", "comptabilite-et-gestion-des-entreprises",
                "Développez les bases de la comptabilité et de la gestion d'entreprise : enregistrement des opérations, suivi de trésorerie, préparation des budgets et lecture des documents comptables.");
        creerFormationSiAbsente("Secrétariat comptable", "Gestion", "secretariat-comptable",
                "Associez les compétences du secrétariat au suivi comptable : classement des documents, préparation des factures, saisie des opérations et suivi administratif des règlements.");
        creerFormationSiAbsente("Secrétariat bureautique", "Gestion", "secretariat-bureautique",
                "Développez vos compétences en traitement de texte, tableur, gestion du courrier, classement et organisation des documents pour les tâches courantes de bureau.");
        creerFormationSiAbsente("Secrétariat de direction", "Gestion", "secretariat-de-direction",
                "Apprenez à organiser les agendas, préparer les réunions, rédiger les comptes rendus et assurer le suivi des dossiers et des communications d'une direction.");
        creerFormationSiAbsente("Gestion des ressources humaines", "Gestion", "gestion-des-ressources-humaines",
                "Découvrez les principales activités des ressources humaines : gestion des dossiers du personnel, suivi des absences, recrutement et organisation de la formation des collaborateurs.");
        creerFormationSiAbsente("Marketing Digital", "Gestion", "marketing-digital",
                "Apprenez à préparer une communication en ligne, créer des contenus pour les réseaux sociaux et suivre les résultats des actions de promotion numérique.");
        creerFormationSiAbsente("Gestion des projets", "Gestion", "gestion-des-projets",
                "Découvrez les méthodes pour définir les objectifs d'un projet, planifier les activités, organiser les ressources et suivre les délais, le budget et les résultats.");

        creerFormationSiAbsente("Auxiliaire de vie (AVS)", "Santé", "auxiliaire-de-vie-avs",
                "Découvrez l'accompagnement des personnes dans les activités de la vie quotidienne, avec une attention portée à l'autonomie, à l'écoute, à l'hygiène et au respect de la personne.");
        creerFormationSiAbsente("Assistant en cabinet médical (ACM)", "Santé", "assistant-en-cabinet-medical-acm",
                "Découvrez l'accueil en cabinet médical, l'organisation des rendez-vous, le suivi administratif des dossiers et les principes de confidentialité et d'hygiène.");
        creerFormationSiAbsente("Vendeur en pharmacie", "Santé", "vendeur-en-pharmacie",
                "Découvrez l'accueil des clients, l'organisation des stocks et le suivi des produits en pharmacie, ainsi que les limites du rôle de vendeur et l'orientation vers le pharmacien.");
        creerFormationSiAbsente("Délégué médical", "Santé", "delegue-medical",
                "Développez les bases de la communication professionnelle pour présenter des produits de santé, préparer les visites et assurer le suivi des échanges avec les professionnels du secteur.");
        creerFormationSiAbsente("Assistant en kinésithérapie", "Santé", "assistant-en-kinesitherapie",
                "Découvrez l'accueil des patients, l'organisation de l'espace et du matériel ainsi que l'assistance au fonctionnement d'un cabinet sous la supervision du kinésithérapeute.");
        creerFormationSiAbsente("Secrétariat médical", "Santé", "secretariat-medical",
                "Apprenez à accueillir les patients, organiser les rendez-vous et gérer les documents et dossiers médicaux dans le respect de la confidentialité.");
        creerFormationSiAbsente("Assistant dentaire", "Santé", "assistant-dentaire",
                "Découvrez l'organisation d'un cabinet dentaire, l'accueil des patients, la préparation du matériel et les principes d'hygiène dans l'assistance au praticien.");
        creerFormationSiAbsente("Aide-soignante", "Santé", "aide-soignante",
                "Découvrez les bases de l'accompagnement quotidien des patients, de l'hygiène et du confort, ainsi que la communication et le travail au sein d'une équipe soignante.");
        creerFormationSiAbsente("Sciences infirmières", "Santé", "sciences-infirmieres",
                "Explorez les fondements des sciences infirmières : observation, hygiène, prévention, relation avec le patient et organisation du travail en équipe de soins.");
        creerFormationSiAbsente("Technicien de laboratoire", "Santé", "technicien-de-laboratoire",
                "Découvrez l'environnement du laboratoire, la préparation du matériel, l'identification des échantillons et les principes de qualité, de traçabilité et d'hygiène.");
        creerFormationSiAbsente("Sage-femme", "Santé", "sage-femme",
                "Découvrez les domaines de la santé maternelle et néonatale : accompagnement de la grossesse, de la naissance et du suivi après l'accouchement, prévention et relation avec les familles.");

        creerFormationSiAbsente("Infographie", "Informatique", "infographie",
                "Développez les bases de la création graphique : composition, typographie, choix des couleurs et préparation de visuels pour les supports imprimés et numériques.");
        creerFormationSiAbsente("Montage audiovisuel", "Informatique", "montage-audiovisuel",
                "Apprenez à organiser les séquences, assembler les images et le son, ajouter des titres et préparer l'export d'un projet audiovisuel.");
        creerFormationSiAbsente("Réseau informatique", "Informatique", "reseau-informatique",
                "Découvrez les composants d'un réseau informatique, les bases de la configuration des équipements, du partage des ressources et du diagnostic des problèmes de connexion.");
        creerFormationSiAbsente("Génie logiciel", "Informatique", "genie-logiciel",
                "Découvrez les étapes de création d'un logiciel : analyse des besoins, conception, programmation, tests, documentation et maintenance des applications.");

        creerFormationSiAbsente("Couture professionnelle", "Industrie", "couture-professionnelle",
                "Développez les bases de la couture : prise de mesures, préparation des pièces, utilisation de la machine, assemblage et réalisation des finitions.");
        creerFormationSiAbsente("Stylisme modélisme", "Industrie", "stylisme-modelisme",
                "Découvrez la conception de vêtements, du dessin de mode au patron : choix des matières, construction des modèles, adaptation des coupes et préparation des prototypes.");
        creerFormationSiAbsente("Haute couture", "Industrie", "haute-couture",
                "Explorez les techniques de confection soignée : travail des volumes, ajustement du vêtement, assemblage précis et finitions réalisées avec une attention particulière aux détails.");
        creerFormationSiAbsente("Électricité", "Industrie", "electricite",
                "Découvrez les bases des circuits électriques, la lecture des schémas, les équipements et les principes de sécurité associés aux travaux d'installation et de maintenance.");
        creerFormationSiAbsente("Restauration", "Industrie", "restauration",
                "Découvrez l'organisation du travail en restauration, la préparation culinaire, les règles d'hygiène alimentaire et les bases du service et de la présentation des plats.");
        creerFormationSiAbsente("Élevage", "Industrie", "elevage",
                "Découvrez les bases de la conduite d'un élevage : organisation des installations, alimentation, hygiène, suivi des animaux et gestion courante de l'activité.");
    }

    private void creerFormationSiAbsente(String nom, String categorie, String slug, String description) {
        if (formationRepositoryPort.existsByNom(nom) || formationRepositoryPort.findBySlug(slug).isPresent()) {
            return;
        }
        formationUseCase.creerFormation(Formation.builder()
                .nom(nom)
                .categorie(categorie)
                .description(description)
                .slug(slug)
                .build(), "system");
    }

    private void createFormations() {
        // ==========================================
        // 1. LES PACKS BEAUTÉ
        // ==========================================

        formationUseCase.creerFormation(Formation.builder()
                .nom("PACK 1 : Onglerie + Make-up")
                .description("Pack combiné : prothésie ongulaire + maquillage professionnel. 2 mois + stage, kit complet, pratique intensive. Idéal pour démarrer une activité polyvalente et répondre à la demande du marché camerounais.")
                .duree("2 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(85000))
                .categorie("Pack Beauté")
                .certificatDelivre(true)
                .nomCertificat("Certificat pack beauté")
                .programme("Onglerie, maquillage, pratique, stage.")
                .objectifs("Maîtriser les deux compétences les plus demandées : ongles et make-up.")
                .materielFourni("Kit pack beauté inclus.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("3 fois par semaine")
                .nombrePlaces(40)
                .nombreInscritsAffiche(13)
                .socialProofActif(true)
                .photoPrincipale("pack1.jpg")
                .photosGalerie(List.of("pack1_1.jpg", "pack1_2.jpg"))
                .metaTitle("Pack Onglerie Make-up")
                .metaDescription("Pack complet Onglerie + Make-up, pratique, kit et stage inclus.")
                .slug("pack1-onglerie-makeup")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("PACK 2 : Esthétique Professionnelle + Onglerie + Make-up")
                .description("Pack complet 6 mois : esthétique, onglerie, make-up, stage inclus. Idéal pour devenir polyvalent(e) et accompagner une clientèle diverse.")
                .duree("6 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(270000))
                .categorie("Pack Beauté")
                .certificatDelivre(true)
                .nomCertificat("Certificat pack beauté complet")
                .programme("Esthétique, onglerie, make-up, pratique, stage.")
                .objectifs("Acquérir toutes les compétences essentielles du secteur beauté.")
                .materielFourni("Kit complet beauté.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("3 fois par semaine")
                .nombrePlaces(36)
                .nombreInscritsAffiche(11)
                .socialProofActif(true)
                .photoPrincipale("pack2.jpg")
                .photosGalerie(List.of("pack2_1.jpg", "pack2_2.jpg"))
                .metaTitle("Pack Beauté Complet")
                .metaDescription("Pack complet esthétique, onglerie, make-up avec stage inclus.")
                .slug("pack2-esthetique-onglerie-makeup")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("PACK 3 : Esthétique Pro + Coiffure Pro + Onglerie + Make-up")
                .description("Pack 12 mois : tout métier de la beauté : esthétique, coiffure pro, onglerie, make-up, stage inclus. Formation complète, pratique, kit fourni.")
                .duree("12 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(465000))
                .categorie("Pack Beauté")
                .certificatDelivre(true)
                .nomCertificat("Certificat pack beauté excellence")
                .programme("Esthétique, coiffure pro, onglerie, make-up, pratique, stage.")
                .objectifs("Devenir expert(e) beauté, maîtriser tous les métiers du secteur.")
                .materielFourni("Kit excellence beauté.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("3 fois par semaine")
                .nombrePlaces(30)
                .nombreInscritsAffiche(6)
                .socialProofActif(true)
                .photoPrincipale("pack3.jpg")
                .photosGalerie(List.of("pack3_1.jpg", "pack3_2.jpg"))
                .metaTitle("Pack Beauté Excellence")
                .metaDescription("Pack complet, tous métiers beauté, pratique et stage inclus.")
                .slug("pack3-beaute-excellence")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("PACK 4 : Coiffure Pro Femme + Onglerie + Make-up")
                .description("Pack 6 mois : coiffure pro femme, onglerie, make-up, stage inclus. Pratique intensive, kit fourni, accompagnement pro.")
                .duree("6 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(245000))
                .categorie("Pack Beauté")
                .certificatDelivre(true)
                .nomCertificat("Certificat pack coiffure+onglerie+makeup")
                .programme("Coiffure pro femme, onglerie, make-up, pratique, stage.")
                .objectifs("Devenir polyvalent(e), maîtriser coiffure, ongles et maquillage.")
                .materielFourni("Kit pack inclus.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("3 fois par semaine")
                .nombrePlaces(32)
                .nombreInscritsAffiche(9)
                .socialProofActif(true)
                .photoPrincipale("pack4.jpg")
                .photosGalerie(List.of("pack4_1.jpg", "pack4_2.jpg"))
                .metaTitle("Pack Coiffure Onglerie Make-up")
                .metaDescription("Pack complet, coiffure femme, onglerie, make-up, pratique et stage inclus.")
                .slug("pack4-coiffure-onglerie-makeup")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("PACK 5 DQP : Esthétique Professionnelle + Cosmétique")
                .description("Pack DQP : esthétique professionnelle et cosmétique, 12 mois, 2 mois de stage, kit inclus. Formation certifiante, pratique, suivi professionnel.")
                .duree("12 mois")
                .fraisInscription(BigDecimal.valueOf(30000))
                .prix(BigDecimal.valueOf(495000))
                .categorie("Pack DQP")
                .certificatDelivre(true)
                .nomCertificat("Certificat DQP Beauté Cosmétique")
                .programme("Esthétique professionnelle, cosmétique, pratique, stage.")
                .objectifs("Obtenir le diplôme DQP, devenir expert(e) beauté/cosmétique, ouvrir son entreprise.")
                .materielFourni("Kit DQP complet.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("3 fois par semaine")
                .nombrePlaces(20)
                .nombreInscritsAffiche(4)
                .socialProofActif(true)
                .photoPrincipale("pack5.jpg")
                .photosGalerie(List.of("pack5_1.jpg", "pack5_2.jpg"))
                .metaTitle("Pack DQP Beauté Cosmétique")
                .metaDescription("Pack DQP complet : esthétique, cosmétique, stage inclus.")
                .slug("pack5-dqp-beaute-cosmetique")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("PACK 6 DQP : Coiffure Pro Femme + Coiffure Homme + Perruques + Customisation")
                .description("Pack DQP 12 mois : coiffure pro femme, coiffure homme, confection perruques, customisation, stage inclus. Formation certifiante, pratique intensive, kit fourni.")
                .duree("12 mois")
                .fraisInscription(BigDecimal.valueOf(30000))
                .prix(BigDecimal.valueOf(455000))
                .categorie("Pack DQP")
                .certificatDelivre(true)
                .nomCertificat("Certificat DQP Coiffure complète")
                .programme("Coiffure femme, homme, perruques, customisation, pratique, stage.")
                .objectifs("Obtenir le DQP coiffure complète, ouvrir son salon, développer activité perruques.")
                .materielFourni("Kit DQP coiffure complet.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("3 fois par semaine")
                .nombrePlaces(18)
                .nombreInscritsAffiche(2)
                .socialProofActif(true)
                .photoPrincipale("pack6.jpg")
                .photosGalerie(List.of("pack6_1.jpg", "pack6_2.jpg"))
                .metaTitle("Pack DQP Coiffure complète")
                .metaDescription("Pack DQP, coiffure femme/homme, perruques, customisation, stage inclus.")
                .slug("pack6-dqp-coiffure-complete")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("PACK 7 : Onglerie + Make-up + Pose lace Frontale")
                .description("Pack 3 mois : ongles, make-up, pose lace frontale, stage inclus. Formation pratique, kit complet, encadrement pro.")
                .duree("3 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(125000))
                .categorie("Pack Beauté")
                .certificatDelivre(true)
                .nomCertificat("Certificat pack beauté 3-en-1")
                .programme("Onglerie, make-up, pose lace, pratique, stage.")
                .objectifs("Devenir polyvalent(e) : beauté, coiffure, ongles, maquillage.")
                .materielFourni("Kit pack inclus.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("3 fois par semaine")
                .nombrePlaces(19)
                .nombreInscritsAffiche(3)
                .socialProofActif(true)
                .photoPrincipale("pack7.jpg")
                .photosGalerie(List.of("pack7_1.jpg", "pack7_2.jpg"))
                .metaTitle("Pack Onglerie Make-up Lace")
                .metaDescription("Pack complet ongles, make-up, lace frontale, stage inclus.")
                .slug("pack7-onglerie-makeup-lace")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("PACK 8 : Onglerie + Make-up + Extensions Cils + Microblading + Microshading")
                .description("Pack 3 mois : ongles, make-up, extensions cils, microblading, microshading, stage inclus. Formation ultra-complète, kit fourni, encadrement professionnel.")
                .duree("3 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(255000))
                .categorie("Pack Beauté")
                .certificatDelivre(true)
                .nomCertificat("Certificat pack beauté expert")
                .programme("Onglerie, make-up, extensions cils, microblading, microshading, pratique, stage.")
                .objectifs("Devenir expert(e) beauté, maîtriser toutes les tendances, développer son activité.")
                .materielFourni("Kit ultra complet inclus.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("3 fois par semaine")
                .nombrePlaces(15)
                .nombreInscritsAffiche(2)
                .socialProofActif(true)
                .photoPrincipale("pack8.jpg")
                .photosGalerie(List.of("pack8_1.jpg", "pack8_2.jpg"))
                .metaTitle("Pack Beauté Expert")
                .metaDescription("Pack complet ongles, make-up, extensions cils, microblading, microshading, stage inclus.")
                .slug("pack8-beaute-expert")
                .build(), "system");

        // ==========================================
        // 2. DQP, CQP, AQP
        // ==========================================

        formationUseCase.creerFormation(Formation.builder()
                .nom("DQP Coiffure Professionnelle")
                .description("DQP Coiffure Pro : 12 mois, pratique, gestion salon, techniques avancées, stage, kit fourni. Formation certifiante, reconnue nationalement.")
                .duree("12 mois")
                .fraisInscription(BigDecimal.valueOf(30000))
                .prix(BigDecimal.valueOf(455000))
                .categorie("DQP")
                .certificatDelivre(true)
                .nomCertificat("Diplôme DQP Coiffure Pro")
                .programme("Techniques avancées, gestion salon, pratique, stage, certification DQP.")
                .objectifs("Obtenir le DQP coiffure pro, ouvrir salon, devenir manager beauté.")
                .materielFourni("Kit DQP coiffure.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("3 fois par semaine")
                .nombrePlaces(12)
                .nombreInscritsAffiche(1)
                .socialProofActif(true)
                .photoPrincipale("dqpcf.jpg")
                .photosGalerie(List.of("dqpcf1.jpg", "dqpcf2.jpg"))
                .metaTitle("DQP Coiffure Pro")
                .metaDescription("Diplôme DQP coiffure pro, pratique, gestion salon, stage inclus.")
                .slug("dqp-coiffure-pro")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("DQP Esthétique Cosmétique")
                .description("DQP Esthétique Cosmétique : 12 mois, fabrication produits, soins, gestion clientèle, pratique, stage, kit inclus. Formation certifiante, reconnue nationalement.")
                .duree("12 mois")
                .fraisInscription(BigDecimal.valueOf(30000))
                .prix(BigDecimal.valueOf(495000))
                .categorie("DQP")
                .certificatDelivre(true)
                .nomCertificat("Diplôme DQP Esthétique Cosmétique")
                .programme("Esthétique, cosmétique, création produits, gestion clientèle, stage DQP.")
                .objectifs("Obtenir le DQP, créer gamme de produits, devenir expert(e) beauté/cosmétique.")
                .materielFourni("Kit DQP esthétique cosmétique.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("3 fois par semaine")
                .nombrePlaces(12)
                .nombreInscritsAffiche(1)
                .socialProofActif(true)
                .photoPrincipale("dqpec.jpg")
                .photosGalerie(List.of("dqpec1.jpg", "dqpec2.jpg"))
                .metaTitle("DQP Esthétique Cosmétique")
                .metaDescription("Diplôme DQP esthétique cosmétique, fabrication produits, stage inclus.")
                .slug("dqp-esthetique-cosmetique")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("PACK DQP/AQP/CQP Complet")
                .description("Le pack ultime : Onglerie, Make-up, Coiffure Pro Femme, Esthétique Pro, Cosmétique, Extensions Cils, Microblading, Microshading, 14 mois + 2 mois suivi. Formation certifiante, kit ultra-complet, pratique, stage, accompagnement pro.")
                .duree("14 mois")
                .fraisInscription(BigDecimal.valueOf(30000))
                .prix(BigDecimal.valueOf(695000))
                .categorie("DQP")
                .certificatDelivre(true)
                .nomCertificat("Diplôme PACK DQP/AQP/CQP Complet")
                .programme("Onglerie, make-up, coiffure, esthétique, cosmétique, extensions cils, microblading, microshading, suivi, stage.")
                .objectifs("Devenir expert(e) beauté, obtenir tous les diplômes, ouvrir son entreprise.")
                .materielFourni("Kit ultra complet pour tous modules.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("3 fois par semaine")
                .nombrePlaces(10)
                .nombreInscritsAffiche(1)
                .socialProofActif(true)
                .photoPrincipale("packdqp.jpg")
                .photosGalerie(List.of("packdqp1.jpg", "packdqp2.jpg"))
                .metaTitle("Pack DQP/AQP/CQP Complet")
                .metaDescription("Pack complet, tous les diplômes beauté, pratique et stage inclus, suivi pro.")
                .slug("pack-dqp-aqp-cqp-complet")
                .build(), "system");

        // ==========================================
        // 3. FORMATIONS INDIVIDUELLES
        // ==========================================

        formationUseCase.creerFormation(Formation.builder()
                .nom("Onglerie")
                .description("Maîtrisez la prothésie ongulaire : capsules, gel, polygel, résine, vernis, nail art et hygiène professionnelle. Formation pratique et théorique sur 2 mois, accompagnée de matériel professionnel et d'un suivi individualisé.")
                .duree("2 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(60000))
                .categorie("Esthétique")
                .certificatDelivre(true)
                .nomCertificat("Certificat de prothésie ongulaire")
                .programme("Anatomie de l'ongle, hygiène, pose capsules, gel, résine, polygel, vernis, nail art, entretien, gestion clientèle.")
                .objectifs("Devenir prothésiste ongulaire, maîtriser toutes les techniques modernes et traditionnelles.")
                .materielFourni("Kit complet : gels, limes, lampe UV, capsules, outils de décoration.")
                .horaires("Lundi-Vendredi 10h30-13h30")
                .frequence("3 fois par semaine")
                .nombrePlaces(35)
                .nombreInscritsAffiche(8)
                .socialProofActif(true)
                .photoPrincipale("onglerie.jpg")
                .photosGalerie(List.of("onglerie1.jpg", "onglerie2.jpg"))
                .metaTitle("Formation Onglerie Professionnelle")
                .metaDescription("Devenez prothésiste ongulaire avec notre formation complète et certifiée.")
                .slug("onglerie")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("Make-up Professionnel")
                .description("Développez votre expertise en maquillage professionnel : bases, techniques avancées, maquillage de jour, soirée, mariage, artistique. Formation de 2 mois, encadrée par des maquilleuses expérimentées, pratique sur modèles, kit inclus.")
                .duree("2 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(45000))
                .categorie("Esthétique")
                .certificatDelivre(true)
                .nomCertificat("Certificat de maquillage professionnel")
                .programme("Soin de la peau, teint, yeux, lèvres, cils, maquillage artistique, mariée, stage pratique.")
                .objectifs("Maîtriser les techniques de maquillage, du naturel à l'artistique, et développer une clientèle fidèle.")
                .materielFourni("Kit complet : pinceaux, palettes, fonds de teint, accessoires.")
                .horaires("Lundi-Vendredi 08h00-10h30")
                .frequence("3 fois par semaine")
                .nombrePlaces(40)
                .nombreInscritsAffiche(17)
                .socialProofActif(true)
                .photoPrincipale("makeup.jpg")
                .photosGalerie(List.of("makeup1.jpg", "makeup2.jpg"))
                .metaTitle("Formation Make-up Professionnel")
                .metaDescription("Maîtrisez les techniques de maquillage professionnel, artistique et événementiel.")
                .slug("make-up-professionnel")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("Extension des cils")
                .description("Maîtrisez la pose d’extensions de cils et le regard parfait. Formation de 2 mois + stage, pratique sur modèles, hygiène, conseils client, kit complet fourni.")
                .duree("2 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(95000))
                .categorie("Esthétique")
                .certificatDelivre(true)
                .nomCertificat("Certificat extension des cils")
                .programme("Pose extensions, entretien, hygiène, pratique sur modèles, stage professionnel.")
                .objectifs("Maîtriser toutes les techniques d’extension pour un résultat naturel et durable.")
                .materielFourni("Kit cils, pinces, colle, patchs, accessoires.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("2 fois par semaine")
                .nombrePlaces(25)
                .nombreInscritsAffiche(7)
                .socialProofActif(true)
                .photoPrincipale("cils.jpg")
                .photosGalerie(List.of("cils1.jpg", "cils2.jpg"))
                .metaTitle("Formation Extension des cils")
                .metaDescription("Formation complète à l’extension des cils avec stage et kit inclus.")
                .slug("extension-des-cils")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("Extension des cils + Microblading + Microshading")
                .description("Pack expert du regard : extension de cils, microblading, microshading. 3 mois de formation + stage, pratique intensive, matériel professionnel, accompagnement individuel.")
                .duree("3 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(205000))
                .categorie("Esthétique")
                .certificatDelivre(true)
                .nomCertificat("Certificat expert regard")
                .programme("Extension cils, microblading, microshading, hygiène, pratique intensive, stage.")
                .objectifs("Maîtriser et combiner toutes les techniques du regard : cils et sourcils.")
                .materielFourni("Kit regard pro : extension cils, pigments, outils microblading/microshading.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("2 fois par semaine")
                .nombrePlaces(20)
                .nombreInscritsAffiche(5)
                .socialProofActif(true)
                .photoPrincipale("pack-regard.jpg")
                .photosGalerie(List.of("packregard1.jpg", "packregard2.jpg"))
                .metaTitle("Pack Extension cils Microblading Microshading")
                .metaDescription("Pack expert du regard, formations extension de cils, microblading et microshading.")
                .slug("pack-extension-cils-microblading-microshading")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("Coiffure Homme")
                .description("Formation Coiffure Homme : toutes les techniques de coupe, coiffure moderne et classique, hygiène, gestion clientèle. 3 mois + stage, pratique en salon, matériel fourni.")
                .duree("3 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(155000))
                .categorie("Coiffure")
                .certificatDelivre(true)
                .nomCertificat("Certificat Coiffure Homme")
                .programme("Coupes modernes, coiffure classique, entretien, hygiène, gestion clientèle, stage en salon.")
                .objectifs("Maîtriser la coiffure masculine, devenir professionnel en salon ou à domicile.")
                .materielFourni("Kit coiffure homme : peignes, tondeuses, ciseaux, produits coiffants.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("2 fois par semaine")
                .nombrePlaces(30)
                .nombreInscritsAffiche(10)
                .socialProofActif(true)
                .photoPrincipale("coiffurehomme.jpg")
                .photosGalerie(List.of("coiffurehomme1.jpg", "coiffurehomme2.jpg"))
                .metaTitle("Formation Coiffure Homme")
                .metaDescription("Techniques et tendances de coiffure masculine, avec stage en salon.")
                .slug("coiffure-homme")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("Massage")
                .description("Formation Massage : techniques de relaxation, huiles, hygiène, pratique sur modèles, conseils bien-être. 2 mois + stage, matériel fourni, encadrement professionnel.")
                .duree("2 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(75000))
                .categorie("Bien-être")
                .certificatDelivre(true)
                .nomCertificat("Certificat Massage Bien-être")
                .programme("Techniques de massage, huiles, relaxation, hygiène, pratique, stage.")
                .objectifs("Devenir praticien(ne) massage, offrir détente et bien-être à ses clients.")
                .materielFourni("Kit massage : huiles, serviettes, accessoires.")
                .horaires("Lundi-Vendredi 10h00-13h00")
                .frequence("2 fois par semaine")
                .nombrePlaces(28)
                .nombreInscritsAffiche(6)
                .socialProofActif(true)
                .photoPrincipale("massage.jpg")
                .photosGalerie(List.of("massage1.jpg", "massage2.jpg"))
                .metaTitle("Formation Massage")
                .metaDescription("Techniques de massage et relaxation, formation pratique avec kit inclus.")
                .slug("massage")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("Pose lace Frontale")
                .description("Apprenez la pose et la customisation de lace frontale, coloration, entretien, gestion des coiffures tendances. 2 mois (1 mois cours + 1 mois stage), matériel fourni, encadrement professionnel.")
                .duree("2 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(60000))
                .categorie("Coiffure")
                .certificatDelivre(true)
                .nomCertificat("Certificat Pose Lace Frontale")
                .programme("Pose lace, customisation, coloration, entretien, pratique, stage.")
                .objectifs("Maîtriser la pose lace frontale, création de coiffures tendances et naturelles.")
                .materielFourni("Kit lace frontale : tulle, lace, produits coiffants, accessoires.")
                .horaires("Lundi-Vendredi 10h00-13h00")
                .frequence("2 fois par semaine")
                .nombrePlaces(22)
                .nombreInscritsAffiche(5)
                .socialProofActif(true)
                .photoPrincipale("lace.jpg")
                .photosGalerie(List.of("lace1.jpg", "lace2.jpg"))
                .metaTitle("Formation Pose Lace Frontale")
                .metaDescription("Apprenez la pose lace frontale et la customisation coiffure.")
                .slug("pose-lace-frontale")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("Décoration événementielle")
                .description("Formation Décoration événementielle : organisation et décoration de mariages, anniversaires, cérémonies, promotions, etc. 6 mois + stage, pratique sur projets réels, matériel fourni, gestion du devis et du client.")
                .duree("6 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(255000))
                .categorie("Décoration")
                .certificatDelivre(true)
                .nomCertificat("Certificat Décoration événementielle")
                .programme("Décoration salle/extérieure, cérémonies, anniversaires, devis, gestion client, pratique, stage.")
                .objectifs("Devenir décorateur(trice) événementiel(le), créer et vendre ses services.")
                .materielFourni("Kit décoration : tissus, accessoires, outils de création.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("2 fois par semaine")
                .nombrePlaces(30)
                .nombreInscritsAffiche(7)
                .socialProofActif(true)
                .photoPrincipale("deco.jpg")
                .photosGalerie(List.of("deco1.jpg", "deco2.jpg"))
                .metaTitle("Formation Décoration événementielle")
                .metaDescription("Organisation et décoration de mariages, cérémonies, promotions, stage inclus.")
                .slug("decoration-evenementielle")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("Confection des perruques + Pose laces + Traitement + Coloration + Customisation")
                .description("Formation complète en perruquerie : confection à la main et à la machine, pose laces frontales, traitement, coloration, customisation. 3 mois + stage, pratique intensive, matériel pro fourni.")
                .duree("3 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(255000))
                .categorie("Coiffure")
                .certificatDelivre(true)
                .nomCertificat("Certificat perruquerie pro")
                .programme("Confection main/machine, lace, coloration, entretien, customisation, stage.")
                .objectifs("Maîtriser la fabrication et la customisation de perruques, développer une activité rentable.")
                .materielFourni("Kit perruquerie : bonnet, tulle, lace, machine, accessoires.")
                .horaires("Lundi-Vendredi 10h00-13h00")
                .frequence("2 fois par semaine")
                .nombrePlaces(24)
                .nombreInscritsAffiche(6)
                .socialProofActif(true)
                .photoPrincipale("perruque.jpg")
                .photosGalerie(List.of("perruque1.jpg", "perruque2.jpg"))
                .metaTitle("Formation Perruquerie complète")
                .metaDescription("Confection, pose, traitement, coloration, customisation perruques.")
                .slug("perruque-complete")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("Coiffure Professionnelle Femme")
                .description("Formation Coiffure Pro Femme : tresses, chignons, brushing, coloration, coupe, gestion clientèle. 5 mois + stage, pratique sur modèles, matériel fourni, accompagnement pro.")
                .duree("5 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(185000))
                .categorie("Coiffure")
                .certificatDelivre(true)
                .nomCertificat("Certificat Coiffure Femme")
                .programme("Tresses, chignons, brushing, coloration, coupe, gestion clientèle, stage.")
                .objectifs("Maîtriser toutes les techniques de coiffure pour femmes, travailler en salon ou à domicile.")
                .materielFourni("Kit coiffure femme : peignes, tresses, produits, accessoires.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("2 fois par semaine")
                .nombrePlaces(32)
                .nombreInscritsAffiche(12)
                .socialProofActif(true)
                .photoPrincipale("coiffurefemme.jpg")
                .photosGalerie(List.of("coiffurefemme1.jpg", "coiffurefemme2.jpg"))
                .metaTitle("Formation Coiffure Femme")
                .metaDescription("Techniques complètes de coiffure pour femmes, pratique et stage inclus.")
                .slug("coiffure-femme")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("Esthétique Professionnelle")
                .description("Formation Esthétique : soins visage, épilation, gommage, massage, hygiène, gestion clientèle. 5 mois + stage, pratique en institut, matériel fourni, encadrement professionnel.")
                .duree("5 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(215000))
                .categorie("Esthétique")
                .certificatDelivre(true)
                .nomCertificat("Certificat Esthétique Pro")
                .programme("Soins visage, épilation, gommage, massage, hygiène, pratique, stage.")
                .objectifs("Maîtriser toutes les techniques esthétiques, travailler en institut ou à domicile.")
                .materielFourni("Kit esthétique : soins, épilateurs, produits, accessoires.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("2 fois par semaine")
                .nombrePlaces(38)
                .nombreInscritsAffiche(15)
                .socialProofActif(true)
                .photoPrincipale("esthetique.jpg")
                .photosGalerie(List.of("esthetique1.jpg", "esthetique2.jpg"))
                .metaTitle("Formation Esthétique Professionnelle")
                .metaDescription("Soins visage, épilation, massage, gommage, hygiène, stage inclus.")
                .slug("esthetique-professionnelle")
                .build(), "system");

        formationUseCase.creerFormation(Formation.builder()
                .nom("Cosmétiques")
                .description("Formation Cosmétique : fabrication de savons, laits, lotions, huiles, gels, suivi entreprise, pratique et stage. 5 mois + 2 mois de suivi, matériel fourni, accompagnement pro.")
                .duree("5 mois")
                .fraisInscription(BigDecimal.valueOf(15000))
                .prix(BigDecimal.valueOf(350000))
                .categorie("Cosmétiques")
                .certificatDelivre(true)
                .nomCertificat("Certificat Cosmétique Pro")
                .programme("Fabrication savon, lait, lotion, huile, gel, suivi entreprise, stage.")
                .objectifs("Maîtriser la fabrication cosmétique, créer son entreprise, travailler en laboratoire.")
                .materielFourni("Kit cosmétique : produits, accessoires, outils de fabrication.")
                .horaires("Lundi-Vendredi 08h00-13h30")
                .frequence("2 fois par semaine")
                .nombrePlaces(20)
                .nombreInscritsAffiche(4)
                .socialProofActif(true)
                .photoPrincipale("cosmetique.jpg")
                .photosGalerie(List.of("cosmetique1.jpg", "cosmetique2.jpg"))
                .metaTitle("Formation Cosmétique Pro")
                .metaDescription("Fabrication de produits cosmétiques, suivi entreprise, stage inclus.")
                .slug("cosmetiques")
                .build(), "system");
    }
}
