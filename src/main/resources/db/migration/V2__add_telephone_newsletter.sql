ALTER TABLE newsletter_subscriptions ADD COLUMN telephone VARCHAR(20);
ALTER TABLE newsletter_subscriptions ADD COLUMN contacte BOOLEAN NOT NULL DEFAULT FALSE;