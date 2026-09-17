# Роадмап

Отмечай `[x]`, когда задача прошла ревью. Этапы 0 и 1 делаем первыми, остальное можно переставлять.
Дополнительные блоки (S, J, D, R, Q) вставляем между этапами, когда хочется сменить тему.

## Этап 0 — Фундамент
- [ ] 0.1 Скелет, docker-compose, миграции, Testcontainers
- [ ] 0.2 Health checks: liveness/readiness, кастомные индикаторы, graceful shutdown

## Этап 1 — Домен и БД
- [ ] 1.1 Агрегат Order (orders + order_items), инварианты, статусная модель (State)
- [ ] 1.2 N+1: воспроизвести и исправить (fetch join, EntityGraph, batch size, DTO-проекции)
- [ ] 1.3 Индексы на 5 млн строк, EXPLAIN ANALYZE, keyset-пагинация
- [ ] 1.4 Списание остатков без овербукинга (optimistic/pessimistic locking, изоляция)
- [ ] 1.5 Strategy / Chain of Responsibility / Factory: провайдеры оплаты, валидация заказа

## Этап 2 — Надёжность и гарантии
- [ ] 2.1 Transactional outbox → Kafka
- [ ] 2.2 Идемпотентный консьюмер + Idempotency-Key в API
- [ ] 2.3 Ретраи, DLT, circuit breaker (Resilience4j)
- [ ] 2.4 Сага: заказ → резерв → оплата с компенсациями

## Этап 3 — Конкурентность и асинхронность
- [ ] 3.1 CompletableFuture: параллельная сборка из медленных источников, таймауты, фолбэки
- [ ] 3.2 Батчер: 10k или 1 секунда, flush при остановке, backpressure
- [ ] 3.3 Асинхронная очередь: ограниченный параллелизм + порядок по ключу
- [ ] 3.4 Virtual threads против platform threads, замер; pinning на JDK 21 против JDK 25

## Этап 4 — Распределение по подам
- [ ] 4.1 Обработка зависших заказов на 3 подах без дублей (SKIP LOCKED)
- [ ] 4.2 Cron ровно на одном поде (ShedLock / leader election)
- [ ] 4.3 Kafka consumer group: партиции, ребалансировка, оффсеты
- [ ] 4.4 Деплой в kind, 3 реплики, rolling update

## Этап 5 — Масштабирование данных
- [ ] 5.1 Партиционирование таблицы событий по месяцам
- [ ] 5.2 Шардирование заказов по user_id на 2 инстанса Postgres

## Этап 6 — High load
- [ ] 6.1 Приём трекинг-событий: k6, профилирование, тюнинг
- [ ] 6.2 Кэш каталога в Redis, защита от cache stampede
- [ ] 6.3 Распределённый rate limiting
- [ ] 6.4 Метрики и дашборд: RPS, p99, consumer lag

## S — Spring под капотом
- [ ] S.1 Ловушки @Transactional
- [ ] S.2 Свой Spring Boot starter
- [ ] S.3 LazyInitializationException и open-in-view

## J — JVM и диагностика
- [ ] J.1 Утечка памяти по heap dump
- [ ] J.2 Deadlock и исчерпание пула по thread dump
- [ ] J.3 G1 против ZGC, GC-логи

## D — Данные и интеграции
- [ ] D.1 Экспорт 10 млн строк в CSV без OOM
- [ ] D.2 Импорт CSV 1 ГБ с отчётом об ошибках
- [ ] D.3 Zero-downtime миграция на 50 млн строк
- [ ] D.4 CDC через Debezium против polling-outbox
- [ ] D.5 Avro + Schema Registry, эволюция схемы
- [ ] D.6 Сверка с платёжным провайдером
- [ ] D.7 Отложенные задачи с backoff на базе БД

## R — Распределённые системы
- [ ] R.1 Распределённый лок на Redis и fencing token
- [ ] R.2 Kafka transactions, exactly-once read-process-write
- [ ] R.3 Hot partition
- [ ] R.4 CQRS, пересборка проекции
- [ ] R.5 OpenTelemetry: HTTP → Kafka → консьюмер
- [ ] R.6 Chaos-тесты через Toxiproxy

## Q — Качество и архитектура
- [ ] Q.1 ArchUnit на границы модулей
- [ ] Q.2 Mutation testing (PIT)
- [ ] Q.3 Spring Security, JWT, доступ только к своим заказам
- [ ] Q.4 ADR на крупные решения (ведём постоянно)

## Build — Gradle
- [ ] G.1 Сервис на Gradle Kotlin DSL: version catalog, convention-плагин, кастомные задачи

## Live coding (без библиотек)
- [ ] Потокобезопасный LRU-кэш
- [ ] Token bucket rate limiter
- [ ] Пул объектов
- [ ] Bounded blocking queue на ReentrantLock + Condition
