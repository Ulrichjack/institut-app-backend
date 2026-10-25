-- Le contact exige un texte, mais la pré-inscription autorise un message facultatif.
-- La validation du formulaire de contact reste assurée par ContactCreateRequest.
ALTER TABLE messages ALTER COLUMN message DROP NOT NULL;
