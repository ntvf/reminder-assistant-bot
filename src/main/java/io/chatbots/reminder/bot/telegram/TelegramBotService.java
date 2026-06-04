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
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private record TimezoneOption(String label, String tz) {}

    private final String botToken;
    private final String botUsername;
    private final TelegramClient telegramClient;
    private final ReminderService reminderService;
    private final StatisticsService statisticsService;
    private final TimezoneGeoService timezoneGeoService;
    private final AppProperties appProperties;

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
                var reminderIds = reminderService.getActiveReminderIds(chatId, MessengerType.TELEGRAM);
                if (reminderIds.isEmpty()) {
                    send(chatId, listText);
                } else {
                    sendWithMarkup(chatId, listText, buildDeleteKeyboard(reminderIds));
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
            } else if (text.startsWith("/stats")) {
                send(chatId, statisticsService.buildStatsReport());
            } else if (text.startsWith("/")) {
                send(chatId, BotMessages.get(BotMessages.Key.UNKNOWN_CMD, displayLang));
            } else if (!text.isBlank()) {
                send(chatId, reminderService.createReminder(messengerMessage, languageCode));
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

        var keyboard = ReplyKeyboardMarkup.builder()
            .keyboard(List.of(row1, row2, row3))
            .resizeKeyboard(true)
            .oneTimeKeyboard(false)
            .build();

        sendWithMarkup(chatId, BotMessages.get(BotMessages.Key.ASK_TIMEZONE, languageCode), keyboard);
    }

    private void handleLocationMessage(String chatId, Location location, String languageCode) {
        sendWithMarkup(chatId, BotMessages.get(BotMessages.Key.TZ_LOCATING, languageCode),
            ReplyKeyboardRemove.builder().removeKeyboard(true).build());

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
        // Dismiss the location/region reply keyboard and show inline timezone list
        sendWithMarkup(chatId,
            BotMessages.get(BotMessages.Key.TZ_CHOOSE_REGION, languageCode),
            ReplyKeyboardRemove.builder().removeKeyboard(true).build());
        sendWithMarkup(chatId, regionLabel, buildTimezonePicker(regionLabel));
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

        if (data.startsWith("del:")) {
            try {
                long reminderId = Long.parseLong(data.substring("del:".length()));
                var result = reminderService.deleteReminder(chatId, MessengerType.TELEGRAM, reminderId);
                // Refresh the list message with updated reminders
                var ids = reminderService.getActiveReminderIds(chatId, MessengerType.TELEGRAM);
                if (ids.isEmpty()) {
                    var lang = reminderService.getUserLanguage(chatId, MessengerType.TELEGRAM);
                    editInlineKeyboard(chatId, messageId,
                        result + "\n\n" + BotMessages.get(BotMessages.Key.NO_REMINDERS, lang),
                        InlineKeyboardMarkup.builder().keyboard(List.of()).build());
                } else {
                    var updatedText = reminderService.listReminders(chatId, MessengerType.TELEGRAM);
                    editInlineKeyboard(chatId, messageId, updatedText, buildDeleteKeyboard(ids));
                }
            } catch (Exception e) {
                log.warn("Failed to delete reminder via button in chat {}: {}", chatId, e.getMessage());
            }
            return;
        }

        if ("tz:regions".equals(data)) {            editInlineKeyboard(chatId, messageId,
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
                removeInlineKeyboard(chatId, messageId);
                sendWithMarkup(chatId, BotMessages.get(BotMessages.Key.TZ_CONFIRMED, languageCode, tz), buildMainKeyboard(languageCode));
            } catch (Exception e) {
                log.warn("Failed to confirm timezone {} for chat {}: {}", tz, chatId, e.getMessage());
            }
        }
    }

    // ── Keyboard builders ─────────────────────────────────────────────────────

    private ReplyKeyboardMarkup buildMainKeyboard(String languageCode) {
        var row = new KeyboardRow();
        row.add(KeyboardButton.builder().text(BotMessages.get(BotMessages.Key.BTN_LIST, languageCode)).build());
        row.add(KeyboardButton.builder().text(BotMessages.get(BotMessages.Key.BTN_CHANGE_TZ, languageCode)).build());
        return ReplyKeyboardMarkup.builder()
            .keyboard(List.of(row))
            .resizeKeyboard(true)
            .oneTimeKeyboard(false)
            .build();
    }

    private InlineKeyboardMarkup buildDeleteKeyboard(List<Long> ids) {
        var rows = new ArrayList<InlineKeyboardRow>();
        var row = new InlineKeyboardRow();
        for (var id : ids) {
            row.add(InlineKeyboardButton.builder()
                .text("🗑 " + id)
                .callbackData("del:" + id)
                .build());
            if (row.size() == 3) {
                rows.add(new InlineKeyboardRow(row));
                row.clear();
            }
        }
        if (!row.isEmpty()) rows.add(new InlineKeyboardRow(row));
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
        // Back button
        rows.add(new InlineKeyboardRow(List.of(InlineKeyboardButton.builder().text("« Back").callbackData("tz:regions").build())));
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    // ── Telegram send helpers ─────────────────────────────────────────────────

    private void sendWithMarkup(String chatId, String text, org.telegram.telegrambots.meta.api.interfaces.BotApiObject markup) {
        try {
            var builder = SendMessage.builder().chatId(chatId).text(text);
            if (markup instanceof org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard kb) {
                builder.replyMarkup(kb);
            }
            telegramClient.execute(builder.build());
        } catch (TelegramApiException e) {
            log.error("Failed to send Telegram message to {}: {}", chatId, e.getMessage());
        }
    }

    private void editInlineKeyboard(String chatId, int messageId, String text, InlineKeyboardMarkup keyboard) {
        try {
            telegramClient.execute(
                org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(text)
                    .replyMarkup(keyboard)
                    .build());
        } catch (TelegramApiException e) {
            log.warn("Failed to edit message {} in chat {}: {}", messageId, chatId, e.getMessage());
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

