# Тестирование

## Обзор

Создан набор тестов для проверки работы глобального обработчика ошибок и обработки исключений в приложении.

## Структура тестов

### 1. GlobalExceptionHandlerTest
**Расположение:** `src/test/java/com/example/prac/exception/GlobalExceptionHandlerTest.java`

Тесты для проверки работы глобального обработчика исключений:

- ✅ `testHandleResourceNotFoundException` - обработка 404 ошибок
- ✅ `testHandleResourceAlreadyExistsException` - обработка 409 ошибок
- ✅ `testHandleValidationException` - обработка ошибок валидации
- ✅ `testHandleBadCredentialsException` - обработка 401 ошибок
- ✅ `testHandleUsernameNotFoundException` - обработка отсутствующих пользователей
- ✅ `testHandleAccessDeniedException` - обработка 403 ошибок
- ✅ `testHandleIllegalArgumentException` - обработка неверных аргументов
- ✅ `testHandleGenericException` - обработка общих исключений (500)
- ✅ `testHandleBusinessLogicException` - обработка ошибок бизнес-логики

### 2. ExceptionHandlingIntegrationTest
**Расположение:** `src/test/java/com/example/prac/integration/ExceptionHandlingIntegrationTest.java`

Интеграционные тесты для проверки форматирования сообщений исключений:

- ✅ `testResourceNotFoundException_MessageFormat` - проверка формата сообщений ResourceNotFoundException
- ✅ `testResourceAlreadyExistsException_MessageFormat` - проверка формата сообщений ResourceAlreadyExistsException

### 3. Controller Exception Tests
**Расположение:** `src/test/java/com/example/prac/controller/`

Тесты для контроллеров с проверкой обработки ошибок:

- `ProjectControllerExceptionTest` - тесты для ProjectController
- `AuthControllerExceptionTest` - тесты для AuthController
- `SpaceStationControllerExceptionTest` - тесты для SpaceStationController

## Запуск тестов

### Все тесты
```bash
mvn test
```

### Конкретный тест
```bash
mvn test -Dtest=GlobalExceptionHandlerTest
```

### Несколько тестов
```bash
mvn test -Dtest=GlobalExceptionHandlerTest,ExceptionHandlingIntegrationTest
```

## Результаты

### Успешные тесты
- ✅ GlobalExceptionHandlerTest - 9 тестов, все прошли
- ✅ ExceptionHandlingIntegrationTest - 2 теста, все прошли

**Итого:** 11 тестов, все успешно пройдены

## Проверяемые сценарии

### 1. Обработка ошибок "Ресурс не найден" (404)
```java
ResourceNotFoundException ex = new ResourceNotFoundException("Project", 1L);
// Ожидаемый ответ: 404, "Resource Not Found", "Project with id 1 not found"
```

### 2. Обработка ошибок "Ресурс уже существует" (409)
```java
ResourceAlreadyExistsException ex = new ResourceAlreadyExistsException("User", "admin");
// Ожидаемый ответ: 409, "Resource Already Exists", "User with identifier 'admin' already exists"
```

### 3. Обработка ошибок валидации (400)
```java
ValidationException ex = new ValidationException("Field is required");
// Ожидаемый ответ: 400, "Validation Failed", список ошибок валидации
```

### 4. Обработка ошибок аутентификации (401)
```java
BadCredentialsException ex = new BadCredentialsException("Invalid credentials");
// Ожидаемый ответ: 401, "Unauthorized", "Invalid username or password"
```

### 5. Обработка ошибок доступа (403)
```java
AccessDeniedException ex = new AccessDeniedException("Access denied");
// Ожидаемый ответ: 403, "Forbidden", "Access denied"
```

### 6. Обработка общих ошибок (500)
```java
Exception ex = new Exception("Unexpected error");
// Ожидаемый ответ: 500, "Internal Server Error", "An unexpected error occurred"
```

## Формат ответов об ошибках

Все ошибки возвращаются в стандартизированном формате:

```json
{
  "timestamp": "2025-12-31T15:10:23",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Project with id 1 not found",
  "path": "/api/projects/1"
}
```

Для ошибок валидации добавляется поле `validationErrors`:

```json
{
  "timestamp": "2025-12-31T15:10:23",
  "status": 400,
  "error": "Validation Failed",
  "message": "Validation failed",
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

## Конфигурация тестов

Тесты используют тестовый профиль `application-test.yml` с H2 in-memory базой данных для изоляции от основной базы данных.

## Расширение тестов

Для добавления новых тестов:

1. Создайте тест класс в соответствующей директории
2. Используйте `@WebMvcTest` для тестов контроллеров
3. Используйте `@ExtendWith(MockitoExtension.class)` для unit-тестов
4. Используйте `@SpringBootTest` для интеграционных тестов

## Примеры

### Unit-тест обработчика
```java
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    @InjectMocks
    private GlobalExceptionHandler handler;
    
    @Test
    void testHandleException() {
        // тест
    }
}
```

### Тест контроллера
```java
@WebMvcTest(ProjectController.class)
class ProjectControllerExceptionTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProjectService projectService;
    
    @Test
    @WithMockUser(roles = "MANAGER")
    void testGetById_NotFound() throws Exception {
        // тест
    }
}
```

