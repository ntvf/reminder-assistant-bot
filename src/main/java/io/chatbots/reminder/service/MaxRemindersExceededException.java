package io.chatbots.reminder.service;

public class MaxRemindersExceededException extends RuntimeException {
    public MaxRemindersExceededException(String message) {
        super(message);
    }
}
