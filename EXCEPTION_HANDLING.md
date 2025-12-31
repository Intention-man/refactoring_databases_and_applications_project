# Глобальный обработчик ошибок

## Обзор

В проект внедрен централизованный глобальный обработчик исключений (`GlobalExceptionHandler`), который стандартизирует все ответы об ошибках и упрощает управление исключениями во всем приложении.

## Структура

### 1. DTO для ответов об ошибках

**`ErrorResponse`** - стандартизированный формат ответа об ошибке:
- `timestamp` - время возникновения ошибки
- `status` - HTTP статус код
- `error` - тип ошибки
- `message` - сообщение об ошибке
- `path` - путь запроса
- `validationErrors` - список ошибок валидации (опционально)

### 2. Кастомные исключения

- **`ResourceNotFoundException`** - ресурс не найден (404)
- **`ResourceAlreadyExistsException`** - ресурс уже существует (409)
- **`ValidationException`** - ошибка валидации (400)
- **`BusinessLogicException`** - ошибка бизнес-логики (400)

### 3. Глобальный обработчик

**`GlobalExceptionHandler`** обрабатывает следующие типы исключений:

#### Бизнес-логика
- `ResourceNotFoundException` → 404 Not Found
- `ResourceAlreadyExistsException` → 409 Conflict
- `ValidationException` → 400 Bad Request
- `BusinessLogicException` → 400 Bad Request

#### Валидация
- `MethodArgumentNotValidException` → 400 Bad Request (с деталями полей)
- `ConstraintViolationException` → 400 Bad Request (с деталями полей)
- `MethodArgumentTypeMismatchException` → 400 Bad Request

#### Безопасность
- `UsernameNotFoundException` → 404 Not Found
- `BadCredentialsException` → 401 Unauthorized
- `AuthenticationException` → 401 Unauthorized
- `AccessDeniedException` → 403 Forbidden

#### Общие
- `IllegalArgumentException` → 400 Bad Request
- `NoHandlerFoundException` → 404 Not Found
- `Exception` → 500 Internal Server Error (fallback)

## Использование

### В сервисах

```java
// Вместо возврата null или Optional.empty()
public ProjectDTO findById(Long id) {
    return projectRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project", id));
}

// Проверка на существование
public void save(ProjectDTO dto) {
    if (existsByName(dto.getName())) {
        throw new ResourceAlreadyExistsException("Project", dto.getName());
    }
    // ...
}
```

### В контроллерах

```java
// Убрать try-catch блоки - обработка автоматическая
@GetMapping("/{id}")
public ResponseEntity<ProjectDTO> getById(@PathVariable Long id) {
    ProjectDTO project = projectService.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    return ResponseEntity.ok(project);
}

// Или еще проще - если сервис уже выбрасывает исключение
@PatchMapping("/{id}")
public ResponseEntity<ProjectDTO> update(@PathVariable Long id, @RequestBody ProjectDTO dto) {
    ProjectDTO updated = projectService.partialUpdate(id, dto);
    return ResponseEntity.ok(updated);
}
```

## Примеры ответов

### Ресурс не найден (404)
```json
{
  "timestamp": "2025-12-31T13:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Project with id 123 not found",
  "path": "/api/projects/123"
}
```

### Ошибка валидации (400)
```json
{
  "timestamp": "2025-12-31T13:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Validation failed for request body",
  "path": "/api/projects",
  "validationErrors": [
    {
      "field": "name",
      "message": "Name is required",
      "rejectedValue": null
    }
  ]
}
```

### Ресурс уже существует (409)
```json
{
  "timestamp": "2025-12-31T13:30:00",
  "status": 409,
  "error": "Resource Already Exists",
  "message": "User with identifier 'admin' already exists",
  "path": "/api/auth/register"
}
```

## Преимущества

1. **Централизация** - вся обработка ошибок в одном месте
2. **Стандартизация** - единый формат ответов
3. **Упрощение кода** - не нужно обрабатывать исключения в каждом контроллере
4. **Логирование** - автоматическое логирование всех ошибок
5. **Расширяемость** - легко добавить обработку новых типов исключений

## Миграция существующего кода

### До рефакторинга:
```java
@GetMapping("/{id}")
public ResponseEntity<ProjectDTO> getById(@PathVariable Long id) {
    return projectService.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
}

@PatchMapping("/{id}")
public ResponseEntity<ProjectDTO> update(@PathVariable Long id, @RequestBody ProjectDTO dto) {
    try {
        ProjectDTO updated = projectService.partialUpdate(id, dto);
        return ResponseEntity.ok(updated);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
```

### После рефакторинга:
```java
@GetMapping("/{id}")
public ResponseEntity<ProjectDTO> getById(@PathVariable Long id) {
    ProjectDTO project = projectService.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    return ResponseEntity.ok(project);
}

@PatchMapping("/{id}")
public ResponseEntity<ProjectDTO> update(@PathVariable Long id, @RequestBody ProjectDTO dto) {
    ProjectDTO updated = projectService.partialUpdate(id, dto);
    return ResponseEntity.ok(updated);
}
```

## Обновленные компоненты

- ✅ `ProjectService` и `ProjectController`
- ✅ `SpaceStationService` и `SpaceStationController`
- ✅ `ResourceService` и `ResourceController`
- ✅ `AuthenticationService` и `AuthController`

Остальные сервисы и контроллеры можно обновить по аналогии.

