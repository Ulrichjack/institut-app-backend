CREATE TABLE testimonials (
                              id BIGSERIAL PRIMARY KEY,
                              nom_etudiant VARCHAR(100) NOT NULL,
                              formation_suivie VARCHAR(100) NOT NULL,
                              temoignage TEXT NOT NULL,
                              note INTEGER CHECK (note >= 1 AND note <= 5),
                              photo_url VARCHAR(255),
                              publie BOOLEAN NOT NULL DEFAULT FALSE,
                              date_creation TIMESTAMP NOT NULL
);