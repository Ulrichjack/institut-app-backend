CREATE TABLE IF NOT EXISTS formations (
                            id BIGSERIAL PRIMARY KEY,
                            nom VARCHAR(100) NOT NULL,
                            description TEXT NOT NULL,
                            duree VARCHAR(50) NOT NULL,
                            frais_inscription DECIMAL(10,2) NOT NULL,
                            prix DECIMAL(10,2) NOT NULL,
                            categorie VARCHAR(50) NOT NULL,
                            certificat_delivre BOOLEAN NOT NULL DEFAULT TRUE,
                            nom_certificat VARCHAR(100),
                            programme TEXT,
                            objectifs TEXT,
                            materiel_fourni TEXT,
                            horaires VARCHAR(100),
                            frequence VARCHAR(50),
                            nombre_places INTEGER NOT NULL DEFAULT 15,
                            nombre_inscrits_reel INTEGER NOT NULL DEFAULT 0,
                            nombre_inscrits_affiche INTEGER NOT NULL DEFAULT 0,
                            social_proof_actif BOOLEAN NOT NULL DEFAULT FALSE,
                            photo_principale VARCHAR(255),
                            active BOOLEAN NOT NULL DEFAULT TRUE,
                            en_promotion BOOLEAN NOT NULL DEFAULT FALSE,
                            pourcentage_reduction DECIMAL(5,2) DEFAULT 0.00,
                            date_debut_promo TIMESTAMP,
                            date_fin_promo TIMESTAMP,
                            date_creation TIMESTAMP NOT NULL,
                            date_mise_a_jour TIMESTAMP,
                            cree_par_admin VARCHAR(50),
                            modifie_par VARCHAR(50),
                            nombre_vues INTEGER NOT NULL DEFAULT 0,
                            nombre_demandes_info INTEGER NOT NULL DEFAULT 0,
                            nombre_inscriptions INTEGER NOT NULL DEFAULT 0,
                            meta_title VARCHAR(200),
                            meta_description VARCHAR(300),
                            slug VARCHAR(100) UNIQUE
);

CREATE TABLE formation_photos (
                                  formation_id BIGINT NOT NULL REFERENCES formations(id),
                                  photo_url VARCHAR(255)
);

CREATE TABLE messages (
                          id BIGSERIAL PRIMARY KEY,
                          type VARCHAR(20) NOT NULL,
                          statut VARCHAR(15) NOT NULL DEFAULT 'NON_LU',
                          nom VARCHAR(100) NOT NULL,
                          email VARCHAR(150),
                          telephone VARCHAR(20),
                          ville VARCHAR(50),
                          sujet VARCHAR(100),
                          message TEXT NOT NULL,
                          formation_id BIGINT REFERENCES formations(id),
                          formation_nom_snapshot VARCHAR(100),
                          disponibilites TEXT,
                          date_creation TIMESTAMP NOT NULL,
                          date_lecture TIMESTAMP,
                          date_traitement TIMESTAMP,
                          traite_par_admin VARCHAR(50),
                          source_visite VARCHAR(100),
                          adresse_ip VARCHAR(45),
                          user_agent TEXT,
                          email_confirmation_envoye BOOLEAN NOT NULL DEFAULT FALSE,
                          whatsapp_notification_envoye BOOLEAN NOT NULL DEFAULT FALSE,
                          date_email_confirmation TIMESTAMP,
                          date_whatsapp_notification TIMESTAMP
);

CREATE TABLE gallery_images (
                                id BIGSERIAL PRIMARY KEY,
                                titre VARCHAR(255) NOT NULL,
                                description VARCHAR(500),
                                url VARCHAR(255) NOT NULL,
                                categorie VARCHAR(255) NOT NULL,
                                is_public BOOLEAN NOT NULL DEFAULT TRUE,
                                date_creation TIMESTAMP NOT NULL,
                                formation_id BIGINT REFERENCES formations(id)
);

CREATE TABLE newsletter_subscriptions (
                                          id BIGSERIAL PRIMARY KEY,
                                          email VARCHAR(200),              -- ✅ OPTIONNEL (pas de NOT NULL)
                                          date_inscription TIMESTAMP NOT NULL DEFAULT NOW()
);