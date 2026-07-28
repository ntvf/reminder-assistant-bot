--liquibase formatted sql

--changeset reminder-bot:005-add-chat-users-source
ALTER TABLE chat_users ADD COLUMN source VARCHAR(64);
ALTER TABLE chat_users ADD COLUMN tz_hint_sent BOOLEAN NOT NULL DEFAULT FALSE;
CREATE INDEX idx_chat_users_source ON chat_users (source);

--rollback DROP INDEX idx_chat_users_source;
--rollback ALTER TABLE chat_users DROP COLUMN tz_hint_sent;
--rollback ALTER TABLE chat_users DROP COLUMN source;
