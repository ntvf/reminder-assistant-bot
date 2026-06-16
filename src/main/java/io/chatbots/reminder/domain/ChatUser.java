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
@Table(name = "chat_users")
public class ChatUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private String chatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "messenger_type", nullable = false)
    private MessengerType messengerType;

    @Column(nullable = false)
    private String timezone = "UTC";

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "timezone_confirmed", nullable = false)
    private boolean timezoneConfirmed = false;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public ChatUser() {
    }

    public ChatUser(String chatId, MessengerType messengerType, String languageCode) {
        this.chatId = chatId;
        this.messengerType = messengerType;
        this.languageCode = languageCode;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getChatId() {
        return chatId;
    }

    public MessengerType getMessengerType() {
        return messengerType;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public boolean isTimezoneConfirmed() {
        return timezoneConfirmed;
    }

    public void setTimezoneConfirmed(boolean timezoneConfirmed) {
        this.timezoneConfirmed = timezoneConfirmed;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
