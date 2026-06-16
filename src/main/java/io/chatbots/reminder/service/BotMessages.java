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
        BTN_LIST, BTN_CHANGE_TZ, BTN_CANCEL, BTN_CONFIRM_DELETE,
        REMINDER_FIRED, TZ_NEEDED_FIRST, PROCESSING,
        FWD_ASK, FWD_AT, FWD_1H, FWD_3H, FWD_1D,
        BTN_MANAGE, BTN_DONE
    }

    private static final Map<Key, String> EN = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Reminder set!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Also scheduled lead-up reminders:"),
        Map.entry(Key.NO_REMINDERS, "You have no active reminders."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Your active reminders:"),
        Map.entry(Key.DELETED, "✅ \"%s\" deleted."),
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
        Map.entry(Key.TZ_NEEDED_FIRST, "🕐 First, let's set your timezone so reminders fire at the right time."),
        Map.entry(Key.TZ_DETECTED, "🌍 Detected timezone: %s\nIs this correct?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Timezone set to %s. Your reminders will use this timezone."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Choose your region:"),
        Map.entry(Key.TZ_LOCATING, "📍 Got your location, detecting timezone…"),
        Map.entry(Key.PROCESSING, "Processing your reminder…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Choose differently"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Share Location"),
        Map.entry(Key.BTN_LIST, "📋 My Reminders"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Change Timezone"),
        Map.entry(Key.BTN_CANCEL, "✖️ Cancel"),
        Map.entry(Key.BTN_CONFIRM_DELETE, "✅ Yes, delete"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s"),
        Map.entry(Key.FWD_ASK, "📌 When should I remind you about this?"),
        Map.entry(Key.FWD_AT, "🔔 At event time"),
        Map.entry(Key.FWD_1H, "⏰ 1 hour before"),
        Map.entry(Key.FWD_3H, "⏰ 3 hours before"),
        Map.entry(Key.FWD_1D, "📅 Day before"),
        Map.entry(Key.BTN_MANAGE, "🗑 Manage"),
        Map.entry(Key.BTN_DONE, "✅ Done"));

    private static final Map<Key, String> RU = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Напоминание создано!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Также запланированы предварительные напоминания:"),
        Map.entry(Key.NO_REMINDERS, "У вас нет активных напоминаний."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Ваши активные напоминания:"),
        Map.entry(Key.DELETED, "✅ \"%s\" удалено."),
        Map.entry(Key.NOT_FOUND, "Напоминание не найдено."),
        Map.entry(Key.TZ_UPDATED, "✅ Часовой пояс обновлён: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Неверный часовой пояс: %s. Используйте формат IANA (например, Europe/Moscow)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Не удалось создать напоминание: неверное расписание. Попробуйте переформулировать."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Расписание слишком частое. Минимальный интервал — 30 минут."),
        Map.entry(Key.FIREAT_INVALID, "❌ Время напоминания должно быть в будущем и не более чем через 50 лет."),
        Map.entry(Key.WRONG, "Что-то пошло не так. Попробуйте снова."),
        Map.entry(Key.INVALID_ID, "Неверный ID. Используйте /list для просмотра напоминаний."),
        Map.entry(Key.UNKNOWN_CMD, "Неизвестная команда. Просто напишите, что нужно напомнить."),
        Map.entry(Key.MAX_REMINDERS, "Достигнут максимум %d активных напоминаний. Удалите некоторые, чтобы добавить новые."),
        Map.entry(Key.OFFTOPIC, "Я могу только помогать настраивать напоминания. Напишите, о чём и когда напомнить."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Вы достигли дневного лимита %d запросов на напоминания. Попробуйте снова завтра."),
        Map.entry(Key.START, "👋 Здравствуйте, %s! Я ваш Помощник-напоминалка.\n\nНапишите что и когда напомнить — и готово:\n• Напомни выпить таблетку каждый день в 20\n• День рождения мамы — 5 марта → цепочка ежегодных напоминаний\n• Мой рейс в следующий вторник в 14 → напомнит о поездке\n• Масло заменено в машине → напомню через год\n\nИспользуйте кнопки ниже или просто пишите."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Поделитесь местоположением для автоматического определения часового пояса.\n\nИли выберите регион ниже:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Определён часовой пояс: %s\nВерно?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Часовой пояс установлен: %s. Напоминания будут использовать этот пояс."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Выберите регион:"),
        Map.entry(Key.TZ_LOCATING, "📍 Местоположение получено, определяю часовой пояс…"),
        Map.entry(Key.PROCESSING, "Обрабатываю напоминание…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Выбрать другой"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Поделиться местоположением"),
        Map.entry(Key.BTN_LIST, "📋 Мои напоминания"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Изменить часовой пояс"),
        Map.entry(Key.BTN_CANCEL, "✖️ Отмена"),
        Map.entry(Key.BTN_CONFIRM_DELETE, "✅ Да, удалить"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s"),
        Map.entry(Key.FWD_ASK, "📌 Когда напомнить об этом?"),
        Map.entry(Key.FWD_AT, "🔔 В момент события"),
        Map.entry(Key.FWD_1H, "⏰ За 1 час"),
        Map.entry(Key.FWD_3H, "⏰ За 3 часа"),
        Map.entry(Key.FWD_1D, "📅 За день"),
        Map.entry(Key.BTN_MANAGE, "🗑 Управлять"),
        Map.entry(Key.BTN_DONE, "✅ Готово")
    );

    private static final Map<Key, String> DE = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Erinnerung gesetzt!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Vorab-Erinnerungen auch geplant:"),
        Map.entry(Key.NO_REMINDERS, "Sie haben keine aktiven Erinnerungen."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Ihre aktiven Erinnerungen:"),
        Map.entry(Key.DELETED, "✅ \"%s\" gelöscht."),
        Map.entry(Key.NOT_FOUND, "Erinnerung nicht gefunden."),
        Map.entry(Key.TZ_UPDATED, "✅ Zeitzone aktualisiert: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Ungültige Zeitzone: %s. Bitte IANA-Format verwenden (z.B. Europe/Berlin)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Erinnerung konnte nicht erstellt werden: ungültiger Zeitplan. Bitte neu formulieren."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Zeitplan zu häufig. Mindestintervall: 30 Minuten."),
        Map.entry(Key.FIREAT_INVALID, "❌ Der Erinnerungszeitpunkt muss in der Zukunft liegen (max. 50 Jahre)."),
        Map.entry(Key.WRONG, "Etwas ist schiefgelaufen. Bitte versuchen Sie es erneut."),
        Map.entry(Key.INVALID_ID, "Ungültige ID. Verwenden Sie /list zum Anzeigen."),
        Map.entry(Key.UNKNOWN_CMD, "Unbekannter Befehl. Schreiben Sie einfach, woran Sie erinnert werden möchten."),
        Map.entry(Key.MAX_REMINDERS, "Maximum von %d Erinnerungen erreicht. Löschen Sie einige, um neue hinzuzufügen."),
        Map.entry(Key.OFFTOPIC, "Ich kann nur beim Einrichten von Erinnerungen helfen. Sagen Sie mir, woran und wann."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Sie haben das Tageslimit von %d Erinnerungsanfragen erreicht. Versuchen Sie es morgen erneut."),
        Map.entry(Key.START, "👋 Hallo, %s! Ich bin Ihr Erinnerungsassistent.\n\nSagen Sie mir einfach, woran Sie erinnert werden möchten:\n• Erinnere mich täglich um 8 Uhr, meine Tablette zu nehmen\n• Mamas Geburtstag ist am 5. März → jährliche Erinnerungskette\n• Mein Flug ist nächsten Dienstag um 14 → Reiseerinnerungen\n• Öl im Auto gewechselt → Erinnerung in einem Jahr\n\nNutzen Sie die Schaltflächen unten oder schreiben Sie einfach."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Teilen Sie Ihren Standort, um die Zeitzone automatisch zu erkennen.\n\nOder wählen Sie unten eine Region:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Erkannte Zeitzone: %s\nIst das korrekt?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Zeitzone festgelegt: %s. Ihre Erinnerungen verwenden diese Zeitzone."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Wählen Sie Ihre Region:"),
        Map.entry(Key.TZ_LOCATING, "📍 Standort erhalten, erkenne Zeitzone…"),
        Map.entry(Key.PROCESSING, "Verarbeite Ihre Erinnerung…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Andere wählen"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Standort teilen"),
        Map.entry(Key.BTN_LIST, "📋 Meine Erinnerungen"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Zeitzone ändern"),
        Map.entry(Key.BTN_CANCEL, "✖️ Abbrechen"),
        Map.entry(Key.BTN_CONFIRM_DELETE, "✅ Ja, löschen"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s"),
        Map.entry(Key.FWD_ASK, "📌 Wann soll ich dich daran erinnern?"),
        Map.entry(Key.FWD_AT, "🔔 Zur Eventzeit"),
        Map.entry(Key.FWD_1H, "⏰ 1 Stunde vorher"),
        Map.entry(Key.FWD_3H, "⏰ 3 Stunden vorher"),
        Map.entry(Key.FWD_1D, "📅 Tag davor"),
        Map.entry(Key.BTN_MANAGE, "🗑 Verwalten"),
        Map.entry(Key.BTN_DONE, "✅ Fertig")
    );

    private static final Map<Key, String> FR = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Rappel créé !"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Rappels préparatoires aussi planifiés :"),
        Map.entry(Key.NO_REMINDERS, "Vous n'avez pas de rappels actifs."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Vos rappels actifs :"),
        Map.entry(Key.DELETED, "✅ \"%s\" supprimé."),
        Map.entry(Key.NOT_FOUND, "Rappel introuvable."),
        Map.entry(Key.TZ_UPDATED, "✅ Fuseau horaire mis à jour : %s."),
        Map.entry(Key.INVALID_TZ, "❌ Fuseau horaire invalide : %s. Utilisez le format IANA (ex. Europe/Paris)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Impossible de créer le rappel : calendrier invalide. Essayez de reformuler."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Fréquence trop élevée. Intervalle minimum : 30 minutes."),
        Map.entry(Key.FIREAT_INVALID, "❌ La date du rappel doit être dans le futur (max 50 ans)."),
        Map.entry(Key.WRONG, "Une erreur s'est produite. Veuillez réessayer."),
        Map.entry(Key.INVALID_ID, "ID de rappel invalide. Utilisez /list pour voir vos rappels."),
        Map.entry(Key.UNKNOWN_CMD, "Commande inconnue. Écrivez simplement ce dont vous voulez vous souvenir."),
        Map.entry(Key.MAX_REMINDERS, "Maximum de %d rappels atteint. Supprimez-en quelques-uns pour en ajouter."),
        Map.entry(Key.OFFTOPIC, "Je peux seulement vous aider à créer des rappels. Dites-moi quoi et quand."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Vous avez atteint la limite quotidienne de %d requêtes de rappels. Réessayez demain."),
        Map.entry(Key.START, "👋 Bonjour, %s ! Je suis votre Assistant de rappels.\n\nDites-moi simplement ce dont vous voulez vous souvenir :\n• Rappelle-moi de prendre mon médicament tous les jours à 8h\n• L'anniversaire de maman est le 5 mars → chaîne de rappels annuels\n• Mon vol est mardi prochain à 14h → rappels avant le départ\n• Huile de voiture changée → je te rappellerai dans un an\n\nUtilisez les boutons ci-dessous ou écrivez directement."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Partagez votre position pour détecter automatiquement votre fuseau horaire.\n\nOu choisissez une région ci-dessous :"),
        Map.entry(Key.TZ_DETECTED, "🌍 Fuseau horaire détecté : %s\nEst-ce correct ?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Fuseau horaire défini : %s. Vos rappels utiliseront ce fuseau."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Choisissez votre région :"),
        Map.entry(Key.TZ_LOCATING, "📍 Position reçue, détection du fuseau horaire…"),
        Map.entry(Key.PROCESSING, "Traitement de votre rappel…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Choisir autrement"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Partager ma position"),
        Map.entry(Key.BTN_LIST, "📋 Mes rappels"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Changer de fuseau"),
        Map.entry(Key.BTN_CANCEL, "✖️ Annuler"),
        Map.entry(Key.BTN_CONFIRM_DELETE, "✅ Oui, supprimer"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s"),
        Map.entry(Key.FWD_ASK, "📌 Quand voulez-vous que je vous le rappelle ?"),
        Map.entry(Key.FWD_AT, "🔔 À l'heure de l'événement"),
        Map.entry(Key.FWD_1H, "⏰ 1 heure avant"),
        Map.entry(Key.FWD_3H, "⏰ 3 heures avant"),
        Map.entry(Key.FWD_1D, "📅 La veille"),
        Map.entry(Key.BTN_MANAGE, "🗑 Gérer"),
        Map.entry(Key.BTN_DONE, "✅ Terminé")
    );

    private static final Map<Key, String> ES = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ ¡Recordatorio creado!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Recordatorios previos también programados:"),
        Map.entry(Key.NO_REMINDERS, "No tiene recordatorios activos."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Sus recordatorios activos:"),
        Map.entry(Key.DELETED, "✅ \"%s\" eliminado."),
        Map.entry(Key.NOT_FOUND, "Recordatorio no encontrado."),
        Map.entry(Key.TZ_UPDATED, "✅ Zona horaria actualizada: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Zona horaria inválida: %s. Use el formato IANA (p. ej. Europe/Madrid)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ No se pudo crear el recordatorio: programación inválida. Intente reformular."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Frecuencia demasiado alta. Intervalo mínimo: 30 minutos."),
        Map.entry(Key.FIREAT_INVALID, "❌ La hora del recordatorio debe ser futura (máx. 50 años)."),
        Map.entry(Key.WRONG, "Algo salió mal. Por favor, inténtelo de nuevo."),
        Map.entry(Key.INVALID_ID, "ID inválido. Use /list para ver sus recordatorios."),
        Map.entry(Key.UNKNOWN_CMD, "Comando desconocido. Escriba directamente lo que quiere recordar."),
        Map.entry(Key.MAX_REMINDERS, "Alcanzó el máximo de %d recordatorios. Elimine algunos para agregar nuevos."),
        Map.entry(Key.OFFTOPIC, "Solo puedo ayudarle a configurar recordatorios. Dígame qué y cuándo recordarle."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Ha alcanzado el límite diario de %d solicitudes de recordatorios. Inténtelo mañana."),
        Map.entry(Key.START, "👋 ¡Hola, %s! Soy su Asistente de recordatorios.\n\nDígame en lenguaje natural qué necesita recordar:\n• Recuérdame tomar mi pastilla cada día a las 8\n• El cumpleaños de mamá es el 5 de marzo → cadena de recordatorios anuales\n• Mi vuelo es el próximo martes a las 14 → recordatorios previos al viaje\n• Cambié el aceite del coche → te lo recuerdo en un año\n\nUse los botones de abajo o simplemente escríbame."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Comparta su ubicación para detectar automáticamente su zona horaria.\n\nO elija una región abajo:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Zona horaria detectada: %s\n¿Es correcto?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Zona horaria establecida: %s. Sus recordatorios usarán esta zona."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Elija su región:"),
        Map.entry(Key.TZ_LOCATING, "📍 Ubicación recibida, detectando zona horaria…"),
        Map.entry(Key.PROCESSING, "Procesando su recordatorio…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Elegir otra zona"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Compartir ubicación"),
        Map.entry(Key.BTN_LIST, "📋 Mis recordatorios"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Cambiar zona horaria"),
        Map.entry(Key.BTN_CANCEL, "✖️ Cancelar"),
        Map.entry(Key.BTN_CONFIRM_DELETE, "✅ Sí, eliminar"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s"),
        Map.entry(Key.FWD_ASK, "📌 ¿Cuándo se lo recuerdo?"),
        Map.entry(Key.FWD_AT, "🔔 A la hora del evento"),
        Map.entry(Key.FWD_1H, "⏰ 1 hora antes"),
        Map.entry(Key.FWD_3H, "⏰ 3 horas antes"),
        Map.entry(Key.FWD_1D, "📅 El día antes"),
        Map.entry(Key.BTN_MANAGE, "🗑 Gestionar"),
        Map.entry(Key.BTN_DONE, "✅ Listo")
    );

    private static final Map<Key, String> PT = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Lembrete criado!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Lembretes prévios também agendados:"),
        Map.entry(Key.NO_REMINDERS, "Você não tem lembretes ativos."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Seus lembretes ativos:"),
        Map.entry(Key.DELETED, "✅ \"%s\" excluído."),
        Map.entry(Key.NOT_FOUND, "Lembrete não encontrado."),
        Map.entry(Key.TZ_UPDATED, "✅ Fuso horário atualizado: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Fuso horário inválido: %s. Use o formato IANA (ex. Europe/Lisbon)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Não foi possível criar o lembrete: programação inválida. Tente reformular."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Frequência muito alta. Intervalo mínimo: 30 minutos."),
        Map.entry(Key.FIREAT_INVALID, "❌ O horário do lembrete deve ser no futuro (máx. 50 anos)."),
        Map.entry(Key.WRONG, "Algo deu errado. Por favor, tente novamente."),
        Map.entry(Key.INVALID_ID, "ID inválido. Use /list para ver seus lembretes."),
        Map.entry(Key.UNKNOWN_CMD, "Comando desconhecido. Escreva diretamente o que quer lembrar."),
        Map.entry(Key.MAX_REMINDERS, "Você atingiu o máximo de %d lembretes. Apague alguns para adicionar novos."),
        Map.entry(Key.OFFTOPIC, "Só posso ajudar a configurar lembretes. Diga-me o quê e quando."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Você atingiu o limite diário de %d pedidos de lembretes. Tente novamente amanhã."),
        Map.entry(Key.START, "👋 Olá, %s! Sou o seu Assistente de lembretes.\n\nDiga-me em linguagem natural o que precisa lembrar:\n• Lembra-me de tomar o meu comprimido todos os dias às 8h\n• O aniversário da mãe é dia 5 de março → cadeia de lembretes anuais\n• O meu voo é na próxima terça às 14h → lembretes antes da viagem\n• Mudei o óleo do carro → lembro-te daqui a um ano\n\nUse os botões abaixo ou escreva diretamente."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Partilhe a sua localização para detetar automaticamente o fuso horário.\n\nOu escolha uma região abaixo:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Fuso horário detetado: %s\nEstá correto?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Fuso horário definido: %s. Os seus lembretes usarão este fuso."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Escolha a sua região:"),
        Map.entry(Key.TZ_LOCATING, "📍 Localização recebida, a detetar fuso horário…"),
        Map.entry(Key.PROCESSING, "A processar o seu lembrete…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Escolher outra zona"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Partilhar localização"),
        Map.entry(Key.BTN_LIST, "📋 Os meus lembretes"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Alterar fuso horário"),
        Map.entry(Key.BTN_CANCEL, "✖️ Cancelar"),
        Map.entry(Key.BTN_CONFIRM_DELETE, "✅ Sim, excluir"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s"),
        Map.entry(Key.FWD_ASK, "📌 Quando devo lembrá-lo disto?"),
        Map.entry(Key.FWD_AT, "🔔 Na hora do evento"),
        Map.entry(Key.FWD_1H, "⏰ 1 hora antes"),
        Map.entry(Key.FWD_3H, "⏰ 3 horas antes"),
        Map.entry(Key.FWD_1D, "📅 No dia anterior"),
        Map.entry(Key.BTN_MANAGE, "🗑 Gerir"),
        Map.entry(Key.BTN_DONE, "✅ Concluído")
    );

    private static final Map<Key, String> IT = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Promemoria impostato!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Promemoria anticipati anche programmati:"),
        Map.entry(Key.NO_REMINDERS, "Non ha promemoria attivi."),
        Map.entry(Key.REMINDERS_HEADER, "📋 I suoi promemoria attivi:"),
        Map.entry(Key.DELETED, "✅ \"%s\" eliminato."),
        Map.entry(Key.NOT_FOUND, "Promemoria non trovato."),
        Map.entry(Key.TZ_UPDATED, "✅ Fuso orario aggiornato: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Fuso orario non valido: %s. Usi il formato IANA (es. Europe/Rome)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Impossibile creare il promemoria: pianificazione non valida. Provi a riformulare."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Frequenza troppo alta. Intervallo minimo: 30 minuti."),
        Map.entry(Key.FIREAT_INVALID, "❌ L'orario del promemoria deve essere nel futuro (max 50 anni)."),
        Map.entry(Key.WRONG, "Qualcosa è andato storto. Per favore riprovi."),
        Map.entry(Key.INVALID_ID, "ID non valido. Usi /list per vedere i suoi promemoria."),
        Map.entry(Key.UNKNOWN_CMD, "Comando sconosciuto. Scriva direttamente cosa vuole ricordare."),
        Map.entry(Key.MAX_REMINDERS, "Ha raggiunto il massimo di %d promemoria. Ne elimini alcuni per aggiungerne di nuovi."),
        Map.entry(Key.OFFTOPIC, "Posso solo aiutarla a impostare promemoria. Mi dica cosa e quando."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Ha raggiunto il limite giornaliero di %d richieste di promemoria. Riprovi domani."),
        Map.entry(Key.START, "👋 Salve, %s! Sono il suo Assistente promemoria.\n\nMi dica in modo naturale cosa vuole ricordare:\n• Ricordami di prendere la pillola ogni giorno alle 8\n• Il compleanno della mamma è il 5 marzo → catena di promemoria annuali\n• Il mio volo è martedì prossimo alle 14 → promemoria prima della partenza\n• Ho cambiato l'olio dell'auto → ti ricorderò tra un anno\n\nUsi i pulsanti in basso o scriva direttamente."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Condivida la sua posizione per rilevare automaticamente il fuso orario.\n\nOppure scelga una regione qui sotto:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Fuso orario rilevato: %s\nÈ corretto?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Fuso orario impostato: %s. I suoi promemoria useranno questo fuso."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Scelga la sua regione:"),
        Map.entry(Key.TZ_LOCATING, "📍 Posizione ricevuta, rilevamento del fuso orario…"),
        Map.entry(Key.PROCESSING, "Elaboro il suo promemoria…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Scegli diversamente"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Condividi la posizione"),
        Map.entry(Key.BTN_LIST, "📋 I miei promemoria"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Cambia fuso orario"),
        Map.entry(Key.BTN_CANCEL, "✖️ Annulla"),
        Map.entry(Key.BTN_CONFIRM_DELETE, "✅ Sì, elimina"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s"),
        Map.entry(Key.FWD_ASK, "📌 Quando vuole che glielo ricordi?"),
        Map.entry(Key.FWD_AT, "🔔 All'ora dell'evento"),
        Map.entry(Key.FWD_1H, "⏰ 1 ora prima"),
        Map.entry(Key.FWD_3H, "⏰ 3 ore prima"),
        Map.entry(Key.FWD_1D, "📅 Il giorno prima"),
        Map.entry(Key.BTN_MANAGE, "🗑 Gestisci"),
        Map.entry(Key.BTN_DONE, "✅ Fatto")
    );

    private static final Map<Key, String> TR = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Hatırlatıcı oluşturuldu!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Önceden hatırlatıcılar da planlandı:"),
        Map.entry(Key.NO_REMINDERS, "Aktif hatırlatıcınız yok."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Aktif hatırlatıcılarınız:"),
        Map.entry(Key.DELETED, "✅ \"%s\" silindi."),
        Map.entry(Key.NOT_FOUND, "Hatırlatıcı bulunamadı."),
        Map.entry(Key.TZ_UPDATED, "✅ Saat dilimi güncellendi: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Geçersiz saat dilimi: %s. IANA biçimini kullanın (ör. Europe/Istanbul)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Hatırlatıcı oluşturulamadı: geçersiz zamanlama. Lütfen yeniden ifade edin."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Çok sık zamanlama. Minimum aralık: 30 dakika."),
        Map.entry(Key.FIREAT_INVALID, "❌ Hatırlatıcı zamanı gelecekte olmalıdır (maks. 50 yıl)."),
        Map.entry(Key.WRONG, "Bir şeyler yanlış gitti. Lütfen tekrar deneyin."),
        Map.entry(Key.INVALID_ID, "Geçersiz kimlik. Hatırlatıcılarınızı görmek için /list kullanın."),
        Map.entry(Key.UNKNOWN_CMD, "Bilinmeyen komut. Hatırlatıcı eklemek için doğrudan yazın."),
        Map.entry(Key.MAX_REMINDERS, "%d hatırlatıcı limitine ulaştınız. Yeni eklemek için bazılarını silin."),
        Map.entry(Key.OFFTOPIC, "Yalnızca hatırlatıcı kurmanıza yardımcı olabilirim. Ne ve ne zaman hatırlatmamı istediğinizi söyleyin."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Günlük %d hatırlatıcı isteği limitine ulaştınız. Yarın tekrar deneyin."),
        Map.entry(Key.START, "👋 Merhaba, %s! Ben Hatırlatma Asistanınım.\n\nBana sadece ne hatırlatmamı istediğinizi söyleyin:\n• Her gün saat 8'de ilaç almamı hatırlat\n• Annemin doğum günü 5 Mart → yıllık hatırlatma zinciri\n• Uçuşum gelecek Salı 14'te → seyahat öncesi hatırlatmalar\n• Arabanın yağını değiştirdim → bir yıl sonra hatırlatırım\n\nAşağıdaki düğmeleri kullanın ya da doğrudan yazın."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Saat diliminizi otomatik algılamak için konumunuzu paylaşın.\n\nYa da aşağıdan bir bölge seçin:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Tespit edilen saat dilimi: %s\nDoğru mu?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Saat dilimi ayarlandı: %s. Hatırlatmalarınız bu saat dilimini kullanacak."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Bölgenizi seçin:"),
        Map.entry(Key.TZ_LOCATING, "📍 Konum alındı, saat dilimi tespit ediliyor…"),
        Map.entry(Key.PROCESSING, "Hatırlatmanız işleniyor…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Farklı seç"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Konumu paylaş"),
        Map.entry(Key.BTN_LIST, "📋 Hatırlatmalarım"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Saat dilimini değiştir"),
        Map.entry(Key.BTN_CANCEL, "✖️ İptal"),
        Map.entry(Key.BTN_CONFIRM_DELETE, "✅ Evet, sil"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s"),
        Map.entry(Key.FWD_ASK, "📌 Bunu ne zaman hatırlatayım?"),
        Map.entry(Key.FWD_AT, "🔔 Etkinlik saatinde"),
        Map.entry(Key.FWD_1H, "⏰ 1 saat önce"),
        Map.entry(Key.FWD_3H, "⏰ 3 saat önce"),
        Map.entry(Key.FWD_1D, "📅 Bir gün önce"),
        Map.entry(Key.BTN_MANAGE, "🗑 Yönet"),
        Map.entry(Key.BTN_DONE, "✅ Bitti")
    );

    private static final Map<Key, String> PL = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Przypomnienie ustawione!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Zaplanowano również przypomnienia wstępne:"),
        Map.entry(Key.NO_REMINDERS, "Brak aktywnych przypomnień."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Aktywne przypomnienia:"),
        Map.entry(Key.DELETED, "✅ \"%s\" usunięte."),
        Map.entry(Key.NOT_FOUND, "Przypomnienie nie znalezione."),
        Map.entry(Key.TZ_UPDATED, "✅ Strefa czasowa zaktualizowana: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Nieprawidłowa strefa czasowa: %s. Proszę użyć formatu IANA (np. Europe/Warsaw)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Nie można utworzyć przypomnienia: nieprawidłowy harmonogram. Proszę przeformułować."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Harmonogram zbyt częsty. Minimalny interwał: 30 minut."),
        Map.entry(Key.FIREAT_INVALID, "❌ Czas przypomnienia musi być w przyszłości (maks. 50 lat)."),
        Map.entry(Key.WRONG, "Coś poszło nie tak. Proszę spróbować ponownie."),
        Map.entry(Key.INVALID_ID, "Nieprawidłowy ID. Proszę użyć /list, aby zobaczyć przypomnienia."),
        Map.entry(Key.UNKNOWN_CMD, "Nieznana komenda. Proszę napisać, o czym przypomnieć."),
        Map.entry(Key.MAX_REMINDERS, "Osiągnięto maksimum %d przypomnień. Proszę usunąć kilka, aby dodać nowe."),
        Map.entry(Key.OFFTOPIC, "Mogę tylko pomóc w ustawianiu przypomnień. Proszę powiedzieć, co i kiedy."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Osiągnięto dzienny limit %d żądań przypomnień. Proszę spróbować ponownie jutro."),
        Map.entry(Key.START, "👋 Dzień dobry, %s! Jestem Asystentem przypomnień.\n\nProszę napisać, co i kiedy przypomnieć — i gotowe:\n• Przypomnij mi wziąć tabletkę codziennie o 20\n• Urodziny mamy są 5 marca → łańcuch corocznych przypomnień\n• Mój lot jest w przyszły wtorek o 14 → przypomnienia przed wylotem\n• Wymieniłem olej w samochodzie → przypomnę za rok\n\nMożna korzystać z przycisków poniżej lub po prostu pisać."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Proszę udostępnić lokalizację, aby automatycznie wykryć strefę czasową.\n\nLub wybrać region poniżej:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Wykryta strefa czasowa: %s\nCzy to prawidłowe?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Ustawiono strefę czasową: %s. Przypomnienia będą jej używać."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Proszę wybrać region:"),
        Map.entry(Key.TZ_LOCATING, "📍 Lokalizacja odebrana, wykrywam strefę czasową…"),
        Map.entry(Key.PROCESSING, "Przetwarzam przypomnienie…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Wybierz inaczej"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Udostępnij lokalizację"),
        Map.entry(Key.BTN_LIST, "📋 Moje przypomnienia"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Zmień strefę czasową"),
        Map.entry(Key.BTN_CANCEL, "✖️ Anuluj"),
        Map.entry(Key.BTN_CONFIRM_DELETE, "✅ Tak, usuń"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s"),
        Map.entry(Key.FWD_ASK, "📌 Kiedy mam o tym przypomnieć?"),
        Map.entry(Key.FWD_AT, "🔔 W czasie wydarzenia"),
        Map.entry(Key.FWD_1H, "⏰ 1 godzinę wcześniej"),
        Map.entry(Key.FWD_3H, "⏰ 3 godziny wcześniej"),
        Map.entry(Key.FWD_1D, "📅 Dzień wcześniej"),
        Map.entry(Key.BTN_MANAGE, "🗑 Zarządzaj"),
        Map.entry(Key.BTN_DONE, "✅ Gotowe")
    );

    private static final Map<Key, String> UK = Map.ofEntries(
        Map.entry(Key.REMINDER_SET, "✅ Нагадування створено!"),
        Map.entry(Key.LEAD_UP_HEADER, "📬 Також заплановані попередні нагадування:"),
        Map.entry(Key.NO_REMINDERS, "У вас немає активних нагадувань."),
        Map.entry(Key.REMINDERS_HEADER, "📋 Ваші активні нагадування:"),
        Map.entry(Key.DELETED, "✅ \"%s\" видалено."),
        Map.entry(Key.NOT_FOUND, "Нагадування не знайдено."),
        Map.entry(Key.TZ_UPDATED, "✅ Часовий пояс оновлено: %s."),
        Map.entry(Key.INVALID_TZ, "❌ Невірний часовий пояс: %s. Використовуйте формат IANA (наприклад, Europe/Kyiv)."),
        Map.entry(Key.INVALID_SCHEDULE, "❌ Не вдалося створити нагадування: невірний розклад. Спробуйте переформулювати."),
        Map.entry(Key.CRON_TOO_FREQUENT, "❌ Розклад занадто частий. Мінімальний інтервал — 30 хвилин."),
        Map.entry(Key.FIREAT_INVALID, "❌ Час нагадування має бути у майбутньому (макс. 50 років)."),
        Map.entry(Key.WRONG, "Щось пішло не так. Будь ласка, спробуйте ще раз."),
        Map.entry(Key.INVALID_ID, "Невірний ID. Використовуйте /list для перегляду нагадувань."),
        Map.entry(Key.UNKNOWN_CMD, "Невідома команда. Просто напишіть, про що потрібно нагадати."),
        Map.entry(Key.MAX_REMINDERS, "Ви досягли максимуму %d активних нагадувань. Видаліть деякі, щоб додати нові."),
        Map.entry(Key.OFFTOPIC, "Я можу лише допомагати налаштовувати нагадування. Скажіть мені що і коли нагадати."),
        Map.entry(Key.RATE_LIMITED, "⚠️ Ви досягли денного ліміту %d запитів на нагадування. Спробуйте знову завтра."),
        Map.entry(Key.START, "👋 Вітаю, %s! Я ваш Помічник-нагадувач.\n\nНапишіть що і коли нагадати — і готово:\n• Нагадай випити таблетку щодня о 20\n• День народження мами — 5 березня → ланцюжок щорічних нагадувань\n• Мій рейс наступного вівторка о 14 → нагадування перед поїздкою\n• Замінив масло в машині → нагадаю через рік\n\nКористуйтеся кнопками нижче або просто пишіть."),
        Map.entry(Key.ASK_TIMEZONE, "📍 Поділіться місцезнаходженням для автоматичного визначення часового поясу.\n\nАбо оберіть регіон нижче:"),
        Map.entry(Key.TZ_DETECTED, "🌍 Визначено часовий пояс: %s\nЦе правильно?"),
        Map.entry(Key.TZ_CONFIRMED, "✅ Часовий пояс встановлено: %s. Ваші нагадування використовуватимуть цей пояс."),
        Map.entry(Key.TZ_CHOOSE_REGION, "🌍 Оберіть свій регіон:"),
        Map.entry(Key.TZ_LOCATING, "📍 Місцезнаходження отримано, визначаю часовий пояс…"),
        Map.entry(Key.PROCESSING, "Обробляю нагадування…"),
        Map.entry(Key.TZ_CHOOSE_DIFFERENTLY, "🌍 Обрати інший"),
        Map.entry(Key.TZ_SHARE_LOCATION, "📍 Поділитися місцезнаходженням"),
        Map.entry(Key.BTN_LIST, "📋 Мої нагадування"),
        Map.entry(Key.BTN_CHANGE_TZ, "🕐 Змінити часовий пояс"),
        Map.entry(Key.BTN_CANCEL, "✖️ Скасувати"),
        Map.entry(Key.BTN_CONFIRM_DELETE, "✅ Так, видалити"),
        Map.entry(Key.REMINDER_FIRED, "⏰ %s"),
        Map.entry(Key.TZ_NEEDED_FIRST, "🕐 Спершу налаштуймо часовий пояс, щоб нагадування спрацьовували вчасно."),
        Map.entry(Key.FWD_ASK, "📌 Коли нагадати про це?"),
        Map.entry(Key.FWD_AT, "🔔 У момент події"),
        Map.entry(Key.FWD_1H, "⏰ За 1 годину"),
        Map.entry(Key.FWD_3H, "⏰ За 3 години"),
        Map.entry(Key.FWD_1D, "📅 За день"),
        Map.entry(Key.BTN_MANAGE, "🗑 Керувати"),
        Map.entry(Key.BTN_DONE, "✅ Готово")
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

    public static String htmlEscape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static java.util.Set<String> getAllValues(Key key) {
        var result = new java.util.HashSet<String>();
        for (var map : MESSAGES.values()) {
            var v = map.get(key);
            if (v != null) result.add(v);
        }
        return java.util.Set.copyOf(result);
    }

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
        long m10 = n % 10;
        long m100 = n % 100;
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
