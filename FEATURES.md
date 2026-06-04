# Reminder Bot — Feature Backlog

## Planned Improvements

1. **Snooze**
   - Command: `/snooze <id> 1h` (or `30m`, `2h`, `tomorrow`, etc.)
   - Reschedules a fired or active reminder to a new time

2. **Edit via natural language**
   - Example: "change reminder 3 to 7pm" or "move reminder 5 to Saturday"
   - Requires AI parsing of edit intent + reminder lookup by ID

3. **Confirm before save**
   - Bot shows parsed reminder details and asks user to confirm before scheduling
   - Requires multi-turn state (see #12)

4. **/done command**
   - `/done <id>` — marks reminder as completed (separate from delete)
   - Tracks completion history and timestamps for stats

5. **Quiet hours**
   - Per-user configurable no-notification window (e.g. 22:00–08:00)
   - Reminders that fire during quiet hours are delayed to window end

6. **Duplicate detection**
   - Warn user if a very similar reminder already exists before saving
   - Fuzzy match on reminderText + schedule

7. **Reply-to reminder**
   - Reply to any Telegram message with "remind me about this in 1h"
   - Bot extracts context from the original message

8. **Pause / vacation mode**
   - "Pause all reminders for 2 weeks" — suspends all firing during a period
   - `/pause` and `/resume` commands

9. **Custom intervals override**
   - User can override default maintenance intervals inline
   - Example: "oil changed, remind in 6 months" overrides the default 1-year interval

10. **Reminder categories / tags**
    - AI auto-tags reminders: health, work, personal, home, finance
    - `/list work` — filter active reminders by category

11. **Completion tracking**
    - History of all fired reminders with timestamps
    - `/history` command to view past reminders
    - Streak stats for recurring reminders (e.g. "fed leaven 8 weeks in a row")

12. **Multi-turn conversation**
    - Enables confirmation flow (#3) and edit flow (#2)
    - Requires Spring AI `ChatMemory` + per-chatId state in Redis or DB
    - Previous turns injected into prompt on follow-up messages
