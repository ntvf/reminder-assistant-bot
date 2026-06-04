--liquibase formatted sql

--changeset reminder-bot:001-create-chat-users
CREATE TABLE chat_users (
    id               BIGSERIAL PRIMARY KEY,
    chat_id          VARCHAR(255) NOT NULL,
    messenger_type   VARCHAR(50)  NOT NULL,
    timezone         VARCHAR(100) NOT NULL DEFAULT 'UTC',
    timezone_confirmed BOOLEAN    NOT NULL DEFAULT FALSE,
    language_code    VARCHAR(10),
    deleted_at       TIMESTAMP,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_chat_users_chat_id_messenger UNIQUE (chat_id, messenger_type)
);

CREATE INDEX idx_chat_users_chat_id ON chat_users(chat_id);

--rollback DROP TABLE chat_users;

--changeset reminder-bot:001-create-reminders
CREATE TABLE reminders (
    id                   BIGSERIAL PRIMARY KEY,
    chat_user_id         BIGINT       NOT NULL REFERENCES chat_users(id),
    reminder_text        VARCHAR(1000) NOT NULL,
    schedule_description VARCHAR(500),
    recurring            BOOLEAN      NOT NULL DEFAULT FALSE,
    cron_expression      VARCHAR(255),
    fire_at              TIMESTAMP,
    quartz_job_key       VARCHAR(255),
    active               BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted_at           TIMESTAMP,
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_reminders_chat_user_active ON reminders(chat_user_id, active);

--rollback DROP TABLE reminders;

--changeset reminder-bot:001-create-chat-daily-limits
CREATE TABLE chat_daily_limits (
    chat_id        VARCHAR(255) NOT NULL,
    messenger_type VARCHAR(50)  NOT NULL,
    limit_date     DATE         NOT NULL,
    request_count  INTEGER      NOT NULL DEFAULT 0,
    PRIMARY KEY (chat_id, messenger_type, limit_date)
);

--rollback DROP TABLE chat_daily_limits;
