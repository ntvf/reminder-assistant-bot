# Reminder Assistant Bot

![Build & Release](https://github.com/ntvf/reminder-assistant-bot/actions/workflows/release.yml/badge.svg)

A Telegram bot that understands natural language reminders and schedules them using AI. Tell it "remind me every Friday to feed the leaven" or "remind me tomorrow to call the dentist" — it parses the intent, extracts timing, and fires the reminder at the right time.

## Features

- Natural language reminder parsing via OpenAI
- One-off and recurring reminders (cron-based)
- Per-user timezone support
- Chained reminders (e.g. "birthday in 1 week" + "birthday tomorrow" auto-created)
- `/list` with inline delete buttons
- Daily reminder limit and per-chat max cap

## Tech stack

- Java 25 + Spring Boot 4
- PostgreSQL + Liquibase migrations
- Quartz scheduler
- Spring AI (OpenAI)
- Telegram Bots API (long polling)

## Coverage & quality gate

Coverage report and per-class breakdown are published to each [workflow run summary](https://github.com/ntvf/reminder-assistant-bot/actions/workflows/release.yml).
Quality gate: **50% line coverage** minimum — build fails if not met.

## Deployment

Each push to `main` builds a release JAR and publishes it as a [GitHub Release](https://github.com/ntvf/reminder-assistant-bot/releases) with commit notes attached.

The production server auto-detects new releases and redeploys via `immortal` (process supervisor with log rotation).

### Required environment variables

| Variable | Description |
|---|---|
| `TELEGRAM_BOT_TOKEN` | Bot token from @BotFather |
| `TELEGRAM_BOT_USERNAME` | Bot username (without @) |
| `OPENAI_API_KEY` | OpenAI API key |
| `DB_HOST` | PostgreSQL host (default: `localhost`) |
| `DB_PORT` | PostgreSQL port (default: `5432`) |
| `DB_NAME` | Database name (default: `reminder`) |
| `DB_USER` | Database user (default: `reminder`) |
| `DB_PASSWORD` | Database password |
