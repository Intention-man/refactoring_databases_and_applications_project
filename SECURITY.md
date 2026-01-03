# Безопасность и конфигурация

## Обзор

Проект использует современные практики безопасности для защиты данных и секретов.

## Хеширование паролей

### BCrypt

Проект использует **BCrypt** для хеширования паролей пользователей. BCrypt - это адаптивная криптографическая хеш-функция, специально разработанная для хеширования паролей.

**Преимущества BCrypt:**
- Адаптивный алгоритм (можно увеличивать сложность)
- Встроенная соль (salt) для каждого пароля
- Защита от атак перебора (brute-force)
- Стандарт индустрии для хранения паролей

**Реализация:**
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

## Переменные окружения

Все секретные данные должны храниться в переменных окружения, а не в коде.

### Обязательные переменные окружения

#### База данных
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
```

#### JWT
```bash
JWT_SECRET=your_base64_encoded_secret_key_min_256_bits
```

### Настройка переменных окружения

#### Linux/macOS
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password
export JWT_SECRET=your_secret_key
```

#### Windows (PowerShell)
```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/postgres"
$env:SPRING_DATASOURCE_USERNAME="your_username"
$env:SPRING_DATASOURCE_PASSWORD="your_password"
$env:JWT_SECRET="your_secret_key"
```

#### Использование .env файла

Создайте файл `.env` в корне проекта:
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=your_secret_key
```

Затем загрузите переменные перед запуском:
```bash
export $(cat .env | xargs)
mvn spring-boot:run
```

**Важно:** Добавьте `.env` в `.gitignore`, чтобы не коммитить секреты!

### Генерация JWT секрета

Для генерации безопасного JWT секрета используйте:

```bash
# Генерация 256-битного ключа в Base64
openssl rand -base64 32
```

Или используйте онлайн-генератор:
- Минимум 256 бит (32 байта)
- Должен быть закодирован в Base64

### Docker

При использовании Docker Compose, переменные окружения можно задать в `docker-compose.yml`:

```yaml
services:
  app:
    environment:
      SPRING_DATASOURCE_URL: ${SPRING_DATASOURCE_URL}
      SPRING_DATASOURCE_USERNAME: ${SPRING_DATASOURCE_USERNAME}
      SPRING_DATASOURCE_PASSWORD: ${SPRING_DATASOURCE_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
```

Или используйте `.env` файл рядом с `docker-compose.yml`.

## Миграция с MD5 на BCrypt

Если в базе данных уже есть пароли, захешированные с помощью MD5, необходимо:

1. **Сбросить все пароли** (рекомендуется для разработки)
2. **Или создать миграцию** для перехеширования паролей при первом входе

Пример миграции:
```java
// При аутентификации проверяем старый формат
if (isMD5Hash(storedPassword)) {
    // Если пароль совпадает с MD5, перехешируем в BCrypt
    if (md5Matches(rawPassword, storedPassword)) {
        String newHash = passwordEncoder.encode(rawPassword);
        user.setPassword(newHash);
        userRepository.save(user);
    }
}
```

## Рекомендации по безопасности

1. **Никогда не коммитьте секреты в Git**
   - Используйте `.gitignore` для `.env` файлов
   - Используйте переменные окружения в CI/CD

2. **Используйте сильные секреты**
   - JWT секрет должен быть минимум 256 бит
   - Пароли БД должны быть сложными

3. **Регулярно ротируйте секреты**
   - Меняйте JWT секрет периодически
   - Обновляйте пароли БД

4. **Используйте разные секреты для разных окружений**
   - Разработка (development)
   - Тестирование (testing)
   - Продакшн (production)

5. **Ограничьте доступ к переменным окружения**
   - Используйте секреты в CI/CD системах
   - Ограничьте доступ к серверам

## Проверка конфигурации

Убедитесь, что все переменные окружения установлены:

```bash
echo $SPRING_DATASOURCE_URL
echo $SPRING_DATASOURCE_USERNAME
echo $JWT_SECRET
```

Если переменные не установлены, приложение будет использовать значения по умолчанию из `application.yml` (не рекомендуется для продакшна).

## Troubleshooting

### Ошибка: "Invalid JWT signature"
- Проверьте, что `JWT_SECRET` установлен и совпадает на всех серверах
- Убедитесь, что секрет правильно закодирован в Base64

### Ошибка: "Password does not match"
- Если мигрируете с MD5, убедитесь, что все пароли перехешированы
- Проверьте, что используется правильный `PasswordEncoder`

### Ошибка подключения к БД
- Проверьте переменные `SPRING_DATASOURCE_*`
- Убедитесь, что БД доступна и учетные данные верны

