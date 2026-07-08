--liquibase formatted sql

--changeset reminder-bot:004-add-reminders-event-at
ALTER TABLE reminders ADD COLUMN event_at TIMESTAMP;

--rollback ALTER TABLE reminders DROP COLUMN event_at;
