package io.chatbots.reminder.domain;

import io.chatbots.reminder.bot.MessengerType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_interactions")
public class AiInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private String chatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "messenger_type", nullable = false)
    private MessengerType messengerType;

    @Column(name = "request_text", columnDefinition = "TEXT")
    private String requestText;

    @Column(name = "sanitized_text", columnDefinition = "TEXT")
    private String sanitizedText;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "response_json", columnDefinition = "TEXT")
    private String responseJson;

    @Column(name = "outcome", nullable = false)
    private String outcome;

    @Column(name = "error_text", columnDefinition = "TEXT")
    private String errorText;

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AiInteraction() {
    }

    public Long getId() {
        return id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public MessengerType getMessengerType() {
        return messengerType;
    }

    public void setMessengerType(MessengerType messengerType) {
        this.messengerType = messengerType;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public String getSanitizedText() {
        return sanitizedText;
    }

    public void setSanitizedText(String sanitizedText) {
        this.sanitizedText = sanitizedText;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getResponseJson() {
        return responseJson;
    }

    public void setResponseJson(String responseJson) {
        this.responseJson = responseJson;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public Long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Long latencyMs) {
        this.latencyMs = latencyMs;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
