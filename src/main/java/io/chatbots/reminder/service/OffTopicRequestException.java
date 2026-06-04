package io.chatbots.reminder.service;

public class OffTopicRequestException extends RuntimeException {
    public OffTopicRequestException(String message) {
        super(message);
    }
}
