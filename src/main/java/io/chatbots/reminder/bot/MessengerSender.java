package io.chatbots.reminder.bot;

public interface MessengerSender {
    MessengerType supports();
    void send(String chatId, String text);
}
