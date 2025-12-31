# Docker Setup для проекта

Этот проект контейнеризирован с помощью Docker и Docker Compose.

## Требования

- Docker (версия 20.10 или выше)
- Docker Compose (версия 2.0 или выше)

## Быстрый старт

1. **Остановите текущее приложение** (если запущено):
   ```bash
   # Найдите процесс и остановите его
   pkill -f "spring-boot:run"
   ```

2. **Запустите приложение с Docker Compose**:
   ```bash
   docker-compose up --build
   ```

   Или в фоновом режиме:
   ```bash
   docker-compose up -d --build
   ```

3. **Проверьте статус контейнеров**:
   ```bash
   docker-compose ps
   ```

4. **Просмотрите логи**:
   ```bash
   # Все сервисы
   docker-compose logs -f
   
   # Только приложение
   docker-compose logs -f app
   
   # Только база данных
   docker-compose logs -f postgres
   ```

## Остановка

```bash
docker-compose down
```

Для удаления всех данных (включая volumes):
```bash
docker-compose down -v
```

## Переменные окружения

Вы можете настроить переменные окружения, создав файл `.env` в корне проекта:

```env
POSTGRES_DB=prac_db
POSTGRES_USER=prac_user
POSTGRES_PASSWORD=prac_password
POSTGRES_PORT=5432
APP_PORT=18123
```

## Доступ к приложению

После запуска приложение будет доступно по адресу:
- **API**: http://localhost:18123
- **PostgreSQL**: localhost:5432

## Структура Docker

- **Dockerfile**: Multi-stage build для оптимизации размера образа
- **docker-compose.yml**: Оркестрация приложения и PostgreSQL
- **application-docker.yml**: Конфигурация Spring Boot для Docker окружения

## Полезные команды

```bash
# Пересобрать образы
docker-compose build --no-cache

# Выполнить команду в контейнере приложения
docker-compose exec app sh

# Выполнить команду в контейнере PostgreSQL
docker-compose exec postgres psql -U prac_user -d prac_db

# Просмотреть использование ресурсов
docker stats

# Очистить неиспользуемые ресурсы Docker
docker system prune -a
```

## Инициализация базы данных

База данных автоматически инициализируется при первом запуске:
1. Создаются таблицы из `db/ddl.sql`
2. Загружаются тестовые данные из `db/insert-test-data.sql`

## Troubleshooting

### Порт уже занят
Если порт 18123 или 5432 уже занят, измените их в `.env` файле или `docker-compose.yml`.

### Проблемы с подключением к БД
Убедитесь, что контейнер PostgreSQL запущен и здоров:
```bash
docker-compose ps
docker-compose logs postgres
```

### Пересоздание базы данных
Если нужно полностью пересоздать БД:
```bash
docker-compose down -v
docker-compose up -d
```



