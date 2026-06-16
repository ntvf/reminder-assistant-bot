package io.chatbots.reminder.service;

import io.chatbots.reminder.ai.ChainedReminder;
import io.chatbots.reminder.ai.PromptSanitizerService;
import io.chatbots.reminder.ai.ReminderAiService;
import io.chatbots.reminder.ai.ReminderParseResult;
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
        var v = validate(message, languageCode);
        if (v.errorText() != null) return v.errorText();
        return persistAndRespond(v, message.chatId());
    }

    public ReminderOutcome createOrOfferLeadTime(MessengerMessage message, String languageCode) {
        var v = validate(message, languageCode);
        if (v.errorText() != null) return ReminderOutcome.text(v.errorText());
        var p = v.parse();
        boolean offerLead = p.preEventChoice() && !p.recurring() && p.fireAt() != null
            && (v.chain() == null || v.chain().isEmpty());
        if (offerLead) {
            return ReminderOutcome.lead(new LeadChoiceDraft(p.reminderText(), p.eventText(), p.fireAt(), v.userLang()));
        }
        return ReminderOutcome.text(persistAndRespond(v, message.chatId()));
    }

    public String createTimedReminder(String chatId, MessengerType messengerType, String reminderText,
                                      String eventText, LocalDateTime fireAt, LocalDateTime fallbackFireAt,
                                      String languageCode) {
        var chatUserOpt = chatUserRepository.findByChatIdAndMessengerType(chatId, messengerType);
        if (chatUserOpt.isEmpty()) return BotMessages.get(BotMessages.Key.WRONG, languageCode);
        var chatUser = chatUserOpt.get();
        var userLang = chatUser.getLanguageCode() != null ? chatUser.getLanguageCode() : languageCode;

        var count = reminderRepository.countByChatUserAndActiveTrue(chatUser);
        if (count >= appProperties.maxRemindersPerChat()) {
            throw new MaxRemindersExceededException(
                BotMessages.get(BotMessages.Key.MAX_REMINDERS, userLang, appProperties.maxRemindersPerChat()));
        }
        var chosen = isFireAtInvalid(fireAt, chatUser.getTimezone()) ? fallbackFireAt : fireAt;
        if (isFireAtInvalid(chosen, chatUser.getTimezone())) {
            return BotMessages.get(BotMessages.Key.FIREAT_INVALID, userLang);
        }
        var scheduleDesc = cronDescriptionService.resolve(null, null, chosen, false, userLang);
        var saved = saveReminder(chatUser, reminderText, eventText, scheduleDesc, false, null, chosen);
        log.info("Created lead-time reminder {} for chat {}", saved.getId(), chatId);
        var label = displayLabel(reminderText, eventText);
        var relWord = chosen.toLocalDate().isEqual(fallbackFireAt.toLocalDate())
            ? null : cronDescriptionService.relativeDayWord(chosen, fallbackFireAt, userLang);
        var displayText = relWord != null ? label + " (" + relWord + ")" : label;
        return new StringBuilder(BotMessages.get(BotMessages.Key.REMINDER_SET, userLang))
            .append("\n📝 <b>").append(BotMessages.htmlEscape(displayText))
            .append("</b>\n🕐 <i>").append(BotMessages.htmlEscape(scheduleDesc)).append("</i>")
            .toString();
    }

    private Validated validate(MessengerMessage message, String languageCode) {
        var cleanText = promptSanitizerService.sanitize(message.text(), message.forwarded());
        log.info("Reminder request from chat {}: {}", message.chatId(), message.text());

        var chatUser = getOrCreateChatUser(message.chatId(), message.messengerType(), languageCode);
        var userLanguageCode = chatUser.getLanguageCode();

        rateLimitService.checkAndIncrement(message.chatId(), message.messengerType(), userLanguageCode);

        var count = reminderRepository.countByChatUserAndActiveTrue(chatUser);
        if (count >= appProperties.maxRemindersPerChat()) {
            throw new MaxRemindersExceededException(
                BotMessages.get(BotMessages.Key.MAX_REMINDERS, userLanguageCode, appProperties.maxRemindersPerChat()));
        }

        var parseResult = reminderAiService.parseReminder(cleanText, chatUser.getTimezone(), userLanguageCode);
        if (parseResult == null) {
            return Validated.error(BotMessages.get(BotMessages.Key.WRONG, userLanguageCode));
        }

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
                return Validated.error("❌ " + parseResult.errorMessage());
            }
            return Validated.error(BotMessages.get(BotMessages.Key.INVALID_SCHEDULE, userLanguageCode));
        }

        List<ChainedReminder> chain = parseResult.chain() != null ? parseResult.chain() : List.of();
        if (count + 1 + chain.size() > appProperties.maxRemindersPerChat()) {
            throw new MaxRemindersExceededException(
                BotMessages.get(BotMessages.Key.MAX_REMINDERS, userLanguageCode, appProperties.maxRemindersPerChat()));
        }

        if (parseResult.recurring() && parseResult.cronExpression() != null
                && !CronExpression.isValidExpression(parseResult.cronExpression())) {
            return Validated.error(BotMessages.get(BotMessages.Key.INVALID_SCHEDULE, userLanguageCode));
        }
        if (parseResult.recurring() && parseResult.cronExpression() != null
                && isCronTooFrequent(parseResult.cronExpression())) {
            return Validated.error(BotMessages.get(BotMessages.Key.CRON_TOO_FREQUENT, userLanguageCode));
        }
        if (!parseResult.recurring() && parseResult.fireAt() != null
                && isFireAtInvalid(parseResult.fireAt(), chatUser.getTimezone())) {
            return Validated.error(BotMessages.get(BotMessages.Key.FIREAT_INVALID, userLanguageCode));
        }

        var scheduleDesc = cronDescriptionService.resolve(
            parseResult.scheduleDescription(), parseResult.cronExpression(),
            parseResult.fireAt(), parseResult.recurring(), userLanguageCode);

        return Validated.ok(chatUser, userLanguageCode, parseResult, scheduleDesc, chain);
    }

    private String persistAndRespond(Validated v, String chatId) {
        var chatUser = v.chatUser();
        var userLanguageCode = v.userLang();
        var parseResult = v.parse();
        var scheduleDesc = v.scheduleDesc();
        var chain = v.chain();

        var saved = saveReminder(chatUser, parseResult.reminderText(), parseResult.eventText(), scheduleDesc,
            parseResult.recurring(), parseResult.cronExpression(), parseResult.fireAt());

        var response = new StringBuilder(BotMessages.get(BotMessages.Key.REMINDER_SET, userLanguageCode))
            .append("\n📝 <b>")
            .append(BotMessages.htmlEscape(displayLabel(parseResult.reminderText(), parseResult.eventText())))
            .append("</b>\n🕐 <i>")
            .append(BotMessages.htmlEscape(scheduleDesc))
            .append("</i>");

        if (!chain.isEmpty()) {
            response.append("\n\n").append(BotMessages.get(BotMessages.Key.LEAD_UP_HEADER, userLanguageCode));
            for (ChainedReminder entry : chain) {
                if (entry.cronExpression() != null && !CronExpression.isValidExpression(entry.cronExpression())) {
                    log.warn("Skipping chain entry with invalid cron: {}", entry.cronExpression());
                    continue;
                }
                if (entry.fireAt() != null && parseResult.fireAt() != null
                        && !entry.fireAt().isBefore(parseResult.fireAt())) {
                    log.warn("Skipping chain entry at/after main reminder time: {}", entry.fireAt());
                    continue;
                }
                var entryDesc = cronDescriptionService.resolve(
                    entry.scheduleDescription(), entry.cronExpression(),
                    entry.fireAt(), entry.cronExpression() != null, userLanguageCode);
                var chained = saveReminder(chatUser, entry.reminderText(), entry.eventText(), entryDesc,
                    entry.cronExpression() != null, entry.cronExpression(), entry.fireAt());
                response.append("\n  • <i>")
                    .append(BotMessages.htmlEscape(entryDesc))
                    .append("</i> — ")
                    .append(BotMessages.htmlEscape(displayLabel(entry.reminderText(), entry.eventText())));
                log.info("Created chain reminder {} for chat {}", chained.getId(), chatId);
            }
        }

        log.info("Created reminder {} for chat {}", saved.getId(), chatId);
        return response.toString();
    }

    public record ReminderOutcome(String replyText, LeadChoiceDraft leadChoice) {
        public static ReminderOutcome text(String replyText) { return new ReminderOutcome(replyText, null); }
        public static ReminderOutcome lead(LeadChoiceDraft draft) { return new ReminderOutcome(null, draft); }
    }

    public record LeadChoiceDraft(String reminderText, String eventText, LocalDateTime eventFireAt, String languageCode) {}

    private record Validated(String errorText, ChatUser chatUser, String userLang,
                             ReminderParseResult parse, String scheduleDesc, List<ChainedReminder> chain) {
        static Validated error(String errorText) { return new Validated(errorText, null, null, null, null, null); }
        static Validated ok(ChatUser chatUser, String userLang, ReminderParseResult parse,
                            String scheduleDesc, List<ChainedReminder> chain) {
            return new Validated(null, chatUser, userLang, parse, scheduleDesc, chain);
        }
    }

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

    private boolean isFireAtInvalid(LocalDateTime fireAt, String timezone) {
        ZoneId zone;
        try { zone = ZoneId.of(timezone); } catch (Exception e) { zone = ZoneId.of("UTC"); }
        var now = LocalDateTime.now(zone);
        return fireAt.isBefore(now.minusSeconds(30)) || fireAt.isAfter(now.plusYears(50));
    }

    private static String displayLabel(String reminderText, String eventText) {
        return eventText != null && !eventText.isBlank() ? eventText : reminderText;
    }

    private Reminder saveReminder(ChatUser chatUser, String text, String eventText, String scheduleDescription,
                                  boolean recurring, String cronExpression, java.time.LocalDateTime fireAt) {
        var reminder = new Reminder();
        reminder.setChatUser(chatUser);
        reminder.setReminderText(text);
        reminder.setEventText(eventText);
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
        return listReminders(chatId, messengerType, false);
    }

    @Transactional(readOnly = true)
    public String listReminders(String chatId, MessengerType messengerType, boolean numbered) {
        var chatUserOpt = chatUserRepository.findByChatIdAndMessengerType(chatId, messengerType);
        if (chatUserOpt.isEmpty()) {
            return BotMessages.get(BotMessages.Key.NO_REMINDERS, null);
        }

        var chatUser = chatUserOpt.get();
        var languageCode = chatUser.getLanguageCode();
        var reminders = reminderRepository.findByChatUserAndActiveTrueOrderByIdAsc(chatUser);
        if (reminders.isEmpty()) {
            return BotMessages.get(BotMessages.Key.NO_REMINDERS, languageCode);
        }

        var sb = new StringBuilder("<b>")
            .append(BotMessages.htmlEscape(BotMessages.get(BotMessages.Key.REMINDERS_HEADER, languageCode)))
            .append("</b>\n\n");
        int index = 1;
        for (var reminder : reminders) {
            if (numbered) {
                sb.append("<b>").append(index++).append(".</b> ");
            }
            sb.append("🔔 <b>").append(BotMessages.htmlEscape(reminder.getDisplayText())).append("</b>");
            sb.append(" — <i>").append(BotMessages.htmlEscape(reminder.getScheduleDescription())).append("</i>");
            long secs = secondsUntilNextFire(reminder, chatUser.getTimezone());
            if (secs > 0) {
                sb.append(" (").append(BotMessages.htmlEscape(BotMessages.formatCountdown(secs, languageCode))).append(")");
            }
            sb.append("\n");
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
                ce.setTimeZone(java.util.TimeZone.getTimeZone(zoneId));
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
        reminder.setDeletedAt(LocalDateTime.now());
        reminderRepository.save(reminder);
        eventPublisher.publishEvent(new ReminderDeletedEvent(reminder));
        return BotMessages.get(BotMessages.Key.DELETED, languageCode, BotMessages.htmlEscape(reminder.getDisplayText()));
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

    public void saveLocation(String chatId, MessengerType messengerType, double latitude, double longitude, String languageCode) {
        var user = getOrCreateChatUser(chatId, messengerType, languageCode);
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        chatUserRepository.save(user);
    }

    public void confirmTimezone(String chatId, MessengerType messengerType, String timezone, String languageCode) {
        ZoneId.of(timezone);
        var user = getOrCreateChatUser(chatId, messengerType, languageCode);
        user.setTimezone(timezone);
        user.setTimezoneConfirmed(true);
        chatUserRepository.save(user);
    }

    private ChatUser getOrCreateChatUser(String chatId, MessengerType messengerType, String languageCode) {
        return chatUserRepository.findByChatIdAndMessengerTypeIncludeDeleted(chatId, messengerType)
            .map(user -> {
                if (user.isDeleted()) {
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
                eventPublisher.publishEvent(new ReminderDeletedEvent(r));
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
    public String getUserTimezone(String chatId, MessengerType messengerType) {
        return chatUserRepository.findByChatIdAndMessengerType(chatId, messengerType)
            .map(ChatUser::getTimezone)
            .orElse(null);
    }
    @Transactional(readOnly = true)
    public List<Long> getActiveReminderIds(String chatId, MessengerType messengerType) {
        return chatUserRepository.findByChatIdAndMessengerType(chatId, messengerType)
            .map(user -> reminderRepository.findByChatUserAndActiveTrueOrderByIdAsc(user)
                .stream().map(Reminder::getId).toList())
            .orElse(List.of());
    }

    public List<Reminder> getActiveReminders(String chatId, MessengerType messengerType) {
        return chatUserRepository.findByChatIdAndMessengerType(chatId, messengerType)
            .map(user -> reminderRepository.findByChatUserAndActiveTrueOrderByIdAsc(user))
            .orElse(List.of());
    }
}
