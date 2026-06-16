--liquibase formatted sql

--changeset reminder-bot:003-add-reminders-event-text
ALTER TABLE reminders ADD COLUMN event_text VARCHAR(1000);
UPDATE reminders SET event_text = reminder_text WHERE event_text IS NULL;

--rollback ALTER TABLE reminders DROP COLUMN event_text;
