package io.chatbots.reminder.bot;

public record MessengerMessage(
    String chatId,
    MessengerType messengerType,
    String text,
    String username,
    Long userId,
    boolean forwarded
) {
    /** Convenience constructor for directly-typed (non-forwarded) messages. */
    public MessengerMessage(String chatId, MessengerType messengerType, String text, String username, Long userId) {
        this(chatId, messengerType, text, username, userId, false);
    }
}
