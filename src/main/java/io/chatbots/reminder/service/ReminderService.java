package io.chatbots.reminder.service;

import io.chatbots.reminder.ai.ChainedReminder;
import io.chatbots.reminder.ai.PromptSanitizerService;
import io.chatbots.reminder.ai.ReminderAiService;
import io.chatbots.reminder.bot.MessengerMessage;
import io.chatbots.reminder.bot.MessengerType;
import io.chatbots.reminder.bot.TimezoneDetector;
import io.chatbots.reminder.config.AppProperties;
import io.chatbots.reminder.domain.ChatUser;
import io.chatbots.reminder.domain.ChatUserRepository;
import io.chatbots.reminder.domain.Reminder;
import io.chatbots.reminder.domain.ReminderRepository;
import io.chatbots.reminder.scheduler.ReminderCreatedEvent;
import io.chatbots.reminder.scheduler.ReminderDeletedEvent;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ReminderService {

    private static final Logger log = LoggerFactory.getLogger(ReminderService.class);

    private static final Set<String> SUPPORTED_LOCALES =
        Set.of("en", "ru", "de", "fr", "es", "pt", "it", "tr", "pl", "uk");

    private final ReminderRepository reminderRepository;
    private final ChatUserRepository chatUserRepository;
    private final ReminderAiService reminderAiService;
    private final PromptSanitizerService promptSanitizerService;
    private final CronDescriptionService cronDescriptionService;
    private final AppProperties appProperties;
    private final ApplicationEventPublisher eventPublisher;
    private final RateLimitService rateLimitService;

    public ReminderService(
            ReminderRepository reminderRepository,
            ChatUserRepository chatUserRepository,
            ReminderAiService reminderAiService,
            PromptSanitizerService promptSanitizerService,
            CronDescriptionService cronDescriptionService,
            AppProperties appProperties,
            ApplicationEventPublisher eventPublisher,
            RateLimitService rateLimitService) {
        this.reminderRepository = reminderRepository;
        this.chatUserRepository = chatUserRepository;
        this.reminderAiService = reminderAiService;
        this.promptSanitizerService = promptSanitizerService;
        this.cronDescriptionService = cronDescriptionService;
        this.appProperties = appProperties;
        this.eventPublisher = eventPublisher;
        this.rateLimitService = rateLimitService;
    }

    public String createReminder(MessengerMessage message, String languageCode) {
        promptSanitizerService.validateInput(message.text());

        var chatUser = getOrCreateChatUser(message.chatId(), message.messengerType(), languageCode);
        var userLanguageCode = chatUser.getLanguageCode();

        rateLimitService.checkAndIncrement(message.chatId(), message.messengerType(), userLanguageCode);

        var count = reminderRepository.countByChatUserAndActiveTrue(chatUser);
        if (count >= appProperties.maxRemindersPerChat()) {
            throw new MaxRemindersExceededException(
                BotMessages.get(BotMessages.Key.MAX_REMINDERS, userLanguageCode, appProperties.maxRemindersPerChat()));
        }

        var parseResult = reminderAiService.parseReminder(message.text(), chatUser.getTimezone(), userLanguageCode);
        if (parseResult == null) {
            return BotMessages.get(BotMessages.Key.WRONG, userLanguageCode);
        }

        // Update stored language from AI detection
        if (parseResult.detectedLanguageCode() != null) {
            var detected = parseResult.detectedLanguageCode().toLowerCase();
            if (detected.length() > 2) detected = detected.substring(0, 2);
            if (SUPPORTED_LOCALES.contains(detected) && !detected.equals(userLanguageCode)) {
                userLanguageCode = detected;
                chatUser.setLanguageCode(detected);
                chatUserRepository.save(chatUser);
            }
        }
        if (!parseResult.valid()) {
            if (parseResult.errorMessage() != null && !parseResult.errorMessage().isBlank()) {
                return "❌ " + parseResult.errorMessage();
            }
            return BotMessages.get(BotMessages.Key.INVALID_SCHEDULE, userLanguageCode);
        }

        List<ChainedReminder> chain = parseResult.chain() != null ? parseResult.chain() : List.of();
        if (count + 1 + chain.size() > appProperties.maxRemindersPerChat()) {
            throw new MaxRemindersExceededException(
                BotMessages.get(BotMessages.Key.MAX_REMINDERS, userLanguageCode, appProperties.maxRemindersPerChat()));
        }

        if (parseResult.recurring() && parseResult.cronExpression() != null
                && !CronExpression.isValidExpression(parseResult.cronExpression())) {
            return BotMessages.get(BotMessages.Key.INVALID_SCHEDULE, userLanguageCode);
        }
        if (parseResult.recurring() && parseResult.cronExpression() != null
                && isCronTooFrequent(parseResult.cronExpression())) {
            return BotMessages.get(BotMessages.Key.CRON_TOO_FREQUENT, userLanguageCode);
        }
        if (!parseResult.recurring() && parseResult.fireAt() != null
                && isFireAtInvalid(parseResult.fireAt(), chatUser.getTimezone())) {
            return BotMessages.get(BotMessages.Key.FIREAT_INVALID, userLanguageCode);
        }

        var scheduleDesc = cronDescriptionService.resolve(
            parseResult.scheduleDescription(), parseResult.cronExpression(),
            parseResult.fireAt(), parseResult.recurring(), userLanguageCode);

        var saved = saveReminder(chatUser, parseResult.reminderText(), scheduleDesc,
            parseResult.recurring(), parseResult.cronExpression(), parseResult.fireAt());

        var response = new StringBuilder(BotMessages.get(BotMessages.Key.REMINDER_SET, userLanguageCode))
            .append("\n📝 ")
            .append(parseResult.reminderText())
            .append("\n🕐 ")
            .append(scheduleDesc);

        if (!chain.isEmpty()) {
            response.append("\n\n").append(BotMessages.get(BotMessages.Key.LEAD_UP_HEADER, userLanguageCode));
            for (ChainedReminder entry : chain) {
                if (entry.cronExpression() != null && !CronExpression.isValidExpression(entry.cronExpression())) {
                    log.warn("Skipping chain entry with invalid cron: {}", entry.cronExpression());
                    continue;
                }
                var entryDesc = cronDescriptionService.resolve(
                    entry.scheduleDescription(), entry.cronExpression(),
                    entry.fireAt(), entry.cronExpression() != null, userLanguageCode);
                var chained = saveReminder(chatUser, entry.reminderText(), entryDesc,
                    entry.cronExpression() != null, entry.cronExpression(), entry.fireAt());
                response.append("\n  • ")
                    .append(entryDesc)
                    .append(" — ")
                    .append(entry.reminderText());
                log.info("Created chain reminder {} for chat {}", chained.getId(), message.chatId());
            }
        }

        log.info("Created reminder {} for chat {}", saved.getId(), message.chatId());
        return response.toString();
    }

    /** Returns true if cron would fire more often than once per 30 minutes. */
    private boolean isCronTooFrequent(String expr) {
        try {
            var cron = new CronExpression(expr);
            var next1 = cron.getNextValidTimeAfter(new Date());
            if (next1 == null) return false;
            var next2 = cron.getNextValidTimeAfter(next1);
            if (next2 == null) return false;
            return (next2.getTime() - next1.getTime()) < 30L * 60 * 1000;
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns true if fireAt is in the past (>30s ago) or absurdly far future (>50 years). */
    private boolean isFireAtInvalid(LocalDateTime fireAt, String timezone) {
        ZoneId zone;
        try { zone = ZoneId.of(timezone); } catch (Exception e) { zone = ZoneId.of("UTC"); }
        var now = LocalDateTime.now(zone);
        return fireAt.isBefore(now.minusSeconds(30)) || fireAt.isAfter(now.plusYears(50));
    }

    private Reminder saveReminder(ChatUser chatUser, String text, String scheduleDescription,
                                  boolean recurring, String cronExpression, java.time.LocalDateTime fireAt) {
        var reminder = new Reminder();
        reminder.setChatUser(chatUser);
        reminder.setReminderText(text);
        reminder.setScheduleDescription(scheduleDescription);
        reminder.setRecurring(recurring);
        reminder.setCronExpression(cronExpression);
        reminder.setFireAt(fireAt);
        reminder.setActive(true);
        var saved = reminderRepository.save(reminder);
        eventPublisher.publishEvent(new ReminderCreatedEvent(saved));
        return saved;
    }

    @Transactional(readOnly = true)
    public String listReminders(String chatId, MessengerType messengerType) {
        var chatUserOpt = chatUserRepository.findByChatIdAndMessengerType(chatId, messengerType);
        if (chatUserOpt.isEmpty()) {
            return BotMessages.get(BotMessages.Key.NO_REMINDERS, null);
        }

        var chatUser = chatUserOpt.get();
        var languageCode = chatUser.getLanguageCode();
        var reminders = reminderRepository.findByChatUserAndActiveTrue(chatUser);
        if (reminders.isEmpty()) {
            return BotMessages.get(BotMessages.Key.NO_REMINDERS, languageCode);
        }

        var sb = new StringBuilder(BotMessages.get(BotMessages.Key.REMINDERS_HEADER, languageCode)).append("\n\n");
        for (var reminder : reminders) {
            sb.append("🔔 [").append(reminder.getId()).append("] ").append(reminder.getReminderText()).append("\n");
            sb.append("   📅 ").append(reminder.getScheduleDescription());
            long secs = secondsUntilNextFire(reminder, chatUser.getTimezone());
            if (secs > 0) {
                sb.append("\n   ⏱ ").append(BotMessages.formatCountdown(secs, languageCode));
            }
            sb.append("\n\n");
        }
        return sb.toString().trim();
    }

    private long secondsUntilNextFire(Reminder r, String timezone) {
        var zoneId = ZoneId.of(timezone != null && !timezone.isBlank() ? timezone : "UTC");
        var nowInstant = java.time.Instant.now();
        if (!r.isRecurring() && r.getFireAt() != null) {
            var fireInstant = r.getFireAt().atZone(zoneId).toInstant();
            return fireInstant.getEpochSecond() - nowInstant.getEpochSecond();
        }
        if (r.isRecurring() && r.getCronExpression() != null) {
            try {
                var ce = new CronExpression(r.getCronExpression());
                var next = ce.getNextValidTimeAfter(Date.from(nowInstant));
                if (next != null) {
                    return (next.getTime() - System.currentTimeMillis()) / 1000;
                }
            } catch (Exception ignored) {
            }
        }
        return -1;
    }

    public String deleteReminder(String chatId, MessengerType messengerType, long reminderId) {
        var chatUserOpt = chatUserRepository.findByChatIdAndMessengerType(chatId, messengerType);
        if (chatUserOpt.isEmpty()) {
            return BotMessages.get(BotMessages.Key.NOT_FOUND, null);
        }

        var chatUser = chatUserOpt.get();
        var languageCode = chatUser.getLanguageCode();
        var reminderOpt = reminderRepository.findById(reminderId);
        if (reminderOpt.isEmpty() || !reminderOpt.get().getChatUser().getId().equals(chatUser.getId())) {
            return BotMessages.get(BotMessages.Key.NOT_FOUND, languageCode);
        }

        var reminder = reminderOpt.get();
        if (!reminder.isActive()) {
            return BotMessages.get(BotMessages.Key.NOT_FOUND, languageCode);
        }
        reminder.setActive(false);
        reminderRepository.save(reminder);
        eventPublisher.publishEvent(new ReminderDeletedEvent(reminder));
        return BotMessages.get(BotMessages.Key.DELETED, languageCode, reminderId);
    }

    public String updateTimezone(String chatId, MessengerType messengerType, String timezone) {
        var chatUserOpt = chatUserRepository.findByChatIdAndMessengerType(chatId, messengerType);
        var languageCode = chatUserOpt.map(ChatUser::getLanguageCode).orElse(null);

        try {
            ZoneId.of(timezone);
        } catch (Exception e) {
            return BotMessages.get(BotMessages.Key.INVALID_TZ, languageCode, timezone);
        }

        var chatUser = chatUserOpt.orElseGet(() -> chatUserRepository.save(new ChatUser(chatId, messengerType, null)));
        chatUser.setTimezone(timezone);
        chatUser.setTimezoneConfirmed(true);
        chatUserRepository.save(chatUser);
        return BotMessages.get(BotMessages.Key.TZ_UPDATED, chatUser.getLanguageCode(), timezone);
    }

    @Transactional(readOnly = true)
    public boolean isTimezoneConfirmed(String chatId, MessengerType messengerType) {
        return chatUserRepository.findByChatIdAndMessengerType(chatId, messengerType)
            .map(ChatUser::isTimezoneConfirmed)
            .orElse(false);
    }

    public void confirmTimezone(String chatId, MessengerType messengerType, String timezone, String languageCode) {
        ZoneId.of(timezone); // validate — throws DateTimeException if invalid
        var user = getOrCreateChatUser(chatId, messengerType, languageCode);
        user.setTimezone(timezone);
        user.setTimezoneConfirmed(true);
        chatUserRepository.save(user);
    }

    private ChatUser getOrCreateChatUser(String chatId, MessengerType messengerType, String languageCode) {
        return chatUserRepository.findByChatIdAndMessengerTypeIncludeDeleted(chatId, messengerType)
            .map(user -> {
                if (user.isDeleted()) {
                    // Resurrect: clear deleted_at, reset timezone onboarding, restore Telegram language
                    user.setDeletedAt(null);
                    user.setTimezoneConfirmed(false);
                    if (languageCode != null) {
                        user.setLanguageCode(languageCode);
                    }
                    return chatUserRepository.save(user);
                }
                return user;
            })
            .orElseGet(() -> {
                var user = new ChatUser(chatId, messengerType, languageCode);
                user.setTimezone(TimezoneDetector.detect(languageCode));
                return chatUserRepository.save(user);
            });
    }

    public void deleteUserData(String chatId, MessengerType messengerType) {        chatUserRepository.findByChatIdAndMessengerType(chatId, messengerType).ifPresent(chatUser -> {
            var now = LocalDateTime.now();
            var reminders = reminderRepository.findByChatUser(chatUser);
            reminders.forEach(r -> {
                eventPublisher.publishEvent(new ReminderDeletedEvent(r)); // cancel Quartz job
                r.setActive(false);
                r.setDeletedAt(now);
            });
            reminderRepository.saveAll(reminders);
            chatUser.setDeletedAt(now);
            chatUserRepository.save(chatUser);
            log.info("Soft-deleted all data for chat {} ({})", chatId, messengerType);
        });
    }

    @Transactional(readOnly = true)
    public String getUserLanguage(String chatId, MessengerType messengerType) {
        return chatUserRepository.findByChatIdAndMessengerType(chatId, messengerType)
            .map(ChatUser::getLanguageCode)
            .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Long> getActiveReminderIds(String chatId, MessengerType messengerType) {
        return chatUserRepository.findByChatIdAndMessengerType(chatId, messengerType)
            .map(user -> reminderRepository.findByChatUserAndActiveTrue(user)
                .stream().map(Reminder::getId).toList())
            .orElse(List.of());
    }
}
