# 🎯 AI Betting Bot - Android приложение

Android приложение для спортивных прогнозов с AI-анализом. Создано на базе Telegram бота [telegram-betting-bot](../telegram-betting-bot).

## 📋 Описание

Нативное Android приложение для получения AI-прогнозов на спортивные события с интеграцией:
- **OpenAI GPT-4o** — генерация детальных прогнозов
- **Google Gemini AI** — VIP экспрессы с двумя мнениями
- **The Odds API** — реальные коэффициенты букмекеров (100+ лиг)
- **RuStore Billing** — монетизация через подписки и пакеты
- **Firebase Auth** — регистрация и авторизация пользователей

---

## 🏗️ Технологический стек

- **Язык**: Kotlin 1.9+
- **UI**: Jetpack Compose + Material 3
- **Архитектура**: Clean Architecture + MVVM
- **DI**: Hilt (Dagger)
- **Async**: Kotlin Coroutines + Flow
- **Database**: Room + Firebase Firestore
- **Network**: Retrofit 2 + OkHttp 4

---

## 🚀 Быстрый старт

### 1. Клонирование проекта

```bash
cd ~/projects
git clone https://github.com/Rost-R/android-ai-betting-bot.git
cd android-ai-betting-bot
```

### 2. Настройка API ключей

Создайте файл `local.properties` в корне проекта:

```properties
sdk.dir=/path/to/Android/sdk
OPENAI_API_KEY=sk-proj-your-key-here
GEMINI_API_KEY=your-gemini-key-here
ODDS_API_KEY=your-odds-api-key-here
```

### 3. Настройка Firebase

1. Создайте проект в [Firebase Console](https://console.firebase.google.com/)
2. Добавьте Android приложение с package name: `com.aiprognoz.betting`
3. Включите методы аутентификации: Email/Password и Anonymous
4. Скачайте `google-services.json`
5. Поместите в `app/google-services.json`

### 4. Сборка и запуск

```bash
# Сборка debug APK
./gradlew assembleDebug

# Установка на эмулятор/устройство
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Запуск
adb shell am start -n com.aiprognoz.betting/.MainActivity
```

Или откройте проект в Android Studio и нажмите Run.

---

## 📦 Структура проекта

```
android-ai-betting-bot/
├── app/
│   ├── src/main/java/com/aiprognoz/betting/
│   │   ├── presentation/          # UI (Compose Screens)
│   │   │   ├── screens/
│   │   │   │   ├── home/          # Главный экран
│   │   │   │   ├── matches/       # Список матчей
│   │   │   │   ├── prediction/    # Прогноз матча
│   │   │   │   ├── auth/          # Авторизация
│   │   │   │   ├── payment/       # Оплата
│   │   │   │   └── profile/       # Профиль
│   │   │   ├── theme/             # Material 3 Theme
│   │   │   └── navigation/        # Навигация
│   │   │
│   │   ├── domain/                # Business Logic
│   │   │   ├── model/             # Data models
│   │   │   └── repository/        # Repository interfaces
│   │   │
│   │   ├── data/                  # Data Layer
│   │   │   ├── remote/            # API clients (Odds, OpenAI, Gemini)
│   │   │   ├── local/             # Room Database
│   │   │   ├── auth/              # Firebase Auth
│   │   │   ├── payment/           # RuStore Billing
│   │   │   └── repository/        # Repository implementations
│   │   │
│   │   ├── di/                    # Hilt DI modules
│   │   ├── MainActivity.kt
│   │   └── BettingApplication.kt
│   │
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 💰 Монетизация

### Продукты RuStore

| Продукт | Цена | Описание |
|---------|------|----------|
| 1 прогноз | 10₽ | Разовая покупка |
| 5 прогнозов | 45₽ | Скидка 10% |
| 10 прогнозов | 80₽ | Скидка 20% |
| 25 прогнозов | 175₽ | Скидка 30% |
| 50 прогнозов | 300₽ | Скидка 40% |
| 100 прогнозов | 500₽ | Скидка 50% |
| **VIP подписка** | **299₽/мес** | Ежедневные экспрессы (GPT-4o + Gemini) |

> **Примечание**: Платежи через RuStore временно работают в демо-режиме.

---

## 🔐 Безопасность API ключей

⚠️ **КРИТИЧЕСКИ ВАЖНО**: API ключи могут быть извлечены из APK!

### Рекомендации:
1. ✅ Использовать `local.properties` (не коммитить в Git)
2. ✅ Ограничить лимиты на стороне провайдеров API
3. ✅ ProGuard обфускация (включена в release)
4. ⚠️ Рассмотреть NDK + C++ для критичных ключей
5. ⚠️ Альтернатива: минимальный proxy сервер

---

## 🛠️ Разработка

### Запуск тестов

```bash
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests
```

### Сборка APK

```bash
# Debug
./gradlew assembleDebug

# Release (подписанный)
./gradlew assembleRelease
```

### Линтинг

```bash
./gradlew lint
```

---

## 📱 Публикация в RuStore

### Подготовка:
1. Обновите `versionCode` и `versionName` в `build.gradle.kts`
2. Создайте keystore для подписи:
   ```bash
   keytool -genkey -v -keystore release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key
   ```
3. Настройте подпись в `build.gradle.kts`
4. Соберите релизную сборку: `./gradlew bundleRelease`

### Загрузка в RuStore:
1. Зайдите в [RuStore Console](https://console.rustore.ru/)
2. Создайте новое приложение
3. Загрузите AAB файл
4. Заполните описание, скриншоты, категорию
5. Отправьте на модерацию

**Важно**: Подчеркните "информационный характер" прогнозов в описании!

---

## 🐛 Известные проблемы и решения

### 1. API ключи не работают
- Проверьте `local.properties`
- Убедитесь что ключи корректны (OpenAI, Gemini, The Odds API)

### 2. Firebase не инициализирован
- Убедитесь что `google-services.json` находится в `app/`
- Проверьте `package name` в Firebase Console

### 3. RuStore Billing не работает
- Платежи временно в демо-режиме
- Для production: интегрируйте реальный RuStore SDK

---

## 📊 План разработки

**Этапы:**
- [x] ✅ Этап 1: Настройка проекта
- [x] ✅ Этап 2: Core функционал (Room Database, The Odds API)
- [x] ✅ Этап 3: AI интеграции (OpenAI GPT-4o, Google Gemini)
- [x] ✅ Этап 4: Авторизация (Firebase Auth)
- [x] ✅ Этап 5: UI/UX (все экраны на Jetpack Compose)
- [x] ✅ Этап 6: Платежная система (RuStore Billing - демо режим)
- [ ] 🔄 Этап 7: Дополнительные функции (экспрессы, статистика)
- [ ] 🔄 Этап 8: Тестирование
- [ ] 🔄 Этап 9: Публикация в RuStore

---

## 📄 Лицензия

Проект создан для образовательных целей.

---

## 👨‍💻 Разработка

Создано с использованием:
- Clean Architecture
- MVVM Pattern
- Kotlin Coroutines
- Jetpack Compose

**Текущий статус**: MVP готов к тестированию. Основной функционал реализован.
