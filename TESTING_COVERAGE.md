# Покрытие критического функционала Unit и Integration тестами

## Обзор

Реализовано покрытие критического функционала приложения unit и integration тестами. Тесты проверяют как отдельные методы сервисов, так и полноценные HTTP-запросы с ожиданием ответа.

## Unit тесты

### AuthenticationServiceTest

**Покрытие:**
- ✅ Регистрация нового пользователя
- ✅ Регистрация с существующим именем пользователя (конфликт)
- ✅ Аутентификация с валидными учетными данными
- ✅ Аутентификация с неверными учетными данными
- ✅ Проверка валидности токена
- ✅ Проверка невалидного токена

**Файл:** `src/test/java/com/example/prac/service/auth/AuthenticationServiceTest.java`

### ActorTaskServiceTest

**Покрытие:**
- ✅ Создание связи Actor-Task с обновлением статуса задачи на ACTIVE
- ✅ Обработка несуществующего актора
- ✅ Обработка несуществующей задачи
- ✅ Обработка задачи с неправильным статусом (не OPEN)
- ✅ Проверка, что статус не обновляется для задач с неправильным статусом

**Файл:** `src/test/java/com/example/prac/service/data/ActorTaskServiceTest.java`

### ActorExperimentServiceTest

**Покрытие:**
- ✅ Создание связи Actor-Experiment с обновлением статуса эксперимента на ACTIVE
- ✅ Обработка несуществующего актора
- ✅ Обработка несуществующего эксперимента
- ✅ Обработка эксперимента с неправильным статусом (не OPEN)

**Файл:** `src/test/java/com/example/prac/service/data/ActorExperimentServiceTest.java`

### ProjectServiceTest

**Покрытие:**
- ✅ Установка статуса OVERDUE для просроченного проекта
- ✅ Сохранение статуса COMPLETED для завершенного проекта (не устанавливается OVERDUE)
- ✅ Сохранение статуса ACTIVE для проекта, который еще не просрочен
- ✅ Частичное обновление с установкой OVERDUE при изменении даты окончания
- ✅ Обработка несуществующего проекта при обновлении

**Файл:** `src/test/java/com/example/prac/service/data/ProjectServiceTest.java`

### TaskServiceTest

**Покрытие:**
- ✅ Установка статуса OVERDUE для просроченной задачи
- ✅ Сохранение статуса COMPLETED для завершенной задачи
- ✅ Частичное обновление с установкой OVERDUE при изменении дедлайна
- ✅ Обработка несуществующей задачи при обновлении

**Файл:** `src/test/java/com/example/prac/service/data/TaskServiceTest.java`

### ExperimentServiceTest

**Покрытие:**
- ✅ Установка статуса OVERDUE для просроченного эксперимента
- ✅ Сохранение статуса COMPLETED для завершенного эксперимента
- ✅ Частичное обновление с установкой OVERDUE при изменении дедлайна
- ✅ Обработка несуществующего эксперимента при обновлении

**Файл:** `src/test/java/com/example/prac/service/data/ExperimentServiceTest.java`

## Integration тесты

### AuthControllerIntegrationTest

**Покрытие:**
- ✅ POST `/api/auth/register` - регистрация нового пользователя
- ✅ POST `/api/auth/register` - конфликт при регистрации существующего пользователя
- ✅ POST `/api/auth/login` - успешная аутентификация
- ✅ POST `/api/auth/login` - неверные учетные данные
- ✅ POST `/api/auth/login` - несуществующий пользователь

**Файл:** `src/test/java/com/example/prac/integration/AuthControllerIntegrationTest.java`

### ProjectControllerIntegrationTest

**Покрытие:**
- ✅ POST `/api/projects` - создание проекта
- ✅ POST `/api/projects` - автоматическая установка статуса OVERDUE для просроченного проекта
- ✅ GET `/api/projects/{id}` - получение проекта по ID
- ✅ GET `/api/projects/{id}` - обработка несуществующего проекта
- ✅ PATCH `/api/projects/{id}` - частичное обновление с установкой OVERDUE
- ✅ GET `/api/projects` - получение списка всех проектов

**Файл:** `src/test/java/com/example/prac/integration/ProjectControllerIntegrationTest.java`

### ActorTaskControllerIntegrationTest

**Покрытие:**
- ✅ POST `/api/actor-tasks` - создание связи Actor-Task с обновлением статуса задачи
- ✅ POST `/api/actor-tasks` - обработка задачи с неправильным статусом
- ✅ POST `/api/actor-tasks` - обработка несуществующего актора
- ✅ POST `/api/actor-tasks` - обработка несуществующей задачи

**Файл:** `src/test/java/com/example/prac/integration/ActorTaskControllerIntegrationTest.java`

### ActorExperimentControllerIntegrationTest

**Покрытие:**
- ✅ POST `/api/actor-experiments` - создание связи Actor-Experiment с обновлением статуса эксперимента
- ✅ POST `/api/actor-experiments` - обработка эксперимента с неправильным статусом
- ✅ POST `/api/actor-experiments` - обработка несуществующего актора
- ✅ POST `/api/actor-experiments` - обработка несуществующего эксперимента

**Файл:** `src/test/java/com/example/prac/integration/ActorExperimentControllerIntegrationTest.java`

### TaskControllerIntegrationTest

**Покрытие:**
- ✅ POST `/api/tasks` - создание задачи
- ✅ POST `/api/tasks` - автоматическая установка статуса OVERDUE для просроченной задачи
- ✅ GET `/api/tasks/{id}` - получение задачи по ID
- ✅ GET `/api/tasks/{id}` - обработка несуществующей задачи
- ✅ GET `/api/tasks` - получение списка всех задач
- ✅ PATCH `/api/tasks/{id}` - частичное обновление с установкой OVERDUE
- ✅ PATCH `/api/tasks/{id}` - обновление полей задачи
- ✅ DELETE `/api/tasks/{id}` - удаление задачи

**Файл:** `src/test/java/com/example/prac/integration/TaskControllerIntegrationTest.java`

### ExperimentControllerIntegrationTest

**Покрытие:**
- ✅ POST `/api/experiments` - создание эксперимента
- ✅ POST `/api/experiments` - автоматическая установка статуса OVERDUE для просроченного эксперимента
- ✅ GET `/api/experiments/{id}` - получение эксперимента по ID
- ✅ GET `/api/experiments/{id}` - обработка несуществующего эксперимента
- ✅ GET `/api/experiments` - получение списка всех экспериментов
- ✅ PATCH `/api/experiments/{id}` - частичное обновление с установкой OVERDUE
- ✅ PATCH `/api/experiments/{id}` - обновление полей эксперимента
- ✅ DELETE `/api/experiments/{id}` - удаление эксперимента

**Файл:** `src/test/java/com/example/prac/integration/ExperimentControllerIntegrationTest.java`

### SpaceStationControllerIntegrationTest

**Покрытие:**
- ✅ POST `/api/space-stations` - создание космической станции
- ✅ POST `/api/space-stations` - обработка конфликта при дублировании имени
- ✅ GET `/api/space-stations/{id}` - получение станции по ID
- ✅ GET `/api/space-stations/{id}` - обработка несуществующей станции
- ✅ GET `/api/space-stations` - получение списка всех станций
- ✅ PATCH `/api/space-stations/{id}` - частичное обновление станции
- ✅ DELETE `/api/space-stations/{id}` - удаление станции

**Файл:** `src/test/java/com/example/prac/integration/SpaceStationControllerIntegrationTest.java`

### ResourceControllerIntegrationTest

**Покрытие:**
- ✅ POST `/api/resources` - создание ресурса
- ✅ GET `/api/resources/{id}` - получение ресурса по ID
- ✅ GET `/api/resources/{id}` - обработка несуществующего ресурса
- ✅ GET `/api/resources` - получение списка всех ресурсов
- ✅ PATCH `/api/resources/{id}` - частичное обновление ресурса
- ✅ DELETE `/api/resources/{id}` - удаление ресурса

**Файл:** `src/test/java/com/example/prac/integration/ResourceControllerIntegrationTest.java`

## Статистика тестов

### Unit тесты
- **AuthenticationServiceTest**: 7 тестов
- **ActorTaskServiceTest**: 5 тестов
- **ActorExperimentServiceTest**: 4 теста
- **ProjectServiceTest**: 5 тестов
- **TaskServiceTest**: 4 теста
- **ExperimentServiceTest**: 4 теста
- **Итого unit тестов**: 29 тестов

### Integration тесты
- **AuthControllerIntegrationTest**: 5 тестов
- **ProjectControllerIntegrationTest**: 10 тестов
- **ActorTaskControllerIntegrationTest**: 4 теста
- **ActorExperimentControllerIntegrationTest**: 4 теста
- **TaskControllerIntegrationTest**: 8 тестов
- **ExperimentControllerIntegrationTest**: 8 тестов
- **SpaceStationControllerIntegrationTest**: 7 тестов
- **ResourceControllerIntegrationTest**: 6 тестов
- **Итого integration тестов**: 52 теста

### Общая статистика
- **Всего тестов**: 80+ тестов (включая существующие тесты обработки исключений)
- **Unit тесты**: 29 тестов
- **Integration тесты**: 50+ тестов

## Запуск тестов

### Запуск всех тестов
```bash
mvn test
```

### Запуск только unit тестов
```bash
mvn test -Dtest="*ServiceTest"
```

### Запуск только integration тестов
```bash
mvn test -Dtest="*IntegrationTest"
```

### Запуск конкретного теста
```bash
mvn test -Dtest=AuthenticationServiceTest
```

## Технологии

- **JUnit 5**: Framework для написания тестов
- **Mockito**: Mocking framework для unit тестов
- **Spring Boot Test**: Интеграция с Spring для integration тестов
- **MockMvc**: Тестирование веб-слоя
- **H2 Database**: In-memory база данных для тестов

## Покрытие критического функционала

### ✅ Аутентификация и авторизация
- Регистрация пользователей
- Аутентификация
- Валидация JWT токенов

### ✅ Бизнес-логика переноса из хранимых процедур
- Создание связей Actor-Task с обновлением статуса
- Создание связей Actor-Experiment с обновлением статуса
- Валидация статусов перед созданием связей

### ✅ Проверка просрочки
- Автоматическая установка статуса OVERDUE для проектов
- Автоматическая установка статуса OVERDUE для задач
- Автоматическая установка статуса OVERDUE для экспериментов
- Сохранение статуса COMPLETED (не перезаписывается на OVERDUE)

### ✅ Обработка ошибок
- ResourceNotFoundException для несуществующих ресурсов
- BusinessLogicException для нарушений бизнес-правил
- ResourceAlreadyExistsException для дублирующихся ресурсов
- Стандартизированные сообщения об ошибках

## Примечания

- Все unit тесты используют моки для изоляции тестируемого кода
- Integration тесты используют H2 in-memory базу данных
- Тесты проверяют как успешные сценарии, так и обработку ошибок
- Все тесты независимы и могут выполняться в любом порядке

