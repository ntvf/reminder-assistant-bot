package io.chatbots.reminder.domain;

import io.chatbots.reminder.bot.MessengerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface ChatDailyLimitRepository extends JpaRepository<ChatDailyLimit, ChatDailyLimit.PK> {

    @Query("SELECT c FROM ChatDailyLimit c WHERE c.chatId = ?1 AND c.messengerType = ?2 AND c.limitDate = ?3")
    Optional<ChatDailyLimit> findByKey(String chatId, MessengerType messengerType, LocalDate date);

    @Modifying
    @Query(nativeQuery = true, value = """
        INSERT INTO chat_daily_limits (chat_id, messenger_type, limit_date, request_count)
        VALUES (?1, ?2, ?3, 1)
        ON CONFLICT (chat_id, messenger_type, limit_date)
        DO UPDATE SET request_count = chat_daily_limits.request_count + 1
        """)
    void upsertIncrement(String chatId, String messengerType, LocalDate date);
}
