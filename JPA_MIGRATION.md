# Миграция на Spring Data JPA

## Обзор

Проект полностью переведен с legacy-конфигурации Hibernate на Spring Data JPA. Все устаревшие настройки удалены, код использует стандартные механизмы Spring Data JPA.

## Выполненные изменения

### 1. Удаление Legacy-конфигурации Hibernate

#### Удален файл `HibernateConfig.java`
- Удален класс с `LocalSessionFactoryBean` (Hibernate 5 legacy API)
- Удалены ручные настройки `SessionFactory`
- Удалены устаревшие свойства Hibernate

**Было:**
```java
@Configuration
public class HibernateConfig {
    @Bean
    @Primary
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        // Legacy Hibernate 5 конфигурация
    }
}
```

**Стало:** Конфигурация полностью удалена, Spring Boot автоматически настраивает JPA через `application.yml`

### 2. Очистка `application.yml`

#### Удалены устаревшие настройки:
- `hibernate.dialect` (теперь через `spring.jpa.properties.hibernate.dialect`)
- `hibernate.hbm2ddl.auto` (заменено на `spring.jpa.hibernate.ddl-auto`)
- `hibernate.show_sql` (заменено на `spring.jpa.show-sql`)
- `hibernate.jdbc.lob.non_contextual_creation` (больше не требуется)
- `hibernate.use_sql_comments` (удалено)
- `hibernate.default_schema` (удалено)
- `generate-ddl` (удалено, используется только `ddl-auto`)

#### Оставлены только необходимые настройки:
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

### 3. Замена SessionFactory на EntityManager

#### ActorTaskService и ActorExperimentService

**Было:**
```java
private final SessionFactory sessionFactory;

public ActorTaskDTO save(ActorTaskDTO dto) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    // ...
    session.getTransaction().commit();
}
```

**Стало:**
```java
@PersistenceContext
private EntityManager entityManager;

@Transactional
public ActorTaskDTO save(ActorTaskDTO dto) {
    // Spring управляет транзакциями автоматически
    entityManager.createNativeQuery("CALL add_actor_to_task(:actorId, :taskId)")
        .setParameter("actorId", dto.getActorId())
        .setParameter("taskId", dto.getTaskId())
        .executeUpdate();
}
```

**Преимущества:**
- Использование стандартного JPA `EntityManager` вместо Hibernate `Session`
- Автоматическое управление транзакциями через `@Transactional`
- Интеграция с Spring Transaction Management
- Нет необходимости вручную открывать/закрывать сессии

### 4. Проверка репозиториев

Все репозитории используют Spring Data JPA:

```java
public interface ActorTaskRepository extends CrudRepository<ActorTask, Integer> {
}

public interface ProjectRepository extends CrudRepository<Project, Integer> {
    List<Project> findByNameContaining(String substring);
}
```

**Преимущества Spring Data JPA:**
- Автоматическая генерация реализации репозиториев
- Поддержка методов по соглашению об именовании
- Возможность использования `@Query` для кастомных запросов
- Интеграция с Spring транзакциями
- Упрощенный код без boilerplate

## Архитектура после миграции

```
┌─────────────────────────────────────┐
│      Spring Boot Application        │
│                                     │
│  ┌───────────────────────────────┐ │
│  │   Spring Data JPA             │ │
│  │   (автоматическая конфигурация)│ │
│  └───────────────────────────────┘ │
│           │                        │
│  ┌───────────────────────────────┐ │
│  │   EntityManager (JPA)         │ │
│  │   (управляется Spring)        │ │
│  └───────────────────────────────┘ │
│           │                        │
│  ┌───────────────────────────────┐ │
│  │   Hibernate (ORM)             │ │
│  │   (используется как провайдер)│ │
│  └───────────────────────────────┘ │
│           │                        │
│  ┌───────────────────────────────┐ │
│  │   PostgreSQL Database         │ │
│  └───────────────────────────────┘ │
└─────────────────────────────────────┘
```

## Настройки JPA

### Автоматическая конфигурация Spring Boot

Spring Boot автоматически настраивает:
- `EntityManagerFactory` на основе `DataSource`
- `PlatformTransactionManager` для управления транзакциями
- Репозитории Spring Data JPA
- Интеграцию с Hibernate как провайдером JPA

### Ручная настройка не требуется

В отличие от legacy-подхода, больше не нужно:
- Создавать `SessionFactory` вручную
- Настраивать `LocalSessionFactoryBean`
- Управлять жизненным циклом сессий Hibernate
- Настраивать транзакции вручную

## Использование EntityManager

Для выполнения нативных SQL-запросов (например, хранимых процедур):

```java
@Service
@AllArgsConstructor
public class ActorTaskService {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Transactional
    public void executeNativeQuery() {
        entityManager.createNativeQuery("CALL procedure_name(:param)")
            .setParameter("param", value)
            .executeUpdate();
    }
}
```

**Важно:**
- Используйте `@PersistenceContext` для инъекции `EntityManager`
- Используйте `@Transactional` для методов, изменяющих данные
- Spring автоматически управляет транзакциями

## Преимущества миграции

1. **Упрощение кода**
   - Меньше boilerplate кода
   - Автоматическая конфигурация
   - Стандартные Spring механизмы

2. **Лучшая интеграция**
   - Полная интеграция с Spring транзакциями
   - Автоматическое управление жизненным циклом
   - Упрощенное тестирование

3. **Современный подход**
   - Использование стандартов JPA
   - Spring Data JPA best practices
   - Готовность к обновлениям

4. **Производительность**
   - Оптимизация через Spring
   - Кэширование на уровне Spring
   - Эффективное управление соединениями

## Проверка миграции

### Компиляция
```bash
mvn clean compile
```

### Тесты
```bash
mvn test
```

### Проверка отсутствия legacy-кода
```bash
grep -r "SessionFactory\|LocalSessionFactoryBean\|hibernate5" src/
```

## Обратная совместимость

Все существующие репозитории и сервисы продолжают работать без изменений:
- Методы репозиториев работают как прежде
- Entity классы не изменились
- Бизнес-логика сохранена

## Дополнительные ресурсы

- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [JPA Specification](https://jakarta.ee/specifications/persistence/)
- [Spring Boot JPA](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.jpa-and-spring-data)

