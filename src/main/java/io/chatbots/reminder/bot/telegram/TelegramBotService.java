package io.chatbots.reminder.bot.telegram;

import io.chatbots.reminder.bot.MessengerMessage;
import io.chatbots.reminder.bot.MessengerSender;
import io.chatbots.reminder.bot.MessengerType;
import io.chatbots.reminder.config.AppProperties;
import io.chatbots.reminder.service.BotMessages;
import io.chatbots.reminder.service.MaxRemindersExceededException;
import io.chatbots.reminder.service.OffTopicRequestException;
import io.chatbots.reminder.service.RateLimitExceededException;
import io.chatbots.reminder.service.ReminderService;
import io.chatbots.reminder.service.StatisticsService;
import io.chatbots.reminder.service.TimezoneGeoService;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.location.Location;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import io.chatbots.reminder.domain.Reminder;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TelegramBotService implements SpringLongPollingBot, MessengerSender {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotService.class);

    // Region → list of (button label, IANA timezone) pairs
    private static final Map<String, List<TimezoneOption>> REGIONS = new LinkedHashMap<>();
    // The same region labels used as reply keyboard button texts (fixed, not translated)
    private static final Set<String> REGION_BTN_SET;

    static {
        REGIONS.put("🌍 Europe", List.of(
            new TimezoneOption("🇬🇧 London", "Europe/London"),
            new TimezoneOption("🇵🇹 Lisbon", "Europe/Lisbon"),
            new TimezoneOption("🇫🇷 Paris / Berlin", "Europe/Paris"),
            new TimezoneOption("🇵🇱 Warsaw / Prague", "Europe/Warsaw"),
            new TimezoneOption("🇫🇮 Helsinki / Kyiv", "Europe/Helsinki"),
            new TimezoneOption("🇷🇺 Moscow", "Europe/Moscow"),
            new TimezoneOption("🇹🇷 Istanbul", "Europe/Istanbul")
        ));
        REGIONS.put("🌎 Americas", List.of(
            new TimezoneOption("🗽 New York", "America/New_York"),
            new TimezoneOption("🌆 Chicago", "America/Chicago"),
            new TimezoneOption("🏔 Denver", "America/Denver"),
            new TimezoneOption("🌉 Los Angeles", "America/Los_Angeles"),
            new TimezoneOption("🍁 Toronto", "America/Toronto"),
            new TimezoneOption("🇧🇷 São Paulo", "America/Sao_Paulo"),
            new TimezoneOption("🇦🇷 Buenos Aires", "America/Argentina/Buenos_Aires"),
            new TimezoneOption("🇲🇽 Mexico City", "America/Mexico_City")
        ));
        REGIONS.put("🌏 Asia", List.of(
            new TimezoneOption("🇮🇳 India", "Asia/Kolkata"),
            new TimezoneOption("🇵🇰 Pakistan", "Asia/Karachi"),
            new TimezoneOption("🇧🇩 Bangladesh", "Asia/Dhaka"),
            new TimezoneOption("🇹🇭 Bangkok", "Asia/Bangkok"),
            new TimezoneOption("🇸🇬 Singapore", "Asia/Singapore"),
            new TimezoneOption("🇨🇳 Shanghai", "Asia/Shanghai"),
            new TimezoneOption("🇯🇵 Tokyo", "Asia/Tokyo"),
            new TimezoneOption("🇰🇷 Seoul", "Asia/Seoul")
        ));
        REGIONS.put("🕌 Middle East", List.of(
            new TimezoneOption("🇸🇦 Riyadh", "Asia/Riyadh"),
            new TimezoneOption("🇦🇪 Dubai", "Asia/Dubai"),
            new TimezoneOption("🇮🇷 Tehran", "Asia/Tehran"),
            new TimezoneOption("🇮🇱 Jerusalem", "Asia/Jerusalem"),
            new TimezoneOption("🇰🇿 Almaty", "Asia/Almaty")
        ));
        REGIONS.put("🌏 Pacific / AU", List.of(
            new TimezoneOption("🇦🇺 Sydney", "Australia/Sydney"),
            new TimezoneOption("🇦🇺 Perth", "Australia/Perth"),
            new TimezoneOption("🇳🇿 Auckland", "Pacific/Auckland"),
            new TimezoneOption("🏝 Hawaii", "Pacific/Honolulu")
        ));
        REGIONS.put("🌐 UTC / Other", List.of(
            new TimezoneOption("🌐 UTC", "UTC"),
            new TimezoneOption("🇿🇦 Johannesburg", "Africa/Johannesburg"),
            new TimezoneOption("🇳🇬 Lagos", "Africa/Lagos"),
            new TimezoneOption("🇪🇬 Cairo", "Africa/Cairo")
        ));
        REGION_BTN_SET = Set.copyOf(REGIONS.keySet());
    }

    private static final List<String> REGION_ORDER = List.copyOf(REGIONS.keySet());

    // All translations of the main keyboard buttons — used for reverse text→command lookup
    private static final Set<String> ALL_BTN_LIST     = BotMessages.getAllValues(BotMessages.Key.BTN_LIST);
    private static final Set<String> ALL_BTN_CHANGE_TZ = BotMessages.getAllValues(BotMessages.Key.BTN_CHANGE_TZ);
    private static final Set<String> ALL_BTN_CANCEL   = BotMessages.getAllValues(BotMessages.Key.BTN_CANCEL);

    private record TimezoneOption(String label, String tz) {}

    private final String botToken;
    private final String botUsername;
    private final TelegramClient telegramClient;
    private final ReminderService reminderService;
    private final StatisticsService statisticsService;
    private final TimezoneGeoService timezoneGeoService;
    private final AppProperties appProperties;

    /** Reminder messages received before the user confirmed a timezone, replayed once it is set. */
    private final Map<String, MessengerMessage> pendingReminders = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * Text of forwarded messages awaiting a lead-time choice, keyed by "chatId:buttonsMessageId".
     * Filled when a forward is received, drained when the user taps a lead-time button.
     */
    private final Map<String, String> forwardTexts = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * Parsed direct-typed events awaiting a lead-time choice, keyed by "chatId:buttonsMessageId".
     * Unlike forwards, the event is already parsed, so the chosen offset is applied locally (no second LLM call).
     */
    private final Map<String, ReminderService.LeadChoiceDraft> leadDrafts = new java.util.concurrent.ConcurrentHashMap<>();

    /** Braille frames cycled to animate the "processing…" placeholder while a reminder is created. */
    private static final String[] SPINNER_FRAMES = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
    private static final long SPINNER_INTERVAL_MS = 700;
    private final ScheduledExecutorService spinnerExecutor = Executors.newScheduledThreadPool(2, r -> {
        var t = new Thread(r, "tg-spinner");
        t.setDaemon(true);
        return t;
    });

    public TelegramBotService(
            @Value("${telegram.bot-token}") String botToken,
            @Value("${telegram.bot-username}") String botUsername,
            OkHttpClient telegramOkHttpClient,
            ReminderService reminderService,
            StatisticsService statisticsService,
            TimezoneGeoService timezoneGeoService,
            AppProperties appProperties) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.telegramClient = new OkHttpTelegramClient(telegramOkHttpClient, botToken);
        this.reminderService = reminderService;
        this.statisticsService = statisticsService;
        this.timezoneGeoService = timezoneGeoService;
        this.appProperties = appProperties;
    }

    // ── Menu button (☰) commands ─────────────────────────────────────────────

    private static final List<String> SUPPORTED_LANG_CODES =
        List.of("en", "ru", "de", "fr", "es", "pt", "it", "tr", "pl", "uk");

    @EventListener(ApplicationReadyEvent.class)
    public void registerBotCommands() {
        registerBotCommands(null); // default (English) for unmatched locales
        for (var lang : SUPPORTED_LANG_CODES) {
            registerBotCommands(lang);
        }
    }

    private void registerBotCommands(String langCode) {
        try {
            telegramClient.execute(buildSetMyCommands(langCode));
        } catch (TelegramApiException e) {
            log.error("Failed to register bot commands for lang {}: {}", langCode, e.getMessage(), e);
        }
    }

    private SetMyCommands buildSetMyCommands(String langCode) {
        var commands = List.of(
            BotCommand.builder()
                .command("list")
                .description(BotMessages.get(BotMessages.Key.BTN_LIST, langCode))
                .build(),
            BotCommand.builder()
                .command("timezone")
                .description(BotMessages.get(BotMessages.Key.BTN_CHANGE_TZ, langCode))
                .build()
        );
        var builder = SetMyCommands.builder().commands(commands);
        if (langCode != null) builder.languageCode(langCode);
        return builder.build();
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updates -> updates.forEach(this::handleUpdate);
    }

    @Override
    public MessengerType supports() {
        return MessengerType.TELEGRAM;
    }

    @Override
    public void send(String chatId, String text) {
        sendWithMarkup(chatId, text, null);
    }

    public String getBotUsername() {
        return botUsername;
    }

    // ── Update routing ────────────────────────────────────────────────────────

    private void handleUpdate(Update update) {
        if (update.hasMyChatMember()) {
            var memberUpdate = update.getMyChatMember();
            var newStatus = memberUpdate.getNewChatMember().getStatus();
            if ("kicked".equals(newStatus) || "left".equals(newStatus)) {
                var chatId = memberUpdate.getChat().getId().toString();
                reminderService.deleteUserData(chatId, MessengerType.TELEGRAM);
            }
            return;
        }

        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
            return;
        }

        if (!update.hasMessage()) return;

        var message = update.getMessage();
        var chat = message.getChat();
        var chatId = chat.getId().toString();
        var isGroup = "group".equals(chat.getType()) || "supergroup".equals(chat.getType());

        var from = message.getFrom();
        var username = from != null ? from.getUserName() : null;
        var languageCode = from != null ? from.getLanguageCode() : null;

        if (message.hasLocation() && !isGroup) {
            handleLocationMessage(chatId, message.getLocation(), languageCode);
            return;
        }

        if (!message.hasText()) return;

        var rawText = message.getText().trim();
        if (isGroup && !rawText.startsWith("/") && !rawText.contains("@" + botUsername)) {
            return;
        }

        var text = normalizeCommand(rawText);
        if (isGroup) {
            text = text.replaceAll("@" + botUsername + "\\s*", "").trim();
        }

        // Persistent main keyboard buttons → map to commands
        if (ALL_BTN_LIST.contains(text)) text = "/list";
        else if (ALL_BTN_CHANGE_TZ.contains(text)) text = "/timezone";

        // Cancel button — dismiss timezone keyboard, show main keyboard
        if (!isGroup && ALL_BTN_CANCEL.contains(text)) {
            var profileLangC = reminderService.getUserLanguage(chatId, MessengerType.TELEGRAM);
            var cancelLang = profileLangC != null ? profileLangC : languageCode;
            sendWithMarkup(chatId, BotMessages.get(BotMessages.Key.BTN_LIST, cancelLang), buildMainKeyboard(cancelLang));
            return;
        }

        // Region button from timezone keyboard
        if (!isGroup && REGION_BTN_SET.contains(text)) {
            handleRegionButtonTap(chatId, text, languageCode);
            return;
        }

        var messengerMessage = new MessengerMessage(chatId, MessengerType.TELEGRAM, text, username, from != null ? from.getId() : null);

        // Stored profile language — used for error messages so they match the user's actual language
        var profileLang = reminderService.getUserLanguage(chatId, MessengerType.TELEGRAM);
        var displayLang = profileLang != null ? profileLang : languageCode;

        try {
            if (text.startsWith("/start")) {
                var name = username != null ? "@" + username : "there";
                if (reminderService.isTimezoneConfirmed(chatId, MessengerType.TELEGRAM)) {
                    sendWithMarkup(chatId, BotMessages.get(BotMessages.Key.START, displayLang, name), buildMainKeyboard(displayLang));
                } else {
                    send(chatId, BotMessages.get(BotMessages.Key.START, displayLang, name));
                    sendTimezoneRequest(chatId, displayLang);
                }
            } else if (text.startsWith("/list")) {
                var listText = reminderService.listReminders(chatId, MessengerType.TELEGRAM);
                var reminders = reminderService.getActiveReminders(chatId, MessengerType.TELEGRAM);
                if (reminders.isEmpty()) {
                    send(chatId, listText);
                } else {
                    // Default view is read-only: a single "Manage" button reveals the delete grid on demand.
                    sendWithMarkup(chatId, listText, buildManageKeyboard(displayLang));
                }
                // Remove the user's command / button-tap message to keep the chat clean.
                // Telegram only lets bots delete incoming messages in private chats.
                if (!isGroup) {
                    deleteMessage(chatId, message.getMessageId());
                }
            } else if (text.startsWith("/delete ")) {
                var parts = text.split("\\s+", 2);
                long reminderId = Long.parseLong(parts[1].trim());
                send(chatId, reminderService.deleteReminder(chatId, MessengerType.TELEGRAM, reminderId));
            } else if ("/timezone".equals(text)) {
                sendTimezoneRequest(chatId, displayLang);
            } else if (text.startsWith("/timezone ")) {
                var tz = text.substring("/timezone ".length()).trim();
                sendWithMarkup(chatId, reminderService.updateTimezone(chatId, MessengerType.TELEGRAM, tz), buildMainKeyboard(displayLang));
                if (reminderService.isTimezoneConfirmed(chatId, MessengerType.TELEGRAM)) {
                    replayPendingReminder(chatId, displayLang);
                }
            } else if (text.startsWith("/stats")) {
                send(chatId, statisticsService.buildStatsReport());
            } else if (text.startsWith("/")) {
                send(chatId, BotMessages.get(BotMessages.Key.UNKNOWN_CMD, displayLang));
            } else if (!text.isBlank()) {
                if (!reminderService.isTimezoneConfirmed(chatId, MessengerType.TELEGRAM)) {
                    // Without a confirmed timezone the bot's clock differs from the user's,
                    // producing reminders at the wrong wall-clock time. Onboard first, then
                    // replay this message so the user doesn't have to retype it.
                    pendingReminders.put(chatId, messengerMessage);
                    send(chatId, BotMessages.get(BotMessages.Key.TZ_NEEDED_FIRST, displayLang));
                    sendTimezoneRequest(chatId, displayLang);
                } else if (!isGroup && message.getForwardDate() != null) {
                    // Forwarded event announcement: timing intent is ambiguous (fire at the event,
                    // or ahead of it?). Ask the user with lead-time buttons instead of guessing.
                    promptForwardLeadTime(chatId, text, displayLang);
                } else {
                    handleReminderCreation(chatId, messengerMessage, languageCode, displayLang);
                }
            }
        } catch (NumberFormatException e) {
            send(chatId, BotMessages.get(BotMessages.Key.INVALID_ID, displayLang));
        } catch (OffTopicRequestException e) {
            send(chatId, BotMessages.get(BotMessages.Key.OFFTOPIC, displayLang));
        } catch (MaxRemindersExceededException e) {
            send(chatId, e.getMessage());
        } catch (RateLimitExceededException e) {
            send(chatId, e.getMessage());
        } catch (Exception e) {
            log.error("Error handling update for chat {}: {}", chatId, e.getMessage(), e);
            send(chatId, BotMessages.get(BotMessages.Key.WRONG, displayLang));
        }
    }

    /** Processes a reminder message that was stashed while the user set their timezone. */
    private void replayPendingReminder(String chatId, String displayLang) {
        var pending = pendingReminders.remove(chatId);
        if (pending == null) return;
        handleReminderCreation(chatId, pending, displayLang, displayLang);
    }

    /**
     * Posts an animated "processing…" placeholder, runs the (slow, AI-backed) reminder creation,
     * then replaces the placeholder in-place with the final result. Errors replace the placeholder
     * too, so the user never sees a stranded spinner.
     */
    private void handleReminderCreation(String chatId, MessengerMessage message, String languageCode, String displayLang) {
        var processingText = BotMessages.get(BotMessages.Key.PROCESSING, displayLang);
        var placeholderId = sendReturningId(chatId, SPINNER_FRAMES[0] + " " + processingText);
        var stopSpinner = placeholderId != null ? startSpinner(chatId, placeholderId, processingText) : null;

        ReminderService.ReminderOutcome outcome;
        try {
            outcome = reminderService.createOrOfferLeadTime(message, languageCode);
        } catch (OffTopicRequestException e) {
            outcome = ReminderService.ReminderOutcome.text(BotMessages.get(BotMessages.Key.OFFTOPIC, displayLang));
        } catch (MaxRemindersExceededException | RateLimitExceededException e) {
            outcome = ReminderService.ReminderOutcome.text(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to create reminder for chat {}: {}", chatId, e.getMessage(), e);
            outcome = ReminderService.ReminderOutcome.text(BotMessages.get(BotMessages.Key.WRONG, displayLang));
        } finally {
            if (stopSpinner != null) stopSpinner.run();
        }

        // Detected an event with no explicit reminder offset → ask how far ahead via buttons instead of persisting.
        if (outcome.leadChoice() != null && placeholderId != null) {
            if (leadDrafts.size() > 500) leadDrafts.clear();
            leadDrafts.put(chatId + ":" + placeholderId, outcome.leadChoice());
            editInlineKeyboard(chatId, placeholderId,
                BotMessages.get(BotMessages.Key.FWD_ASK, displayLang), buildLeadTimeKeyboard(displayLang, "lead"));
            return;
        }

        // No placeholder to host buttons → just schedule at the event time.
        var result = outcome.replyText() != null
            ? outcome.replyText()
            : reminderService.createTimedReminder(chatId, MessengerType.TELEGRAM,
                outcome.leadChoice().reminderText(), outcome.leadChoice().eventFireAt(),
                outcome.leadChoice().eventFireAt(), displayLang);
        if (placeholderId != null) {
            editText(chatId, placeholderId, result);
        } else {
            send(chatId, result);
        }
    }

    // ── Forwarded-message lead-time flow ──────────────────────────────────────

    /** Posts the forwarded text's lead-time question + buttons, stashing the text until a button is tapped. */
    private void promptForwardLeadTime(String chatId, String forwardText, String displayLang) {
        var msgId = sendReturningIdWithMarkup(chatId,
            BotMessages.get(BotMessages.Key.FWD_ASK, displayLang), buildLeadTimeKeyboard(displayLang, "fwd"));
        if (msgId == null) {
            // Couldn't show buttons — fall back to creating the reminder at the event time.
            handleReminderCreation(chatId,
                new MessengerMessage(chatId, MessengerType.TELEGRAM, forwardText, null, null, true), displayLang, displayLang);
            return;
        }
        // Bound the map so abandoned forwards can't grow it without limit.
        if (forwardTexts.size() > 500) forwardTexts.clear();
        forwardTexts.put(chatId + ":" + msgId, forwardText);
    }

    /** Lead-time buttons. {@code prefix} routes the callback: "fwd" (forwards, re-parse) or "lead" (direct events, local offset). */
    private InlineKeyboardMarkup buildLeadTimeKeyboard(String languageCode, String prefix) {
        var rows = new ArrayList<InlineKeyboardRow>();
        rows.add(new InlineKeyboardRow(List.of(
            InlineKeyboardButton.builder().text(BotMessages.get(BotMessages.Key.FWD_AT, languageCode)).callbackData(prefix + ":at").build())));
        rows.add(new InlineKeyboardRow(List.of(
            InlineKeyboardButton.builder().text(BotMessages.get(BotMessages.Key.FWD_1H, languageCode)).callbackData(prefix + ":1h").build(),
            InlineKeyboardButton.builder().text(BotMessages.get(BotMessages.Key.FWD_3H, languageCode)).callbackData(prefix + ":3h").build())));
        rows.add(new InlineKeyboardRow(List.of(
            InlineKeyboardButton.builder().text(BotMessages.get(BotMessages.Key.FWD_1D, languageCode)).callbackData(prefix + ":1d").build())));
        rows.add(new InlineKeyboardRow(List.of(
            InlineKeyboardButton.builder().text(BotMessages.get(BotMessages.Key.BTN_CANCEL, languageCode)).callbackData(prefix + ":cancel").build())));
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    /** Handles a lead-time button tap: appends a scheduling directive to the forwarded text and creates the reminder. */
    private void handleForwardLeadTime(String chatId, int messageId, String option, Long userId, String username, String languageCode) {
        var key = chatId + ":" + messageId;
        if ("cancel".equals(option)) {
            forwardTexts.remove(key);
            deleteMessage(chatId, messageId);
            return;
        }
        var forwardText = forwardTexts.remove(key);
        var profileLang = reminderService.getUserLanguage(chatId, MessengerType.TELEGRAM);
        var displayLang = profileLang != null ? profileLang : languageCode;
        if (forwardText == null) {
            // Stash expired (e.g. after a restart) — nothing to act on.
            editText(chatId, messageId, BotMessages.get(BotMessages.Key.WRONG, displayLang));
            return;
        }
        var directive = switch (option) {
            case "1h" -> "\n\n[INSTRUCTION: Schedule the reminder to fire 1 hour before the event's start time.]";
            case "3h" -> "\n\n[INSTRUCTION: Schedule the reminder to fire 3 hours before the event's start time.]";
            case "1d" -> "\n\n[INSTRUCTION: Schedule the reminder to fire the day before the event at 09:00.]";
            default   -> "\n\n[INSTRUCTION: Schedule the reminder to fire exactly at the event's start time.]";
        };
        var message = new MessengerMessage(chatId, MessengerType.TELEGRAM, forwardText + directive, username, userId, true);

        editText(chatId, messageId, BotMessages.get(BotMessages.Key.PROCESSING, displayLang));
        String result;
        try {
            result = reminderService.createReminder(message, displayLang);
        } catch (OffTopicRequestException e) {
            result = BotMessages.get(BotMessages.Key.OFFTOPIC, displayLang);
        } catch (MaxRemindersExceededException | RateLimitExceededException e) {
            result = e.getMessage();
        } catch (Exception e) {
            log.error("Failed to create forwarded reminder for chat {}: {}", chatId, e.getMessage(), e);
            result = BotMessages.get(BotMessages.Key.WRONG, displayLang);
        }
        editText(chatId, messageId, result);
    }

    /** Handles a lead-time tap for a directly-typed event: applies the offset to the parsed event time locally and persists. */
    private void handleEventLeadTime(String chatId, int messageId, String option, String languageCode) {
        var key = chatId + ":" + messageId;
        var profileLang = reminderService.getUserLanguage(chatId, MessengerType.TELEGRAM);
        var displayLang = profileLang != null ? profileLang : languageCode;
        if ("cancel".equals(option)) {
            leadDrafts.remove(key);
            deleteMessage(chatId, messageId);
            return;
        }
        var draft = leadDrafts.remove(key);
        if (draft == null) {
            editText(chatId, messageId, BotMessages.get(BotMessages.Key.WRONG, displayLang));
            return;
        }
        var event = draft.eventFireAt();
        var fireAt = switch (option) {
            case "1h" -> event.minusHours(1);
            case "3h" -> event.minusHours(3);
            case "1d" -> event.minusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
            default   -> event;
        };
        editText(chatId, messageId, BotMessages.get(BotMessages.Key.PROCESSING, displayLang));
        String result;
        try {
            // Fall back to the event time itself if the offset lands in the past.
            result = reminderService.createTimedReminder(chatId, MessengerType.TELEGRAM,
                draft.reminderText(), fireAt, event, displayLang);
        } catch (MaxRemindersExceededException | RateLimitExceededException e) {
            result = e.getMessage();
        } catch (Exception e) {
            log.error("Failed to create lead-time reminder for chat {}: {}", chatId, e.getMessage(), e);
            result = BotMessages.get(BotMessages.Key.WRONG, displayLang);
        }
        editText(chatId, messageId, result);
    }

    /** Animates the placeholder by cycling a braille spinner frame. Returns a stopper to halt it. */
    private Runnable startSpinner(String chatId, int messageId, String processingText) {
        var stopped = new AtomicBoolean(false);
        var frame = new AtomicInteger(0);
        var future = spinnerExecutor.scheduleAtFixedRate(() -> {
            if (stopped.get()) return;
            var i = frame.incrementAndGet() % SPINNER_FRAMES.length;
            editText(chatId, messageId, SPINNER_FRAMES[i] + " " + processingText);
        }, SPINNER_INTERVAL_MS, SPINNER_INTERVAL_MS, TimeUnit.MILLISECONDS);
        return () -> {
            stopped.set(true);
            future.cancel(false);
        };
    }

    // ── Timezone onboarding ───────────────────────────────────────────────────

    private void sendTimezoneRequest(String chatId, String languageCode) {
        var row1 = new KeyboardRow();
        row1.add(KeyboardButton.builder().text(BotMessages.get(BotMessages.Key.TZ_SHARE_LOCATION, languageCode)).requestLocation(true).build());

        var row2 = new KeyboardRow();
        row2.add(KeyboardButton.builder().text("🌍 Europe").build());
        row2.add(KeyboardButton.builder().text("🌎 Americas").build());
        row2.add(KeyboardButton.builder().text("🌏 Asia").build());

        var row3 = new KeyboardRow();
        row3.add(KeyboardButton.builder().text("🕌 Middle East").build());
        row3.add(KeyboardButton.builder().text("🌏 Pacific / AU").build());
        row3.add(KeyboardButton.builder().text("🌐 UTC / Other").build());

        var row4 = new KeyboardRow();
        row4.add(KeyboardButton.builder().text(BotMessages.get(BotMessages.Key.BTN_CANCEL, languageCode)).build());

        var keyboard = ReplyKeyboardMarkup.builder()
            .keyboard(List.of(row1, row2, row3, row4))
            .resizeKeyboard(true)
            .oneTimeKeyboard(false)
            .build();

        var currentTz = reminderService.getUserTimezone(chatId, MessengerType.TELEGRAM);
        var baseMsg = BotMessages.get(BotMessages.Key.ASK_TIMEZONE, languageCode);
        var msg = (currentTz != null && !currentTz.equals("UTC"))
            ? "🕐 " + currentTz + "\n\n" + baseMsg
            : baseMsg;

        sendWithMarkup(chatId, msg, keyboard);
    }

    private void handleLocationMessage(String chatId, Location location, String languageCode) {
        sendWithMarkup(chatId, BotMessages.get(BotMessages.Key.TZ_LOCATING, languageCode),
            ReplyKeyboardRemove.builder().removeKeyboard(true).build());

        reminderService.saveLocation(chatId, MessengerType.TELEGRAM,
            location.getLatitude(), location.getLongitude(), languageCode);

        var detected = timezoneGeoService.findTimezone(location.getLatitude(), location.getLongitude());

        if (detected.isEmpty()) {
            sendWithMarkup(chatId,
                BotMessages.get(BotMessages.Key.TZ_CHOOSE_REGION, languageCode),
                buildRegionPicker());
            return;
        }

        var tz = detected.get();
        var offsetStr = formatOffset(tz);
        var keyboard = InlineKeyboardMarkup.builder()
            .keyboardRow(new InlineKeyboardRow(List.of(
                InlineKeyboardButton.builder()
                    .text("✅ " + tz + " (" + offsetStr + ")")
                    .callbackData("tz:s:" + tz)
                    .build(),
                InlineKeyboardButton.builder()
                    .text(BotMessages.get(BotMessages.Key.TZ_CHOOSE_DIFFERENTLY, languageCode))
                    .callbackData("tz:regions")
                    .build()
            )))
            .build();

        sendWithMarkup(chatId,
            BotMessages.get(BotMessages.Key.TZ_DETECTED, languageCode, tz + " (" + offsetStr + ")"),
            keyboard);
    }

    private void handleRegionButtonTap(String chatId, String regionLabel, String languageCode) {
        // First message removes the reply keyboard
        sendWithMarkup(chatId,
            BotMessages.get(BotMessages.Key.TZ_CHOOSE_REGION, languageCode),
            ReplyKeyboardRemove.builder().removeKeyboard(true).build());
        // Second message shows the inline timezone picker for the selected region
        sendWithMarkup(chatId,
            BotMessages.get(BotMessages.Key.TZ_CHOOSE_REGION, languageCode),
            buildTimezonePicker(regionLabel));
    }

    // ── Callback query handling ───────────────────────────────────────────────

    private void handleCallbackQuery(CallbackQuery cb) {
        var chatId = cb.getMessage().getChatId().toString();
        var messageId = cb.getMessage().getMessageId();
        var data = cb.getData();
        var from = cb.getFrom();
        var languageCode = from != null ? from.getLanguageCode() : null;

        try {
            telegramClient.execute(AnswerCallbackQuery.builder().callbackQueryId(cb.getId()).build());
        } catch (TelegramApiException ignored) {}

        if (data == null) return;

        if (data.startsWith("del:confirm:")) {
            try {
                long reminderId = Long.parseLong(data.substring("del:confirm:".length()));
                var lang = reminderService.getUserLanguage(chatId, MessengerType.TELEGRAM);
                var displayLang = lang != null ? lang : languageCode;
                var confirmRow = new InlineKeyboardRow();
                confirmRow.add(InlineKeyboardButton.builder()
                    .text(BotMessages.get(BotMessages.Key.BTN_CONFIRM_DELETE, displayLang))
                    .callbackData("del:" + reminderId)
                    .build());
                confirmRow.add(InlineKeyboardButton.builder()
                    .text(BotMessages.get(BotMessages.Key.BTN_CANCEL, displayLang))
                    .callbackData("del:cancel")
                    .build());
                editInlineKeyboard(chatId, messageId, null,
                    InlineKeyboardMarkup.builder().keyboard(List.of(confirmRow)).build());
            } catch (Exception e) {
                log.warn("Failed to show delete confirm in chat {}: {}", chatId, e.getMessage());
            }
            return;
        }

        if ("del:cancel".equals(data)) {
            try {
                var lang = reminderService.getUserLanguage(chatId, MessengerType.TELEGRAM);
                var displayLang = lang != null ? lang : languageCode;
                var reminders = reminderService.getActiveReminders(chatId, MessengerType.TELEGRAM);
                var backToManage = reminders.isEmpty();
                var updatedText = reminderService.listReminders(chatId, MessengerType.TELEGRAM, !backToManage);
                editInlineKeyboard(chatId, messageId, updatedText,
                    backToManage
                        ? buildManageKeyboard(displayLang)
                        : buildNumberGrid(reminders, displayLang));
            } catch (Exception e) {
                log.warn("Failed to restore list keyboard in chat {}: {}", chatId, e.getMessage());
            }
            return;
        }

        if ("list:cancel".equals(data)) {
            deleteMessage(chatId, messageId);
            return;
        }

        if ("list:manage".equals(data)) {
            // Reveal the compact number grid and renumber the list so taps map to lines.
            var reminders = reminderService.getActiveReminders(chatId, MessengerType.TELEGRAM);
            var lang = reminderService.getUserLanguage(chatId, MessengerType.TELEGRAM);
            var displayLang = lang != null ? lang : languageCode;
            if (reminders.isEmpty()) {
                editInlineKeyboard(chatId, messageId,
                    reminderService.listReminders(chatId, MessengerType.TELEGRAM), buildManageKeyboard(displayLang));
            } else {
                editInlineKeyboard(chatId, messageId,
                    reminderService.listReminders(chatId, MessengerType.TELEGRAM, true), buildNumberGrid(reminders, displayLang));
            }
            return;
        }

        if ("list:done".equals(data)) {
            // Collapse the grid back to the single Manage button and drop the line numbers.
            var lang = reminderService.getUserLanguage(chatId, MessengerType.TELEGRAM);
            var displayLang = lang != null ? lang : languageCode;
            editInlineKeyboard(chatId, messageId,
                reminderService.listReminders(chatId, MessengerType.TELEGRAM), buildManageKeyboard(displayLang));
            return;
        }

        if (data.startsWith("fwd:")) {
            var userId = from != null ? from.getId() : null;
            var username = from != null ? from.getUserName() : null;
            handleForwardLeadTime(chatId, messageId, data.substring("fwd:".length()), userId, username, languageCode);
            return;
        }

        if (data.startsWith("lead:")) {
            handleEventLeadTime(chatId, messageId, data.substring("lead:".length()), languageCode);
            return;
        }

        if (data.startsWith("del:")) {
            try {
                long reminderId = Long.parseLong(data.substring("del:".length()));
                var result = reminderService.deleteReminder(chatId, MessengerType.TELEGRAM, reminderId);
                // Refresh the list message with updated reminders
                var reminders = reminderService.getActiveReminders(chatId, MessengerType.TELEGRAM);
                var lang = reminderService.getUserLanguage(chatId, MessengerType.TELEGRAM);
                var displayLang = lang != null ? lang : languageCode;
                if (reminders.isEmpty()) {
                    editInlineKeyboard(chatId, messageId,
                        result + "\n\n" + BotMessages.get(BotMessages.Key.NO_REMINDERS, displayLang),
                        InlineKeyboardMarkup.builder().keyboard(List.of()).build());
                } else {
                    var updatedText = reminderService.listReminders(chatId, MessengerType.TELEGRAM, true);
                    editInlineKeyboard(chatId, messageId, updatedText, buildNumberGrid(reminders, displayLang));
                }
            } catch (Exception e) {
                log.warn("Failed to delete reminder via button in chat {}: {}", chatId, e.getMessage());
            }
            return;
        }

        if ("tz:regions".equals(data)) {
            editInlineKeyboard(chatId, messageId,
                BotMessages.get(BotMessages.Key.TZ_CHOOSE_REGION, languageCode),
                buildRegionPicker());

        } else if (data.startsWith("tz:r:")) {
            var idxStr = data.substring("tz:r:".length());
            try {
                int idx = Integer.parseInt(idxStr);
                if (idx >= 0 && idx < REGION_ORDER.size()) {
                    var regionLabel = REGION_ORDER.get(idx);
                    editInlineKeyboard(chatId, messageId, regionLabel, buildTimezonePicker(regionLabel));
                }
            } catch (NumberFormatException ignored) {}

        } else if (data.startsWith("tz:s:")) {
            var tz = data.substring("tz:s:".length());
            try {
                ZoneId.of(tz); // validate
                reminderService.confirmTimezone(chatId, MessengerType.TELEGRAM, tz, languageCode);
                var profileLang = reminderService.getUserLanguage(chatId, MessengerType.TELEGRAM);
                var displayLang = profileLang != null ? profileLang : languageCode;
                removeInlineKeyboard(chatId, messageId);
                sendWithMarkup(chatId, BotMessages.get(BotMessages.Key.TZ_CONFIRMED, displayLang, tz), buildMainKeyboard(displayLang));
                replayPendingReminder(chatId, displayLang);
            } catch (Exception e) {
                log.warn("Failed to confirm timezone {} for chat {}: {}", tz, chatId, e.getMessage());
            }

        } else if ("tz:cancel".equals(data)) {
            var profileLang = reminderService.getUserLanguage(chatId, MessengerType.TELEGRAM);
            var displayLang = profileLang != null ? profileLang : languageCode;
            removeInlineKeyboard(chatId, messageId);
            sendWithMarkup(chatId, BotMessages.get(BotMessages.Key.BTN_LIST, displayLang), buildMainKeyboard(displayLang));
        }
    }

    // ── Keyboard builders ─────────────────────────────────────────────────────

    private ReplyKeyboardMarkup buildMainKeyboard(String languageCode) {
        var row = new KeyboardRow();
        row.add(KeyboardButton.builder().text(BotMessages.get(BotMessages.Key.BTN_LIST, languageCode)).build());
        return ReplyKeyboardMarkup.builder()
            .keyboard(List.of(row))
            .resizeKeyboard(true)
            .oneTimeKeyboard(false)
            .build();
    }

    /** Read-only list footer: "Manage" reveals the delete grid; "Cancel" closes the message. */
    private InlineKeyboardMarkup buildManageKeyboard(String languageCode) {
        return InlineKeyboardMarkup.builder().keyboard(List.of(new InlineKeyboardRow(List.of(
            InlineKeyboardButton.builder()
                .text(BotMessages.get(BotMessages.Key.BTN_MANAGE, languageCode))
                .callbackData("list:manage")
                .build(),
            InlineKeyboardButton.builder()
                .text(BotMessages.get(BotMessages.Key.BTN_CANCEL, languageCode))
                .callbackData("list:cancel")
                .build())))).build();
    }

    /**
     * Compact delete grid: numbered buttons (5 per row) matching the numbered list lines, plus a Done button.
     * Far denser than one full-width button per reminder when the user has many.
     */
    private InlineKeyboardMarkup buildNumberGrid(List<Reminder> reminders, String languageCode) {
        var rows = new ArrayList<InlineKeyboardRow>();
        var row = new InlineKeyboardRow();
        int index = 1;
        for (var reminder : reminders) {
            row.add(InlineKeyboardButton.builder()
                .text(String.valueOf(index++))
                .callbackData("del:confirm:" + reminder.getId())
                .build());
            if (row.size() == 5) {
                rows.add(new InlineKeyboardRow(row));
                row = new InlineKeyboardRow();
            }
        }
        if (!row.isEmpty()) rows.add(new InlineKeyboardRow(row));
        rows.add(new InlineKeyboardRow(List.of(
            InlineKeyboardButton.builder()
                .text(BotMessages.get(BotMessages.Key.BTN_DONE, languageCode))
                .callbackData("list:done")
                .build())));
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    private InlineKeyboardMarkup buildRegionPicker() {
        var rows = new ArrayList<InlineKeyboardRow>();
        var row = new InlineKeyboardRow();
        for (int i = 0; i < REGION_ORDER.size(); i++) {
            row.add(InlineKeyboardButton.builder()
                .text(REGION_ORDER.get(i))
                .callbackData("tz:r:" + i)
                .build());
            if (row.size() == 2) {
                rows.add(new InlineKeyboardRow(row));
                row.clear();
            }
        }
        if (!row.isEmpty()) rows.add(new InlineKeyboardRow(row));
        rows.add(new InlineKeyboardRow(List.of(InlineKeyboardButton.builder().text("✖️ Cancel").callbackData("tz:cancel").build())));
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    private InlineKeyboardMarkup buildTimezonePicker(String regionLabel) {
        var options = REGIONS.getOrDefault(regionLabel, List.of());
        var rows = new ArrayList<InlineKeyboardRow>();
        var row = new InlineKeyboardRow();
        for (var opt : options) {
            row.add(InlineKeyboardButton.builder()
                .text(opt.label() + "  " + formatOffset(opt.tz()))
                .callbackData("tz:s:" + opt.tz())
                .build());
            if (row.size() == 2) {
                rows.add(new InlineKeyboardRow(row));
                row.clear();
            }
        }
        if (!row.isEmpty()) rows.add(new InlineKeyboardRow(row));
        // Back to regions + Cancel
        rows.add(new InlineKeyboardRow(List.of(
            InlineKeyboardButton.builder().text("« Back").callbackData("tz:regions").build(),
            InlineKeyboardButton.builder().text("✖️ Cancel").callbackData("tz:cancel").build()
        )));
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    // ── Telegram send helpers ─────────────────────────────────────────────────

    private void sendWithMarkup(String chatId, String text, org.telegram.telegrambots.meta.api.interfaces.BotApiObject markup) {
        try {
            var builder = SendMessage.builder().chatId(chatId).text(text).parseMode("HTML");
            if (markup instanceof org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard kb) {
                builder.replyMarkup(kb);
            } else if (markup instanceof InlineKeyboardMarkup inlineKb) {
                builder.replyMarkup(inlineKb);
            }
            telegramClient.execute(builder.build());
        } catch (TelegramApiException e) {
            log.error("Failed to send Telegram message to {}: {}", chatId, e.getMessage());
        }
    }

    /** Sends a message and returns its Telegram message id, or null if sending failed. */
    private Integer sendReturningId(String chatId, String text) {
        try {
            var sent = telegramClient.execute(
                SendMessage.builder().chatId(chatId).text(text).parseMode("HTML").build());
            return sent != null ? sent.getMessageId() : null;
        } catch (TelegramApiException e) {
            log.error("Failed to send Telegram message to {}: {}", chatId, e.getMessage());
            return null;
        }
    }

    /** Sends a message with an inline keyboard and returns its Telegram message id, or null if sending failed. */
    private Integer sendReturningIdWithMarkup(String chatId, String text, InlineKeyboardMarkup keyboard) {
        try {
            var sent = telegramClient.execute(
                SendMessage.builder().chatId(chatId).text(text).parseMode("HTML").replyMarkup(keyboard).build());
            return sent != null ? sent.getMessageId() : null;
        } catch (TelegramApiException e) {
            log.error("Failed to send Telegram message to {}: {}", chatId, e.getMessage());
            return null;
        }
    }

    /** Replaces the text of an existing message (no inline keyboard). */
    private void editText(String chatId, int messageId, String text) {
        try {
            telegramClient.execute(
                org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(text)
                    .parseMode("HTML")
                    .build());
        } catch (TelegramApiException e) {
            log.warn("Failed to edit message {} in chat {}: {}", messageId, chatId, e.getMessage());
        }
    }

    private void editInlineKeyboard(String chatId, int messageId, String text, InlineKeyboardMarkup keyboard) {
        try {
            if (text == null) {
                telegramClient.execute(
                    EditMessageReplyMarkup.builder()
                        .chatId(chatId)
                        .messageId(messageId)
                        .replyMarkup(keyboard)
                        .build());
            } else {
                telegramClient.execute(
                    org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(messageId)
                        .text(text)
                        .parseMode("HTML")
                        .replyMarkup(keyboard)
                        .build());
            }
        } catch (TelegramApiException e) {
            log.warn("Failed to edit message {} in chat {}: {}", messageId, chatId, e.getMessage());
        }
    }

    private void deleteMessage(String chatId, int messageId) {
        try {
            telegramClient.execute(
                DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build());
        } catch (TelegramApiException e) {
            log.warn("Failed to delete message {} in chat {}: {}", messageId, chatId, e.getMessage());
        }
    }

    private void removeInlineKeyboard(String chatId, int messageId) {
        try {
            telegramClient.execute(
                EditMessageReplyMarkup.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .replyMarkup(InlineKeyboardMarkup.builder().keyboard(List.of()).build())
                    .build());
        } catch (TelegramApiException e) {
            log.warn("Failed to remove inline keyboard from message {} in chat {}: {}", messageId, chatId, e.getMessage());
        }
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private String normalizeCommand(String text) {
        return text.replaceFirst("@\\S+", "");
    }

    private static String formatOffset(String tz) {
        try {
            var zone = ZoneId.of(tz);
            var offset = zone.getRules().getOffset(java.time.Instant.now());
            var totalSeconds = offset.getTotalSeconds();
            var hours = totalSeconds / 3600;
            var minutes = Math.abs((totalSeconds % 3600) / 60);
            return minutes == 0
                ? String.format("UTC%+d", hours)
                : String.format("UTC%+d:%02d", hours, minutes);
        } catch (Exception e) {
            return "UTC";
        }
    }
}

