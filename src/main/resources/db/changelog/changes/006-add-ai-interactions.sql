--liquibase formatted sql

--changeset reminder-bot:006-add-ai-interactions
CREATE TABLE ai_interactions (
    id BIGSERIAL PRIMARY KEY,
    chat_id VARCHAR(64) NOT NULL,
    messenger_type VARCHAR(32) NOT NULL,
    request_text TEXT,
    sanitized_text TEXT,
    language_code VARCHAR(16),
    timezone VARCHAR(64),
    response_json TEXT,
    outcome VARCHAR(32) NOT NULL,
    error_text TEXT,
    latency_ms BIGINT,
    created_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_ai_interactions_created_at ON ai_interactions (created_at);
CREATE INDEX idx_ai_interactions_chat_id ON ai_interactions (chat_id);
CREATE INDEX idx_ai_interactions_outcome ON ai_interactions (outcome);

--rollback DROP TABLE ai_interactions;
