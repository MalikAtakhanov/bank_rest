# Bank REST API

REST API для банковских операций. Проект предоставляет базовый функционал для управления счетами, проведения транзакций и работы с клиентами банка.

## Основные возможности

- Создание и управление банковскими картами
- Переводы между картами
- Управление данными клиентов

## Технологии

- Spring Boot
- Spring Data JPA
- PostgreSQL
- Spring Security для аутентификации
- JWT токены
- Maven

## Требования

- Java 17 или выше
- PostgreSQL
- Maven 3.6+

## Установка и запуск

1. Клонируйте репозиторий:
```
   git clone https://github.com/MalikAtakhanov/bank_rest.git
   cd bank_rest
```
2. Создайте базу данных PostgreSQL:
```
CREATE DATABASE bank_db;
```
3. Настройте подключение к БД в application.properties:
```
   spring.datasource.url=jdbc:postgresql://localhost:5432/bank_db
   spring.datasource.username=ваш_пользователь
   spring.datasource.password=ваш_пароль
```

4. Соберите проект:
```
   mvn clean install
```
5. Запустите приложение:
```
   mvn spring-boot:run
```
Сервер запустится на http://localhost:8080

## API Endpoints

### Авторизация
- POST /api/auth/login

### Пользователи
- GET /api/admin/users - список всех пользователей
- POST /api/admin/users - добавление нового пользователя
- DELETE /api/admin/users/{id} - удалить пользователя

### Транзакции
- POST /api/cards/transfer - перевод между своими картами
### Карты
- POST /api/cards - создание новой карты
- GET /api/cards/my - список всех карт пользователя
- GET /api/cards/admin/all - список всех карт
- DELETE /api/cards/{id} - удаление карты по ID
- PATCH /api/cards/{id}/block - блокирование карты
- PATCH /api/cards/{id}/activate - активация карты

## Структура проекта
```bash
src/
├── main/
│   ├── java/
│   │   └── com/example/bankcards
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── entity/enums
│   │       ├── exception/
│   │       ├── repository/
│   │       ├── security/
│   │       ├── service/
│   │       └── util/
│   └── resources/
│       ├── db/
│       │   └── migration/
│       └── application.yml
└── test/
```


## Безопасность

API использует JWT токены для аутентификации. После успешного входа клиент получает токен, который необходимо передавать в заголовке Authorization для доступа к защищенным endpoints.

## Лицензия

MIT