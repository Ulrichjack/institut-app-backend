ALTER TABLE formations ADD COLUMN mise_en_avant BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE formations ADD COLUMN ordre_affichage INTEGER;

-- Six positions exclusives, y compris en cas de requêtes concurrentes.
ALTER TABLE formations ADD CONSTRAINT ck_formations_mise_en_avant
    CHECK ((mise_en_avant = FALSE AND ordre_affichage IS NULL)
        OR (mise_en_avant = TRUE AND active = TRUE AND ordre_affichage IS NOT NULL
            AND ordre_affichage BETWEEN 1 AND 6));

CREATE UNIQUE INDEX uk_formations_ordre_mise_en_avant
    ON formations (ordre_affichage) WHERE mise_en_avant = TRUE;
