package io.chatbots.reminder.bot;

public record MessengerMessage(
    String chatId,
    MessengerType messengerType,
    String text,
    String username,
    Long userId
) {
}
