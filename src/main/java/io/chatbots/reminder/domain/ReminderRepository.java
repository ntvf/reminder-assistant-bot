package io.chatbots.reminder.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByChatUserAndActiveTrueOrderByIdAsc(ChatUser chatUser);
    List<Reminder> findByChatUser(ChatUser chatUser);
    long countByChatUserAndActiveTrue(ChatUser chatUser);
    @Query("SELECT r FROM Reminder r JOIN FETCH r.chatUser WHERE r.active = true")
    List<Reminder> findByActiveTrue();
    long countByActiveTrue();

    @Query("SELECT r.chatUser.languageCode, COUNT(r) FROM Reminder r WHERE r.active = true GROUP BY r.chatUser.languageCode")
    List<Object[]> countActiveByLanguage();

    @Query("SELECT COUNT(DISTINCT r.chatUser.id) FROM Reminder r WHERE r.active = true")
    long countDistinctActiveUsers();
}
