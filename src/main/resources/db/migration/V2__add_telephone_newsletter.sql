ALTER TABLE newsletter_subscriptions ADD COLUMN telephone VARCHAR(20) NOT NULL;
ALTER TABLE newsletter_subscriptions ADD COLUMN contacte BOOLEAN NOT NULL DEFAULT FALSE;