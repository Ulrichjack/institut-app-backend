-- Les formations de l'affiche sont complétées ensuite par l'administrateur.
-- NULL signifie « à renseigner », sans annoncer de gratuité ni inventer de durée/capacité.
ALTER TABLE formations ALTER COLUMN prix DROP NOT NULL;
ALTER TABLE formations ALTER COLUMN frais_inscription DROP NOT NULL;
ALTER TABLE formations ALTER COLUMN duree DROP NOT NULL;
ALTER TABLE formations ALTER COLUMN nombre_places DROP NOT NULL;
