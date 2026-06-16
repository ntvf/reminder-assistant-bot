package io.chatbots.reminder.scheduler;

import io.chatbots.reminder.bot.MessengerSender;
import io.chatbots.reminder.bot.MessengerType;
import io.chatbots.reminder.domain.ReminderRepository;
import io.chatbots.reminder.service.BotMessages;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReminderNotificationJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(ReminderNotificationJob.class);
    public static final String REMINDER_ID_KEY = "reminderId";
    public static final String CHAT_ID_KEY = "chatId";
    public static final String MESSENGER_TYPE_KEY = "messengerType";

    private final ReminderRepository reminderRepository;
    private final List<MessengerSender> messengerSenders;

    public ReminderNotificationJob(ReminderRepository reminderRepository, List<MessengerSender> messengerSenders) {
        this.reminderRepository = reminderRepository;
        this.messengerSenders = messengerSenders;
    }

    @Override
    public void execute(JobExecutionContext context) {
        var dataMap = context.getJobDetail().getJobDataMap();
        var reminderId = dataMap.getLong(REMINDER_ID_KEY);
        var chatId = dataMap.getString(CHAT_ID_KEY);
        var messengerTypeStr = dataMap.getString(MESSENGER_TYPE_KEY);

        var reminderOpt = reminderRepository.findById(reminderId);
        if (reminderOpt.isEmpty()) {
            log.warn("Reminder {} not found, skipping", reminderId);
            return;
        }
        var reminder = reminderOpt.get();
        if (!reminder.isActive()) {
            log.info("Reminder {} is no longer active, skipping", reminderId);
            return;
        }

        var messengerType = MessengerType.valueOf(messengerTypeStr);
        var sender = messengerSenders.stream()
            .filter(s -> s.supports() == messengerType)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No sender for " + messengerType));

        sender.send(chatId, "⏰ <b>" + BotMessages.htmlEscape(reminder.getReminderText()) + "</b>");
        log.info("Sent reminder {} to chat {}", reminderId, chatId);

        if (!reminder.isRecurring()) {
            reminder.setActive(false);
            reminderRepository.save(reminder);
        }
    }
}
