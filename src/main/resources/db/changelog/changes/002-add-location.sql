--liquibase formatted sql

--changeset reminder-bot:002-add-chat-users-location
ALTER TABLE chat_users ADD COLUMN latitude  DOUBLE PRECISION;
ALTER TABLE chat_users ADD COLUMN longitude DOUBLE PRECISION;

--rollback ALTER TABLE chat_users DROP COLUMN latitude;
--rollback ALTER TABLE chat_users DROP COLUMN longitude;
