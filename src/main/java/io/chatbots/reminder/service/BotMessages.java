package io.chatbots.reminder.service;

import java.util.Map;

public final class BotMessages {

    public enum Key {
        REMINDER_SET, LEAD_UP_HEADER, NO_REMINDERS, REMINDERS_HEADER,
        DELETED, NOT_FOUND, TZ_UPDATED, INVALID_TZ, INVALID_SCHEDULE,
        CRON_TOO_FREQUENT, FIREAT_INVALID,
        WRONG, INVALID_ID, UNKNOWN_CMD, MAX_REMINDERS, OFFTOPIC, RATE_LIMITED, START,
        ASK_TIMEZONE, TZ_DETECTED, TZ_CONFIRMED, TZ_CHOOSE_REGION,
        TZ_LOCATING, TZ_CHOOSE_DIFFERENTLY, TZ_SHARE_LOCATION,
        BTN_LIST, BTN_CHANGE_TZ,
        REMINDER_FIRED
    }

    private static final Map<Key, String> EN = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Reminder set!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Also scheduled lead-up reminders:"),
        Map.entry(Key.NO_REMINDERS, "You have no active reminders."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Your active reminders:"),
        Map.entry(Key.DELETED, "✅ Reminder [%d] deleted."),
        Map.entry(Key.NOT_FOUND, "Reminder not found."),
        Map.entry(Key.TZ_UPDATED, "✅ Timezone updated to %s."),
        Map.entry(Key.INVALID_TZ, "❌ Invalid timezone: %s. Use IANA format (e.g. Europe/London, America/New_York)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Could not create reminder: invalid schedule. Please try rephrasing."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Reminder schedule is too frequent. Minimum interval is 30 minutes."),
        Map.entry(Key.FIREAT_INVALID, "❌ Reminder time must be in the future and within 50 years."),
        Map.entry(Key.WRONG, "Something went wrong. Please try again."),
        Map.entry(Key.INVALID_ID, "Invalid reminder ID. Use /list to see your reminders."),
        Map.entry(Key.UNKNOWN_CMD, "Unknown command. Type anything to set a reminder."),
        Map.entry(Key.MAX_REMINDERS, "You've reached the maximum of %d active reminders. Delete some to add new ones."),
        Map.entry(Key.OFFTOPIC, "I can only help you set up reminders. Please tell me what and when to remind you."),
        Map.entry(Key.RATE_LIMITED, "⚠️ You've reached the daily limit of %d reminder requests. Try again tomorrow."),
        Map.entry(Key.START, "👋 Hello, %s! I'm your Reminder Assistant.\n\nJust tell me what to remind you about:\n• Remind me to take my pill every day at 8am\n• Mom's birthday is March 5th → yearly chain of reminders\n• My flight is next Tuesday at 14 → trip reminders\n• Oil was changed in the car → I'll remind you in a year\n\nUse the buttons below or just type naturally."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Share your location to automatically set your timezone.\n\nOr pick a region below:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Detected timezone: %s\nIs this correct?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Timezone set to %s. Your reminders will use this timezone."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Choose your region:"),
        Map.entry(Key.TZ_LOCATING, "📍 Got your location, detecting timezone…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Choose differently"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Share Location"),
        Map.entry(Key.BTN_LIST, "📋 My Reminders"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Change Timezone"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s")    );

    private static final Map<Key, String> RU = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Напоминание создано!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Также запланированы предварительные напоминания:"),
        Map.entry(Key.NO_REMINDERS, "У вас нет активных напоминаний."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Ваши активные напоминания:"),
        Map.entry(Key.DELETED, "✅ Напоминание [%d] удалено."),
        Map.entry(Key.NOT_FOUND, "Напоминание не найдено."),
        Map.entry(Key.TZ_UPDATED, "✅ Часовой пояс обновлён: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Неверный часовой пояс: %s. Используйте формат IANA (например, Europe/Moscow)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Не удалось создать напоминание: неверное расписание. Попробуйте переформулировать."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Расписание слишком частое. Минимальный интервал — 30 минут."),
        Map.entry(Key.FIREAT_INVALID, "❌ Время напоминания должно быть в будущем и не более чем через 50 лет."),
        Map.entry(Key.WRONG, "Что-то пошло не так. Попробуйте снова."),
        Map.entry(Key.INVALID_ID, "Неверный ID. Используйте /list для просмотра напоминаний."),
        Map.entry(Key.UNKNOWN_CMD, "Неизвестная команда. Просто напиши, что нужно напомнить."),
        Map.entry(Key.MAX_REMINDERS, "Достигнут максимум %d активных напоминаний. Удали некоторые, чтобы добавить новые."),
        Map.entry(Key.OFFTOPIC, "Я могу только помогать настраивать напоминания. Напиши, о чём и когда напомнить."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Вы достигли дневного лимита %d запросов на напоминания. Попробуй снова завтра."),
        Map.entry(Key.START, "👋 Привет, %s! Я твой Помощник-напоминалка.\n\nНапиши что и когда напомнить — и готово:\n• Напомни выпить таблетку каждый день в 20\n• День рождения мамы — 5 марта → цепочка ежегодных напоминаний\n• Мой рейс в следующий вторник в 14 → напомнит о поездке\n• Масло заменено в машине → напомню через год\n\nИспользуй кнопки ниже или просто пиши."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Поделись местоположением для автоматического определения часового пояса.\n\nИли выбери регион ниже:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Определён часовой пояс: %s\nВерно?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Часовой пояс установлен: %s. Напоминания будут использовать этот пояс."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Выбери регион:"),
        Map.entry(Key.TZ_LOCATING, "📍 Местоположение получено, определяю часовой пояс…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Выбрать другой"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Поделиться местоположением"),
        Map.entry(Key.BTN_LIST, "📋 Мои напоминания"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Изменить часовой пояс"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s")
    );

    private static final Map<Key, String> DE = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Erinnerung gesetzt!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Vorab-Erinnerungen auch geplant:"),
        Map.entry(Key.NO_REMINDERS, "Sie haben keine aktiven Erinnerungen."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Ihre aktiven Erinnerungen:"),
        Map.entry(Key.DELETED, "✅ Erinnerung [%d] gelöscht."),
        Map.entry(Key.NOT_FOUND, "Erinnerung nicht gefunden."),
        Map.entry(Key.TZ_UPDATED, "✅ Zeitzone aktualisiert: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Ungültige Zeitzone: %s. Bitte IANA-Format verwenden (z.B. Europe/Berlin)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Erinnerung konnte nicht erstellt werden: ungültiger Zeitplan. Bitte neu formulieren."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Zeitplan zu häufig. Mindestintervall: 30 Minuten."),
        Map.entry(Key.FIREAT_INVALID, "❌ Der Erinnerungszeitpunkt muss in der Zukunft liegen (max. 50 Jahre)."),
        Map.entry(Key.WRONG, "Etwas ist schiefgelaufen. Bitte versuchen Sie es erneut."),
        Map.entry(Key.INVALID_ID, "Ungültige ID. Verwenden Sie /list zum Anzeigen."),
        Map.entry(Key.UNKNOWN_CMD, "Unbekannter Befehl. Schreib einfach, woran du erinnert werden möchtest."),
        Map.entry(Key.MAX_REMINDERS, "Maximum von %d Erinnerungen erreicht. Lösch einige, um neue hinzuzufügen."),
        Map.entry(Key.OFFTOPIC, "Ich kann nur beim Einrichten von Erinnerungen helfen. Sag mir, woran und wann."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Du hast das Tageslimit von %d Erinnerungsanfragen erreicht. Versuche es morgen erneut."),
        Map.entry(Key.START, "👋 Hallo, %s! Ich bin dein Erinnerungsassistent.\n\nSag mir einfach, woran du erinnert werden möchtest:\n• Erinnere mich täglich um 8 Uhr, meine Tablette zu nehmen\n• Mamas Geburtstag ist am 5. März → jährliche Erinnerungskette\n• Mein Flug ist nächsten Dienstag um 14 → Reiseerinnerungen\n• Öl im Auto gewechselt → Erinnerung in einem Jahr\n\nNutze die Schaltflächen unten oder schreib einfach."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Teile deinen Standort, um die Zeitzone automatisch zu erkennen.\n\nOder wähle unten eine Region:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Erkannte Zeitzone: %s\nIst das korrekt?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Zeitzone festgelegt: %s. Deine Erinnerungen verwenden diese Zeitzone."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Wähle deine Region:"),
        Map.entry(Key.TZ_LOCATING, "📍 Standort erhalten, erkenne Zeitzone…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Andere wählen"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Standort teilen"),
        Map.entry(Key.BTN_LIST, "📋 Meine Erinnerungen"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Zeitzone ändern"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s")
    );

    private static final Map<Key, String> FR = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Rappel créé !"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Rappels préparatoires aussi planifiés :"),
        Map.entry(Key.NO_REMINDERS, "Vous n'avez pas de rappels actifs."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Vos rappels actifs :"),
        Map.entry(Key.DELETED, "✅ Rappel [%d] supprimé."),
        Map.entry(Key.NOT_FOUND, "Rappel introuvable."),
        Map.entry(Key.TZ_UPDATED, "✅ Fuseau horaire mis à jour : %s."),
        Map.entry(Key.INVALID_TZ, "❌ Fuseau horaire invalide : %s. Utilisez le format IANA (ex. Europe/Paris)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Impossible de créer le rappel : calendrier invalide. Essayez de reformuler."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Fréquence trop élevée. Intervalle minimum : 30 minutes."),
        Map.entry(Key.FIREAT_INVALID, "❌ La date du rappel doit être dans le futur (max 50 ans)."),
        Map.entry(Key.WRONG, "Une erreur s'est produite. Veuillez réessayer."),
        Map.entry(Key.INVALID_ID, "ID de rappel invalide. Utilisez /list pour voir vos rappels."),
        Map.entry(Key.UNKNOWN_CMD, "Commande inconnue. Écris simplement ce dont tu veux te souvenir."),
        Map.entry(Key.MAX_REMINDERS, "Maximum de %d rappels atteint. Supprime-en quelques-uns pour en ajouter."),
        Map.entry(Key.OFFTOPIC, "Je peux seulement t'aider à créer des rappels. Dis-moi quoi et quand."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Tu as atteint la limite quotidienne de %d requêtes de rappels. Réessaie demain."),
        Map.entry(Key.START, "👋 Bonjour, %s ! Je suis ton Assistant de rappels.\n\nDis-moi simplement ce dont tu veux te souvenir :\n• Rappelle-moi de prendre mon médicament tous les jours à 8h\n• L'anniversaire de maman est le 5 mars → chaîne de rappels annuels\n• Mon vol est mardi prochain à 14h → rappels avant le départ\n• Huile de voiture changée → je te rappellerai dans un an\n\nUtilise les boutons ci-dessous ou écris directement."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Partage ta position pour détecter automatiquement ton fuseau horaire.\n\nOu choisis une région ci-dessous :"),
        Map.entry(Key.TZ_DETECTED, "🌍 Fuseau horaire détecté : %s\nEst-ce correct ?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Fuseau horaire défini : %s. Tes rappels utiliseront ce fuseau."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Choisis ta région :"),
        Map.entry(Key.TZ_LOCATING, "📍 Position reçue, détection du fuseau horaire…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Choisir autrement"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Partager ma position"),
        Map.entry(Key.BTN_LIST, "📋 Mes rappels"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Changer de fuseau"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s")
    );

    private static final Map<Key, String> ES = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ ¡Recordatorio creado!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Recordatorios previos también programados:"),
        Map.entry(Key.NO_REMINDERS, "No tienes recordatorios activos."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Tus recordatorios activos:"),
        Map.entry(Key.DELETED, "✅ Recordatorio [%d] eliminado."),
        Map.entry(Key.NOT_FOUND, "Recordatorio no encontrado."),
        Map.entry(Key.TZ_UPDATED, "✅ Zona horaria actualizada: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Zona horaria inválida: %s. Usa el formato IANA (p. ej. Europe/Madrid)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ No se pudo crear el recordatorio: programación inválida. Intenta reformular."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Frecuencia demasiado alta. Intervalo mínimo: 30 minutos."),
        Map.entry(Key.FIREAT_INVALID, "❌ La hora del recordatorio debe ser futura (máx. 50 años)."),
        Map.entry(Key.WRONG, "Algo salió mal. Por favor, inténtalo de nuevo."),
        Map.entry(Key.INVALID_ID, "ID inválido. Usa /list para ver tus recordatorios."),
        Map.entry(Key.UNKNOWN_CMD, "Comando desconocido. Escribe directamente lo que quieres recordar."),
        Map.entry(Key.MAX_REMINDERS, "Alcanzaste el máximo de %d recordatorios. Elimina algunos para agregar nuevos."),
        Map.entry(Key.OFFTOPIC, "Solo puedo ayudarte a configurar recordatorios. Dime qué y cuándo recordarte."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Has alcanzado el límite diario de %d solicitudes de recordatorios. Inténtalo mañana."),
        Map.entry(Key.START, "👋 ¡Hola, %s! Soy tu Asistente de recordatorios.\n\nDime en lenguaje natural qué necesitas recordar:\n• Recuérdame tomar mi pastilla cada día a las 8\n• El cumpleaños de mamá es el 5 de marzo → cadena de recordatorios anuales\n• Mi vuelo es el próximo martes a las 14 → recordatorios previos al viaje\n• Cambié el aceite del coche → te lo recuerdo en un año\n\nUsa los botones de abajo o simplemente escríbeme."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Comparte tu ubicación para detectar automáticamente tu zona horaria.\n\nO elige una región abajo:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Zona horaria detectada: %s\n¿Es correcto?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Zona horaria establecida: %s. Tus recordatorios usarán esta zona."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Elige tu región:"),
        Map.entry(Key.TZ_LOCATING, "📍 Ubicación recibida, detectando zona horaria…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Elegir otra zona"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Compartir ubicación"),
        Map.entry(Key.BTN_LIST, "📋 Mis recordatorios"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Cambiar zona horaria"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s")
    );

    private static final Map<Key, String> PT = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Lembrete criado!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Lembretes prévios também agendados:"),
        Map.entry(Key.NO_REMINDERS, "Você não tem lembretes ativos."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Seus lembretes ativos:"),
        Map.entry(Key.DELETED, "✅ Lembrete [%d] excluído."),
        Map.entry(Key.NOT_FOUND, "Lembrete não encontrado."),
        Map.entry(Key.TZ_UPDATED, "✅ Fuso horário atualizado: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Fuso horário inválido: %s. Use o formato IANA (ex. Europe/Lisbon)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Não foi possível criar o lembrete: programação inválida. Tente reformular."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Frequência muito alta. Intervalo mínimo: 30 minutos."),
        Map.entry(Key.FIREAT_INVALID, "❌ O horário do lembrete deve ser no futuro (máx. 50 anos)."),
        Map.entry(Key.WRONG, "Algo deu errado. Por favor, tente novamente."),
        Map.entry(Key.INVALID_ID, "ID inválido. Use /list para ver seus lembretes."),
        Map.entry(Key.UNKNOWN_CMD, "Comando desconhecido. Escreve diretamente o que queres lembrar."),
        Map.entry(Key.MAX_REMINDERS, "Você atingiu o máximo de %d lembretes. Apague alguns para adicionar novos."),
        Map.entry(Key.OFFTOPIC, "Só posso ajudar a configurar lembretes. Diga-me o quê e quando."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Atingiste o limite diário de %d pedidos de lembretes. Tenta novamente amanhã."),
        Map.entry(Key.START, "👋 Olá, %s! Sou o teu Assistente de lembretes.\n\nDiz-me em linguagem natural o que precisas lembrar:\n• Lembra-me de tomar o meu comprimido todos os dias às 8h\n• O aniversário da mãe é dia 5 de março → cadeia de lembretes anuais\n• O meu voo é na próxima terça às 14h → lembretes antes da viagem\n• Mudei o óleo do carro → lembro-te daqui a um ano\n\nUsa os botões abaixo ou escreve diretamente."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Partilha a tua localização para detetar automaticamente o fuso horário.\n\nOu escolhe uma região abaixo:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Fuso horário detetado: %s\nEstá correto?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Fuso horário definido: %s. Os teus lembretes usarão este fuso."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Escolhe a tua região:"),
        Map.entry(Key.TZ_LOCATING, "📍 Localização recebida, a detetar fuso horário…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Escolher outra zona"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Partilhar localização"),
        Map.entry(Key.BTN_LIST, "📋 Os meus lembretes"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Alterar fuso horário"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s")
    );

    private static final Map<Key, String> IT = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Promemoria impostato!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Promemoria anticipati anche programmati:"),
        Map.entry(Key.NO_REMINDERS, "Non hai promemoria attivi."),
        Map.entry(Key.REMINDERS_HEADER, "📋 I tuoi promemoria attivi:"),
        Map.entry(Key.DELETED, "✅ Promemoria [%d] eliminato."),
        Map.entry(Key.NOT_FOUND, "Promemoria non trovato."),
        Map.entry(Key.TZ_UPDATED, "✅ Fuso orario aggiornato: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Fuso orario non valido: %s. Usa il formato IANA (es. Europe/Rome)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Impossibile creare il promemoria: pianificazione non valida. Prova a riformulare."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Frequenza troppo alta. Intervallo minimo: 30 minuti."),
        Map.entry(Key.FIREAT_INVALID, "❌ L'orario del promemoria deve essere nel futuro (max 50 anni)."),
        Map.entry(Key.WRONG, "Qualcosa è andato storto. Per favore riprova."),
        Map.entry(Key.INVALID_ID, "ID non valido. Usa /list per vedere i tuoi promemoria."),
        Map.entry(Key.UNKNOWN_CMD, "Comando sconosciuto. Scrivi direttamente cosa vuoi ricordare."),
        Map.entry(Key.MAX_REMINDERS, "Hai raggiunto il massimo di %d promemoria. Eliminane alcuni per aggiungerne di nuovi."),
        Map.entry(Key.OFFTOPIC, "Posso solo aiutarti a impostare promemoria. Dimmi cosa e quando."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Hai raggiunto il limite giornaliero di %d richieste di promemoria. Riprova domani."),
        Map.entry(Key.START, "👋 Ciao, %s! Sono il tuo Assistente promemoria.\n\nDimmi in modo naturale cosa vuoi ricordare:\n• Ricordami di prendere la pillola ogni giorno alle 8\n• Il compleanno della mamma è il 5 marzo → catena di promemoria annuali\n• Il mio volo è martedì prossimo alle 14 → promemoria prima della partenza\n• Ho cambiato l'olio dell'auto → ti ricorderò tra un anno\n\nUsa i pulsanti in basso o scrivi direttamente."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Condividi la tua posizione per rilevare automaticamente il fuso orario.\n\nOppure scegli una regione qui sotto:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Fuso orario rilevato: %s\nÈ corretto?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Fuso orario impostato: %s. I tuoi promemoria useranno questo fuso."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Scegli la tua regione:"),
        Map.entry(Key.TZ_LOCATING, "📍 Posizione ricevuta, rilevamento del fuso orario…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Scegli diversamente"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Condividi la posizione"),
        Map.entry(Key.BTN_LIST, "📋 I miei promemoria"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Cambia fuso orario"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s")
    );

    private static final Map<Key, String> TR = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Hatırlatıcı oluşturuldu!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Önceden hatırlatıcılar da planlandı:"),
        Map.entry(Key.NO_REMINDERS, "Aktif hatırlatıcınız yok."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Aktif hatırlatıcılarınız:"),
        Map.entry(Key.DELETED, "✅ Hatırlatıcı [%d] silindi."),
        Map.entry(Key.NOT_FOUND, "Hatırlatıcı bulunamadı."),
        Map.entry(Key.TZ_UPDATED, "✅ Saat dilimi güncellendi: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Geçersiz saat dilimi: %s. IANA biçimini kullanın (ör. Europe/Istanbul)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Hatırlatıcı oluşturulamadı: geçersiz zamanlama. Lütfen yeniden ifade edin."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Çok sık zamanlama. Minimum aralık: 30 dakika."),
        Map.entry(Key.FIREAT_INVALID, "❌ Hatırlatıcı zamanı gelecekte olmalıdır (maks. 50 yıl)."),
        Map.entry(Key.WRONG, "Bir şeyler yanlış gitti. Lütfen tekrar deneyin."),
        Map.entry(Key.INVALID_ID, "Geçersiz kimlik. Hatırlatıcılarınızı görmek için /list kullanın."),
        Map.entry(Key.UNKNOWN_CMD, "Bilinmeyen komut. Hatırlatıcı eklemek için doğrudan yaz."),
        Map.entry(Key.MAX_REMINDERS, "%d hatırlatıcı limitine ulaştınız. Yeni eklemek için bazılarını silin."),
        Map.entry(Key.OFFTOPIC, "Yalnızca hatırlatıcı kurmanıza yardımcı olabilirim. Ne ve ne zaman hatırlatmamı istediğinizi söyleyin."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Günlük %d hatırlatıcı isteği limitine ulaştınız. Yarın tekrar deneyin."),
        Map.entry(Key.START, "👋 Merhaba, %s! Ben Hatırlatma Asistanınım.\n\nBana sadece ne hatırlatmamı istediğinizi söyleyin:\n• Her gün saat 8'de ilaç almamı hatırlat\n• Annemin doğum günü 5 Mart → yıllık hatırlatma zinciri\n• Uçuşum gelecek Salı 14'te → seyahat öncesi hatırlatmalar\n• Arabanın yağını değiştirdim → bir yıl sonra hatırlatırım\n\nAşağıdaki düğmeleri kullan ya da doğrudan yaz."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Saat diliminizi otomatik algılamak için konumunuzu paylaşın.\n\nYa da aşağıdan bir bölge seçin:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Tespit edilen saat dilimi: %s\nDoğru mu?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Saat dilimi ayarlandı: %s. Hatırlatmalarınız bu saat dilimini kullanacak."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Bölgenizi seçin:"),
        Map.entry(Key.TZ_LOCATING, "📍 Konum alındı, saat dilimi tespit ediliyor…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Farklı seç"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Konumu paylaş"),
        Map.entry(Key.BTN_LIST, "📋 Hatırlatmalarım"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Saat dilimini değiştir"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s")
    );

    private static final Map<Key, String> PL = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Przypomnienie ustawione!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Zaplanowano również przypomnienia wstępne:"),
        Map.entry(Key.NO_REMINDERS, "Nie masz aktywnych przypomnień."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Twoje aktywne przypomnienia:"),
        Map.entry(Key.DELETED, "✅ Przypomnienie [%d] usunięte."),
        Map.entry(Key.NOT_FOUND, "Przypomnienie nie znalezione."),
        Map.entry(Key.TZ_UPDATED, "✅ Strefa czasowa zaktualizowana: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Nieprawidłowa strefa czasowa: %s. Użyj formatu IANA (np. Europe/Warsaw)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Nie można utworzyć przypomnienia: nieprawidłowy harmonogram. Spróbuj przeformułować."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Harmonogram zbyt częsty. Minimalny interwał: 30 minut."),
        Map.entry(Key.FIREAT_INVALID, "❌ Czas przypomnienia musi być w przyszłości (maks. 50 lat)."),
        Map.entry(Key.WRONG, "Coś poszło nie tak. Proszę spróbować ponownie."),
        Map.entry(Key.INVALID_ID, "Nieprawidłowy ID. Użyj /list, aby zobaczyć przypomnienia."),
        Map.entry(Key.UNKNOWN_CMD, "Nieznana komenda. Napisz po prostu, o czym chcesz być przypomniany."),
        Map.entry(Key.MAX_REMINDERS, "Osiągnąłeś maksimum %d przypomnień. Usuń kilka, żeby dodać nowe."),
        Map.entry(Key.OFFTOPIC, "Mogę tylko pomóc w ustawianiu przypomnień. Powiedz mi co i kiedy."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Osiągnąłeś dzienny limit %d żądań przypomnień. Spróbuj ponownie jutro."),
        Map.entry(Key.START, "👋 Cześć, %s! Jestem Twoim Asystentem przypomnień.\n\nNapisz co i kiedy przypomnieć — i gotowe:\n• Przypomnij mi wziąć tabletkę codziennie o 20\n• Urodziny mamy są 5 marca → łańcuch corocznych przypomnień\n• Mój lot jest w przyszły wtorek o 14 → przypomnienia przed wylotem\n• Wymieniłem olej w samochodzie → przypomnę za rok\n\nKorzystaj z przycisków poniżej lub po prostu pisz."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Udostępnij lokalizację, aby automatycznie wykryć strefę czasową.\n\nLub wybierz region poniżej:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Wykryta strefa czasowa: %s\nCzy to prawidłowe?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Ustawiono strefę czasową: %s. Twoje przypomnienia będą jej używać."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Wybierz swój region:"),
        Map.entry(Key.TZ_LOCATING, "📍 Lokalizacja odebrana, wykrywam strefę czasową…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Wybierz inaczej"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Udostępnij lokalizację"),
        Map.entry(Key.BTN_LIST, "📋 Moje przypomnienia"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Zmień strefę czasową"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s")
    );

    private static final Map<Key, String> UK = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Нагадування створено!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Також заплановані попередні нагадування:"),
        Map.entry(Key.NO_REMINDERS, "У вас немає активних нагадувань."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Ваші активні нагадування:"),
        Map.entry(Key.DELETED, "✅ Нагадування [%d] видалено."),
        Map.entry(Key.NOT_FOUND, "Нагадування не знайдено."),
        Map.entry(Key.TZ_UPDATED, "✅ Часовий пояс оновлено: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Невірний часовий пояс: %s. Використовуйте формат IANA (наприклад, Europe/Kyiv)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Не вдалося створити нагадування: невірний розклад. Спробуйте переформулювати."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Розклад занадто частий. Мінімальний інтервал — 30 хвилин."),
        Map.entry(Key.FIREAT_INVALID, "❌ Час нагадування має бути у майбутньому (макс. 50 років)."),
        Map.entry(Key.WRONG, "Щось пішло не так. Будь ласка, спробуйте ще раз."),
        Map.entry(Key.INVALID_ID, "Невірний ID. Використовуйте /list для перегляду нагадувань."),
        Map.entry(Key.UNKNOWN_CMD, "Невідома команда. Просто напиши, про що потрібно нагадати."),
        Map.entry(Key.MAX_REMINDERS, "Ви досягли максимуму %d активних нагадувань. Видали деякі, щоб додати нові."),
        Map.entry(Key.OFFTOPIC, "Я можу лише допомагати налаштовувати нагадування. Скажи мені що і коли нагадати."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Ви досягли денного ліміту %d запитів на нагадування. Спробуй знову завтра."),
        Map.entry(Key.START, "👋 Привіт, %s! Я твій Помічник-нагадувач.\n\nНапиши що і коли нагадати — і готово:\n• Нагадай випити таблетку щодня о 20\n• День народження мами — 5 березня → ланцюжок щорічних нагадувань\n• Мій рейс наступного вівторка о 14 → нагадування перед поїздкою\n• Замінив масло в машині → нагадаю через рік\n\nКористуйся кнопками нижче або просто пиши."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Поділися місцезнаходженням для автоматичного визначення часового поясу.\n\nАбо обери регіон нижче:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Визначено часовий пояс: %s\nЦе правильно?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Часовий пояс встановлено: %s. Твої нагадування використовуватимуть цей пояс."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Обери свій регіон:"),
        Map.entry(Key.TZ_LOCATING, "📍 Місцезнаходження отримано, визначаю часовий пояс…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Обрати інший"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Поділитися місцезнаходженням"),
        Map.entry(Key.BTN_LIST, "📋 Мої нагадування"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Змінити часовий пояс"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s")
    );

    private static final Map<String, Map<Key, String>> MESSAGES = Map.of(
        "en", EN,
        "ru", RU,
        "de", DE,
        "fr", FR,
        "es", ES,
        "pt", PT,
        "it", IT,
        "tr", TR,
        "pl", PL,
        "uk", UK
    );

    private BotMessages() {
    }

    public static String get(Key key, String langCode, Object... args) {
        var template = MESSAGES.getOrDefault(normalize(langCode), EN).getOrDefault(key, EN.get(key));
        return args != null && args.length > 0 ? template.formatted(args) : template;
    }

    /** Returns all translations of a key across every language (for reverse button-text lookup). */
    public static java.util.Set<String> getAllValues(Key key) {
        var result = new java.util.HashSet<String>();
        for (var map : MESSAGES.values()) {
            var v = map.get(key);
            if (v != null) result.add(v);
        }
        return java.util.Set.copyOf(result);
    }

    /**
     * Formats a human-readable countdown string (e.g. "Через 2 хвилини", "in 5 minutes").
     * @param secondsFromNow seconds until the event; must be positive
     */
    public static String formatCountdown(long secondsFromNow, String lang) {
        if (secondsFromNow <= 0) return "";
        if (secondsFromNow < 3600) {
            return formatUnit(Math.max(1, secondsFromNow / 60), 0, lang);
        } else if (secondsFromNow < 86400) {
            return formatUnit(secondsFromNow / 3600, 1, lang);
        } else if (secondsFromNow < 86400L * 30) {
            return formatUnit(secondsFromNow / 86400, 2, lang);
        } else {
            return formatUnit(secondsFromNow / (86400L * 30), 3, lang);
        }
    }

    // unit: 0=minute, 1=hour, 2=day, 3=month
    private static String formatUnit(long n, int unit, String lang) {
        return switch (normalize(lang)) {
            case "uk" -> slavic(n, unit, "Через",
                new String[][]{ {"хвилину","хвилини","хвилин"}, {"годину","години","годин"},
                                {"день","дні","днів"}, {"місяць","місяці","місяців"} });
            case "ru" -> slavic(n, unit, "Через",
                new String[][]{ {"минуту","минуты","минут"}, {"час","часа","часов"},
                                {"день","дня","дней"}, {"месяц","месяца","месяцев"} });
            case "pl" -> slavic(n, unit, "Za",
                new String[][]{ {"minutę","minuty","minut"}, {"godzinę","godziny","godzin"},
                                {"dzień","dni","dni"}, {"miesiąc","miesiące","miesięcy"} });
            case "de" -> western(n, "In", unit,
                new String[][]{ {"Minute","Minuten"}, {"Stunde","Stunden"}, {"Tag","Tagen"}, {"Monat","Monaten"} });
            case "fr" -> western(n, "Dans", unit,
                new String[][]{ {"minute","minutes"}, {"heure","heures"}, {"jour","jours"}, {"mois","mois"} });
            case "es" -> western(n, "En", unit,
                new String[][]{ {"minuto","minutos"}, {"hora","horas"}, {"día","días"}, {"mes","meses"} });
            case "pt" -> western(n, "Em", unit,
                new String[][]{ {"minuto","minutos"}, {"hora","horas"}, {"dia","dias"}, {"mês","meses"} });
            case "it" -> western(n, "Tra", unit,
                new String[][]{ {"minuto","minuti"}, {"ora","ore"}, {"giorno","giorni"}, {"mese","mesi"} });
            case "tr" -> n + " " + new String[]{"dakika","saat","gün","ay"}[unit] + " içinde";
            default   -> western(n, "In", unit,
                new String[][]{ {"minute","minutes"}, {"hour","hours"}, {"day","days"}, {"month","months"} });
        };
    }

    private static String slavic(long n, int unit, String prefix, String[][] forms) {
        long m10 = n % 10, m100 = n % 100;
        int form = (m100 >= 11 && m100 <= 19) ? 2 : (m10 == 1 ? 0 : (m10 >= 2 && m10 <= 4 ? 1 : 2));
        return prefix + " " + n + " " + forms[unit][form];
    }

    private static String western(long n, String prefix, int unit, String[][] forms) {
        return prefix + " " + n + " " + (n == 1 ? forms[unit][0] : forms[unit][1]);
    }

    private static String normalize(String langCode) {
        if (langCode == null || langCode.isBlank()) {
            return "en";
        }
        var normalized = langCode.length() >= 2 ? langCode.substring(0, 2) : langCode;
        return normalized.toLowerCase();
    }
}
