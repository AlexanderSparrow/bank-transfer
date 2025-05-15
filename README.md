# 💸 Bank Transfer Service

Сервис для перевода средств между пользователями с поддержкой авторизации, фильтрации, кэширования, валидации и unit/integration тестирования.

**Репозиторий проекта:**  
🔗 [github.com/AlexanderSparrow/bank-transfer](https://github.com/AlexanderSparrow/bank-transfer)

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/AlexanderSparrow/bank-transfer)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## 🚀 Инструкции по запуску

### 🔧 Требования
- **Java**: 17+
- **Maven**: 3.8+
- **Docker** и **Docker Compose**
- **PostgreSQL** (обеспечивается Docker Compose)
- **Redis** (обеспечивается Docker Compose)
- **Elasticsearch** (обеспечивается Docker Compose, опционально для полнотекстового поиска)

### ⚙️ Установка и запуск

1. **Клонируйте репозиторий**:
   ```bash
   git clone https://github.com/AlexanderSparrow/bank-transfer.git
   cd bank-transfer
   ```

2. **Настройте окружение**:
    - Создайте или отредактируйте файл `application.properties` в `src/main/resources`:
      ```properties
      spring.application.name=bank-transfer
      spring.sql.init.mode=always
      spring.datasource.url=jdbc:postgresql://localhost:5432/bt-db
      spring.datasource.username=user
      spring.datasource.password=secret
      spring.jpa.hibernate.ddl-auto=validate
      spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
      spring.jpa.properties.hibernate.format_sql=true
      spring.cache.type=redis
      spring.cache.redis.time-to-live=10m
      spring.data.redis.host=localhost
      spring.data.redis.port=6379
      jwt.secret=pjdZQuMrnaGFrBHQKbVW3PepNQ+i5C6SJ3IIVxLHibtYQW7qqGH73lue0wSpkCgez53NmdETPI5M9nWHdDS5tQ==
      jwt.expiration=86400000
      spring.jackson.date-format=dd.MM.yyyy
      spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false
      logging.level.org.springframework.orm.jpa=INFO
      logging.level.org.springframework.transaction=INFO
      logging.level.org.hibernate.SQL=DEBUG
      logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
      ```
    - (Опционально) Для использования Elasticsearch добавьте:
      ```properties
      spring.elasticsearch.uris=http://localhost:9200
      ```

3. **Запустите инфраструктуру** (PostgreSQL, Redis, Elasticsearch):
   ```bash
   docker-compose up -d
   ```

4. **Соберите и запустите приложение**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Откройте Swagger UI** для тестирования API:
   ```
   http://localhost:8080/swagger-ui.html
   ```

### 🔐 Аутентификация
Аутентификация реализована через **JWT** (токен действителен 24 часа). Поддерживаются два способа логина:
- **По email и паролю**
- **По телефону и паролю**

**Эндпоинт для логина**:
```
POST /auth/login
```

**Пример ответа**:
```json
{
  "token": "Bearer eyJhbGciOiJIUzUxMiJ9..."
}
```

Для защищённых эндпоинтов добавьте заголовок:
```
Authorization: Bearer <token>
```

---

## 📘 Описание API

### 🔑 Аутентификация
| Метод | URL            | Описание                       |
|-------|----------------|--------------------------------|
| POST  | `/auth/login`  | Получение JWT по email/phone   |

### 👤 Пользователи
| Метод | URL                    | Описание                                |
|-------|------------------------|-----------------------------------------|
| GET   | `/users/me`            | Информация о текущем пользователе       |
| GET   | `/users/search`        | Поиск пользователей с фильтрацией       |
| POST  | `/users/email`         | Добавление email                       |
| DELETE| `/users/email`         | Удаление email                         |
| POST  | `/users/phone`         | Добавление телефона                    |
| DELETE| `/users/phone`         | Удаление телефона                      |

**Фильтрация** в `/users/search` поддерживает параметры: `name`, `email`, `phone`, `dateOfBirthAfter`, `page`, `size`.

### 💳 Переводы
| Метод | URL            | Описание                           |
|-------|----------------|------------------------------------|
| POST  | `/transfer`    | Перевод денег другому пользователю |

---

## 🛠️ Технологии
| Технология             | Назначение                              |
|------------------------|-----------------------------------------|
| **Spring Boot**        | Основной фреймворк                     |
| **Spring Security + JWT** | Аутентификация и авторизация         |
| **Spring Data JPA**    | Работа с базой данных                  |
| **PostgreSQL**         | Основное хранилище                     |
| **Redis**              | Кэширование                            |
| **Elasticsearch**      | Полнотекстовый поиск (опционально)     |
| **Hibernate Validator**| Валидация DTO                          |
| **Swagger / OpenAPI**  | Документация и тестирование API        |
| **Testcontainers + JUnit5** | Интеграционные тесты              |
| **Mockito**            | Unit-тестирование                      |
| **Docker Compose**     | Развёртывание инфраструктуры           |

---

## ⚙️ Архитектурные решения
- **Потокобезопасность**: Переводы защищены от конкурентных операций с помощью `@Lock(PESSIMISTIC_WRITE)`.
- **Кэширование**: Поиск пользователей кэшируется в Redis (`@Cacheable`, TTL 10 минут).
- **JWT**: Токен содержит только `userId` для минимизации размера.
- **Фильтрация**: Поиск пользователей реализован через `Specification` для гибкости.
- **Тестирование**: Интеграционные тесты используют PostgreSQL и Redis через Testcontainers.

---

## Дамп БД
![drawSQL.png](assets/drawSQL.png): [schema.sql](/src/main/resources/schema.sql)
---

## 🧪 Запуск тестов
```bash
mvn test
```

---

## 📁 Структура проекта
```
ru.pionerpixel.banktransfer
├── config/           # Конфигурации (Security, Swagger, Redis)
├── controller/       # REST-контроллеры
├── dto/              # Модели запросов и ответов
├── exception/        # Кастомные исключения
├── handler/          # Обработчики ошибок
├── mapper/           # MapStruct мапперы
├── model/            # Entity-классы
├── repository/       # Репозитории Spring Data JPA
├── scheduler/        # Бизнес-логика автоматического увеличеня баланса 
├── security/         # JWT-фильтр и утилиты для токенов
├── service/          # Бизнес-логика
├── specification/    # Логика фильтрации пользователей
├── utils/            # Вспомогательные утилиты
└── BankTransferApplication.java
```

---

## 📌 Возможные улучшения
- Добавить таблицу для хранения истории переводов.
- Ввести дневные лимиты на количество и сумму переводов.
- Дополнить аккаунт статусом активности для проверки ппи транзакциями.
- Интегрировать ElasticSearch для полнотекстового поиска пользователей.
- Реализовать rate-limiting через Redis или фильтр.

---

## 📜 Лицензия
Проект распространяется под лицензией [MIT](LICENSE).

---

## 👤 Автор
**Александр Горобец**  
GitHub: [github.com/AlexanderSparrow](https://github.com/AlexanderSparrow)