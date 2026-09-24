ALTER TABLE email_templates
    ADD COLUMN variables JSONB NOT NULL DEFAULT '[]'::jsonb;