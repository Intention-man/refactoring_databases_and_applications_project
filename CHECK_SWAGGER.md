# Проверка работы Swagger OpenAPI

## ✅ Статус проверки

Swagger успешно интегрирован и работает! Проверено:

- ✅ OpenAPI JSON доступен: http://localhost:18123/v3/api-docs
- ✅ Swagger UI доступен: http://localhost:18123/swagger-ui.html
- ✅ Документация содержит все эндпоинты
- ✅ Аннотации работают корректно

## Как проверить вручную

### 1. Откройте Swagger UI в браузере

```
http://localhost:18123/swagger-ui.html
```

Вы должны увидеть:
- Список всех API эндпоинтов
- Группировку по тегам (Authentication, Projects, и т.д.)
- Описания для каждого эндпоинта
- Модели данных (Schemas)

### 2. Проверьте OpenAPI JSON

```bash
curl http://localhost:18123/v3/api-docs | jq '.info'
```

Должен вернуться:
```json
{
  "title": "Space Station Management API",
  "description": "API для управления космическими станциями...",
  "version": "1.0.0"
}
```

### 3. Протестируйте эндпоинт через Swagger UI

1. Найдите эндпоинт `/api/auth/register` в разделе **Authentication**
2. Нажмите "Try it out"
3. Заполните Request body:
```json
{
  "username": "testuser",
  "password": "testpass123",
  "role": "MANAGER",
  "contactInformation": "test@example.com"
}
```
4. Нажмите "Execute"
5. Проверьте ответ - должен вернуться JWT токен

### 4. Проверьте аутентификацию

1. Выполните `/api/auth/login` с существующим пользователем
2. Скопируйте полученный токен
3. Нажмите кнопку **"Authorize"** (вверху справа в Swagger UI)
4. Введите: `Bearer {ваш-токен}`
5. Нажмите "Authorize"
6. Теперь попробуйте вызвать защищенный эндпоинт `/api/projects` - он должен работать

### 5. Проверьте документацию моделей

В Swagger UI перейдите в раздел **Schemas** и проверьте:
- `AuthenticationRequest` - должно быть описание полей
- `AuthenticationResponse` - должно быть описание токена и роли
- `ProjectDTO` - должно быть описание всех полей с примерами
- `ErrorResponse` - должно быть описание структуры ошибок

## Проверка через curl

### Проверка OpenAPI JSON:
```bash
curl http://localhost:18123/v3/api-docs | jq '.tags'
```

### Проверка Swagger UI:
```bash
curl -I http://localhost:18123/swagger-ui.html
# Должен вернуть 200 или 302 (редирект)
```

## Что должно быть видно в Swagger UI

### Теги (Tags):
- **Authentication** - с описанием "API для аутентификации и регистрации пользователей"
- **Projects** - с описанием "API для управления проектами"

### Эндпоинты с описаниями:
- `/api/auth/register` - "Регистрация нового пользователя"
- `/api/auth/login` - "Аутентификация пользователя"
- `/api/projects` - "Получить все проекты"
- `/api/projects/{id}` - "Получить проект по ID"
- И другие...

### Модели с описаниями:
- `ProjectDTO` - "DTO для проекта космической станции"
- `AuthenticationRequest` - "Запрос на аутентификацию пользователя"
- `ErrorResponse` - "Стандартизированный ответ об ошибке"

## Возможные проблемы

### Swagger UI не открывается
- Проверьте, что приложение запущено: `lsof -i :18123`
- Проверьте логи приложения на наличие ошибок

### Эндпоинты не видны
- Убедитесь, что контроллеры имеют аннотации `@RestController`
- Проверьте, что пути начинаются с `/api/`

### Нет описаний
- Убедитесь, что добавлены аннотации `@Operation` и `@ApiResponses`
- Проверьте, что DTO имеют аннотации `@Schema`

## Дополнительная проверка

Проверьте, что в OpenAPI JSON есть:
- Информация об API (title, description, version)
- Список всех эндпоинтов
- Описания для эндпоинтов с аннотациями
- Схемы безопасности (Bearer Authentication)
- Модели данных с описаниями

```bash
# Проверка наличия описаний
curl -s http://localhost:18123/v3/api-docs | jq '.paths."/api/projects".get.summary'
# Должно вернуть: "Получить все проекты"
```



