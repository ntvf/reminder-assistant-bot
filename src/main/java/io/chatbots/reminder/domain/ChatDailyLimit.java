package io.chatbots.reminder.domain;

import io.chatbots.reminder.bot.MessengerType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "chat_daily_limits")
@IdClass(ChatDailyLimit.PK.class)
public class ChatDailyLimit {

    @Id
    @Column(name = "chat_id")
    private String chatId;

    @Id
    @Column(name = "messenger_type")
    @Enumerated(EnumType.STRING)
    private MessengerType messengerType;

    @Id
    @Column(name = "limit_date")
    private LocalDate limitDate;

    @Column(name = "request_count", nullable = false)
    private int requestCount;

    public ChatDailyLimit() {}

    public ChatDailyLimit(String chatId, MessengerType messengerType, LocalDate limitDate, int requestCount) {
        this.chatId = chatId;
        this.messengerType = messengerType;
        this.limitDate = limitDate;
        this.requestCount = requestCount;
    }

    public int getRequestCount() { return requestCount; }
    public void setRequestCount(int requestCount) { this.requestCount = requestCount; }
    public String getChatId() { return chatId; }
    public MessengerType getMessengerType() { return messengerType; }
    public LocalDate getLimitDate() { return limitDate; }

    public static class PK implements Serializable {
        private String chatId;
        private MessengerType messengerType;
        private LocalDate limitDate;

        public PK() {}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PK pk)) return false;
            return Objects.equals(chatId, pk.chatId)
                && messengerType == pk.messengerType
                && Objects.equals(limitDate, pk.limitDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(chatId, messengerType, limitDate);
        }
    }
}
