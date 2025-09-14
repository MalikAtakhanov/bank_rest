# Bank REST API

REST API для банковских операций. Проект предоставляет базовый функционал для управления счетами, проведения транзакций и работы с клиентами банка.

## Основные возможности

- Создание и управление банковскими счетами
- Переводы между счетами
- История транзакций
- Управление данными клиентов
- Проверка баланса
- Депозиты и снятие средств

## Технологии

- Spring Boot
- Spring Data JPA
- PostgreSQL
- Spring Security для аутентификации
- JWT токены
- Maven

## Требования

- Java 11 или выше
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

### Счета
- GET /api/accounts - список всех счетов
- GET /api/accounts/{id} - информация о счете
- POST /api/accounts - создать новый счет
- DELETE /api/accounts/{id} - закрыть счет

### Транзакции
- POST /api/transactions/transfer - перевод между счетами
- POST /api/transactions/deposit - пополнение счета
- POST /api/transactions/withdraw - снятие средств
- GET /api/transactions/history/{accountId} - история операций

### Клиенты
- GET /api/customers - список клиентов
- POST /api/customers - регистрация клиента
- GET /api/customers/{id} - данные клиента

## Структура проекта
```bash
src/
├── main/
│   ├── java/
│   │   └── com/bank/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── model/
│   │       ├── dto/
│   │       └── config/
│   └── resources/
│       └── application.properties
└── test/
```

## Тестирование

Запуск тестов:
```
mvn test
```
## Безопасность

API использует JWT токены для аутентификации. После успешного входа клиент получает токен, который необходимо передавать в заголовке Authorization для доступа к защищенным endpoints.

## Лицензия

MIT