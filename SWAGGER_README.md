# Swagger OpenAPI Documentation

## Обзор

В проект интегрирован Swagger OpenAPI для автоматической генерации документации API и интерактивного тестирования эндпоинтов.

## Доступ к документации

После запуска приложения документация доступна по следующим адресам:

- **Swagger UI**: http://localhost:18123/swagger-ui.html
- **OpenAPI JSON**: http://localhost:18123/v3/api-docs
- **OpenAPI YAML**: http://localhost:18123/v3/api-docs.yaml

## Использование

### 1. Просмотр документации

Откройте Swagger UI в браузере для интерактивного просмотра всех доступных эндпоинтов.

### 2. Тестирование API

1. Откройте Swagger UI
2. Найдите нужный эндпоинт
3. Нажмите "Try it out"
4. Заполните параметры запроса
5. Нажмите "Execute" для отправки запроса

### 3. Аутентификация

Для доступа к защищенным эндпоинтам:

1. Выполните запрос на `/api/auth/login` или `/api/auth/register`
2. Скопируйте полученный JWT токен
3. В Swagger UI нажмите кнопку "Authorize" (вверху справа)
4. Введите токен в формате: `Bearer {your-token}`
5. Нажмите "Authorize"

Теперь все защищенные эндпоинты будут использовать этот токен для аутентификации.

## Структура документации

### Теги (Tags)

API организованы по следующим тегам:

- **Authentication** - Аутентификация и регистрация
- **Projects** - Управление проектами
- И другие теги для каждого контроллера

### Модели данных

В разделе "Schemas" доступны все DTO и модели данных:

- `AuthenticationRequest` - Запрос на аутентификацию
- `AuthenticationResponse` - Ответ с JWT токеном
- `ProjectDTO` - Данные проекта
- `ErrorResponse` - Стандартизированный ответ об ошибке
- И другие модели

## Настройка

### Конфигурация OpenAPI

Основная конфигурация находится в `OpenApiConfig.java`:

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        // Настройки API
    }
}
```

### Настройки в application.yml

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operations-sorter: method
    tags-sorter: alpha
```

## Аннотации

### Контроллеры

Используются следующие аннотации:

- `@Tag` - Группировка эндпоинтов по категориям
- `@Operation` - Описание операции
- `@ApiResponses` - Описание возможных ответов
- `@SecurityRequirement` - Требования безопасности

### DTO

Используются следующие аннотации:

- `@Schema` - Описание модели данных
- `@Schema(description = "...")` - Описание поля
- `@Schema(example = "...")` - Пример значения

## Примеры

### Пример контроллера с аннотациями

```java
@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "API для управления проектами")
@SecurityRequirement(name = "Bearer Authentication")
public class ProjectController {
    
    @Operation(summary = "Создать новый проект")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Проект создан"),
        @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        // ...
    }
}
```

### Пример DTO с аннотациями

```java
@Schema(description = "DTO для проекта")
public class ProjectDTO {
    @Schema(description = "Название проекта", example = "Mars Habitat", required = true)
    private String name;
}
```

## Безопасность

Swagger UI доступен без аутентификации для удобства разработки. В production рекомендуется:

1. Отключить Swagger UI
2. Ограничить доступ к `/v3/api-docs`
3. Использовать отдельный профиль для документации

## Расширение документации

Для добавления документации к новым эндпоинтам:

1. Добавьте `@Tag` к контроллеру
2. Добавьте `@Operation` к каждому методу
3. Добавьте `@ApiResponses` для описания ответов
4. Добавьте `@Schema` к DTO и полям

## Полезные ссылки

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)



