package io.chatbots.reminder.domain;

import io.chatbots.reminder.bot.MessengerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
    @Query("SELECT u FROM ChatUser u WHERE u.chatId = :chatId AND u.messengerType = :messengerType AND u.deletedAt IS NULL")
    Optional<ChatUser> findByChatIdAndMessengerType(String chatId, MessengerType messengerType);

    @Query("SELECT u FROM ChatUser u WHERE u.chatId = :chatId AND u.messengerType = :messengerType")
    Optional<ChatUser> findByChatIdAndMessengerTypeIncludeDeleted(String chatId, MessengerType messengerType);

    long countByMessengerType(MessengerType messengerType);
}
