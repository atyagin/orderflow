# OrderFlow

Учебный бэкенд мини-маркетплейса: заказы, склад, оплата через внешний провайдер, уведомления, приём аналитических событий.
Цель проекта — прокачать Java/Spring на задачах, которые спрашивают на собеседованиях (надёжность, конкурентность, высокая нагрузка, распределённая обработка).

Роадмап и статус задач: [`tasks/README.md`](tasks/README.md). Архитектурные решения: [`docs/adr`](docs/adr).

## Стек

Java 21, Spring Boot 3.5, Maven, PostgreSQL 17, Kafka (KRaft), Redis, Flyway, Testcontainers, WireMock, Prometheus + Grafana.

## Требования

- JDK 21+ (для задачи 3.4 дополнительно JDK 25)
- Maven 3.9+ (один раз, чтобы сгенерировать wrapper; дальше `./mvnw`)
- Docker (Docker Desktop, OrbStack, Colima или Rancher Desktop)

## Первый запуск

```bash
# Maven Wrapper (один раз, результат коммитим)
mvn -N wrapper:wrapper -Dmaven=3.9.9

# Инфраструктура
docker compose up -d
docker compose ps            # все сервисы должны быть healthy

# Сборка + юнит-тесты + интеграционные тесты (нужен Docker)
./mvnw verify                # на Windows (cmd/PowerShell): mvnw.cmd verify
```

## Запуск приложения

| Способ | Команда | Инфраструктура |
|---|---|---|
| Против docker-compose | Run `OrderFlowApplication` в IDE или `./mvnw -pl orderflow-app spring-boot:run` | `docker compose up -d` |
| Против Testcontainers | Run `TestOrderFlowApplication` (src/test) или `./mvnw -pl orderflow-app spring-boot:test-run` | поднимется сама |

Проверка: http://localhost:8080/actuator/health

```Тесты
# Только unit тесты
./mvnw test

# интеграционные тесты + unit тесты
./mvnw verify

# Только интеграционные тесты
./mvnw verify -DskipUnitTests=true
```

## Порты

| Сервис | Адрес | Примечание |
|---|---|---|
| Приложение | localhost:8080 | |
| PostgreSQL | localhost:5432 | orderflow / orderflow |
| Kafka | localhost:9092 | изнутри compose-сети: `kafka:29092` |
| Redis | localhost:6379 | |
| Платёжный провайдер (WireMock) | localhost:8089 | стабы в `infra/wiremock/mappings`, админка `/__admin` |
| Kafka UI | localhost:8090 | `docker compose --profile tools up -d` |
| Prometheus | localhost:9090 | `docker compose --profile observability up -d` |
| Grafana | localhost:3000 | там же, вход анонимный |

## Структура

```
orderflow/
├── pom.xml                      # parent: версии, enforcer (Java 21+, Maven 3.9)
├── orderflow-app/               # модульный монолит
│   └── src/main/java/dev/orderflow/
│       ├── common/              # инфраструктура без домена
│       ├── order/               # заказы
│       ├── inventory/           # склад
│       ├── payment/             # оплата
│       ├── notification/        # уведомления
│       └── analytics/           # трекинг-события, high load
├── infra/                       # конфиги для docker-compose
├── tasks/                       # условия задач
├── docs/adr/                    # архитектурные решения
└── CLAUDE.md                    # инструкции для Claude Code (режим ревьюера)
```

Позже появятся отдельные сервисы (один из них на Gradle) и манифесты для k8s.

## Соглашения

- Юнит-тесты: `*Test` (surefire, фаза `test`, без Docker). Интеграционные: `*IT` (failsafe, фаза `verify`, Testcontainers).
- Схема БД меняется только миграциями Flyway. Применённые миграции не редактируются.
- Модули общаются через публичный API пакета, а не через внутренние классы друг друга.
- Время берём из бина `Clock`.
- Одна задача — одна ветка `task/<номер>-<название>`.

## Windows + WSL2

Рекомендуемая схема: IDE и JDK на Windows, Docker через **Docker Desktop с WSL2-бэкендом**. Тогда `docker compose` и Testcontainers работают из Windows без дополнительной настройки, а контейнеры доступны на `localhost`.

Если Docker Engine установлен прямо в WSL (без Docker Desktop):

- Проще всего открыть проект **внутри WSL** (`~/projects/orderflow`, не `/mnt/c/...`) и запускать `./mvnw` оттуда. IntelliJ и VS Code умеют работать с WSL-проектами.
- Если JVM и тесты запускаются на Windows, Testcontainers не увидит Docker из WSL. Нужно открыть демон по TCP только на localhost и выставить на Windows `DOCKER_HOST=tcp://localhost:2375`. Это рабочий, но более хрупкий вариант.

Postgres, поставленный в WSL через apt, подходит для запуска приложения, но интеграционные тесты всё равно поднимают свой Postgres в Testcontainers, так что Docker нужен в любом случае. Если используешь нативный Postgres, удали или закомментируй сервис `postgres` в `docker-compose.yml` (иначе конфликт порта 5432), создай пользователя и базу `orderflow` и добавь `shared_preload_libraries = 'pg_stat_statements'` в `postgresql.conf`.

## Если что-то не работает

- **Тесты падают с ошибкой подключения к Docker.** Проверь `docker info`. Для Colima/Podman может понадобиться `DOCKER_HOST` и `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE`, см. документацию Testcontainers.
- **Ошибка вида "client version ... is too old".** Версия Testcontainers не дружит с твоим Docker Engine. Переопредели `<testcontainers.version>` в `properties` корневого pom на более новую.
- **Порт занят.** Поменяй левую часть маппинга портов в `docker-compose.yml`.
- **Хочу чистую базу.** `docker compose down -v` (удалит volume с данными).
