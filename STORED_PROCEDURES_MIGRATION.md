# Миграция бизнес-логики из хранимых процедур в сервисный слой

## Обзор

Вся бизнес-логика, ранее реализованная в хранимых процедурах PostgreSQL, перенесена в сервисный слой приложения. Это улучшает тестируемость, переносимость и поддерживаемость кода.

## Перенесенная логика

### 1. Процедура `add_actor_to_task`

**Было (PostgreSQL):**
```sql
CREATE OR REPLACE PROCEDURE add_actor_to_task(
    p_actor_id INT,
    p_task_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO Actor_Task (actor_id, task_id)
    VALUES (p_actor_id, p_task_id);

    UPDATE Task
    SET status = 'ACTIVE'
    WHERE task_id = p_task_id AND status = 'OPEN';
END;
$$;
```

**Стало (Java):**
```java
@Transactional
public ActorTaskDTO save(ActorTaskDTO actorTaskDTO) {
    // Проверка существования актора
    Actor actor = actorRepository.findById(actorTaskDTO.getActorId())
            .orElseThrow(() -> new ResourceNotFoundException("Actor", ...));

    // Проверка существования задачи и её статуса
    Task task = taskRepository.findById(actorTaskDTO.getTaskId())
            .orElseThrow(() -> new ResourceNotFoundException("Task", ...));

    if (!Objects.equals(task.getStatus(), "OPEN")) {
        throw new BusinessLogicException("Task is not open...");
    }

    // Создание и сохранение связи Actor-Task
    ActorTask actorTask = new ActorTask();
    actorTask.setActor(actor);
    actorTask.setTask(task);
    actorTaskRepository.save(actorTask);

    // Обновление статуса задачи на ACTIVE, если она была OPEN
    if ("OPEN".equals(task.getStatus())) {
        task.setStatus("ACTIVE");
        taskRepository.save(task);
    }

    return actorTaskDTO;
}
```

### 2. Процедура `add_actor_to_experiment`

**Было (PostgreSQL):**
```sql
CREATE OR REPLACE PROCEDURE add_actor_to_experiment(
    p_actor_id INT,
    p_experiment_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO Actor_Experiment (actor_id, experiment_id)
    VALUES (p_actor_id, p_experiment_id);

    UPDATE Experiment
    SET status = 'ACTIVE'
    WHERE experiment_id = p_experiment_id AND status = 'OPEN';
END;
$$;
```

**Стало (Java):**
Аналогично `add_actor_to_task`, но для экспериментов.

### 3. Триггеры проверки просрочки

**Было (PostgreSQL триггеры):**
```sql
CREATE OR REPLACE FUNCTION check_project_overdue()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status != 'COMPLETED' AND CURRENT_DATE > NEW.end_date THEN
        NEW.status := 'OVERDUE';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

**Стало (Java):**
```java
private void checkAndSetOverdueStatus(Project project) {
    if (project.getStatus() != null && !"COMPLETED".equals(project.getStatus())
            && project.getEndDate() != null
            && LocalDate.now().isAfter(project.getEndDate())) {
        project.setStatus("OVERDUE");
    }
}
```

Аналогичная логика добавлена для `Task` и `Experiment`.

## Преимущества миграции

### 1. Тестируемость
- Бизнес-логика теперь может быть протестирована с помощью unit-тестов
- Не требуется подключение к базе данных для тестирования логики
- Можно использовать моки для репозиториев

### 2. Переносимость
- Код не зависит от специфичных функций PostgreSQL
- Легче мигрировать на другую СУБД
- Бизнес-логика централизована в одном месте

### 3. Поддерживаемость
- Вся логика на Java, что упрощает понимание и изменение
- Единый стиль кода во всем приложении
- Использование стандартных механизмов Spring (транзакции, исключения)

### 4. Интеграция с обработкой ошибок
- Использование кастомных исключений (`ResourceNotFoundException`, `BusinessLogicException`)
- Стандартизированные сообщения об ошибках
- Автоматическая обработка через `GlobalExceptionHandler`

## Изменения в сервисах

### ActorTaskService
- ✅ Удален вызов `CALL add_actor_to_task`
- ✅ Логика создания `ActorTask` перенесена в Java
- ✅ Логика обновления статуса `Task` перенесена в Java
- ✅ Использование кастомных исключений вместо `RuntimeException`

### ActorExperimentService
- ✅ Удален вызов `CALL add_actor_to_experiment`
- ✅ Логика создания `ActorExperiment` перенесена в Java
- ✅ Логика обновления статуса `Experiment` перенесена в Java
- ✅ Использование кастомных исключений вместо `RuntimeException`

### ProjectService
- ✅ Добавлен метод `checkAndSetOverdueStatus()`
- ✅ Вызывается в `save()` и `partialUpdate()`
- ✅ Логика перенесена из триггера `check_project_overdue`

### TaskService
- ✅ Добавлен метод `checkAndSetOverdueStatus()`
- ✅ Вызывается в `save()` и `partialUpdate()`
- ✅ Логика перенесена из триггера `check_task_overdue`

### ExperimentService
- ✅ Добавлен метод `checkAndSetOverdueStatus()`
- ✅ Вызывается в `save()` и `partialUpdate()`
- ✅ Логика перенесена из триггера `check_experiment_overdue`

## Удаление зависимостей

После миграции больше не требуется:
- `EntityManager` для выполнения нативных SQL-запросов (в `ActorTaskService` и `ActorExperimentService`)
- Хранимые процедуры в базе данных
- Триггеры в базе данных (опционально, можно оставить для дополнительной защиты)

## Обратная совместимость

**Важно:** Если хранимые процедуры и триггеры все еще существуют в базе данных, они будут продолжать работать параллельно с Java-логикой. Рекомендуется удалить их после проверки работоспособности новой реализации.

## Миграция базы данных

Для полного перехода можно создать миграцию, которая удаляет хранимые процедуры и триггеры:

```sql
-- Удаление процедур
DROP PROCEDURE IF EXISTS add_actor_to_task;
DROP PROCEDURE IF EXISTS add_actor_to_experiment;

-- Удаление триггеров (опционально)
DROP TRIGGER IF EXISTS trg_check_project_overdue ON Project;
DROP TRIGGER IF EXISTS trg_check_task_overdue ON Task;
DROP TRIGGER IF EXISTS trg_check_experiment_overdue ON Experiment;

-- Удаление функций (опционально)
DROP FUNCTION IF EXISTS check_project_overdue();
DROP FUNCTION IF EXISTS check_task_overdue();
DROP FUNCTION IF EXISTS check_experiment_overdue();
```

## Тестирование

Рекомендуется создать тесты для проверки:
1. Создания связи Actor-Task с обновлением статуса
2. Создания связи Actor-Experiment с обновлением статуса
3. Проверки просрочки проектов, задач и экспериментов
4. Обработки ошибок (несуществующие сущности, неправильный статус)

## Дополнительные улучшения

В будущем можно рассмотреть:
- Использование `@PrePersist` и `@PreUpdate` JPA callbacks для автоматической проверки просрочки
- Создание отдельного сервиса для проверки просрочки (если логика станет сложнее)
- Использование Spring Events для уведомлений о просроченных задачах

