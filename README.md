# Інформаційна система реєстрації інцидентів громадського транспорту (Варіант №20)

Курсова робота з дисципліни «Кросплатформене програмування».
Студент: Погрібняк Віталій Андрійович, група 202-ТН.

## Використані технології
- Java 21, Apache Maven
- Сервер: Servlet API 4.0.1 (Apache Tomcat 9.x), JDBC, MySQL Connector/J, Jackson Databind
- Клієнт: JavaFX 21, HTTP/JSON, фоновий CSV-імпорт через `javafx.concurrent.Task`
- Тестування: JUnit 5 (12 модульних тестів)

## Структура проєкту
- `database.sql` — створення БД `public_transport_db`, 4 таблиць та початкових даних
- `server/` — серверний Web-модуль (`packaging: war`)
- `client/` — графічний клієнтський застосунок JavaFX та каталог `sample-data/vehicles.csv`

## Порядок підготовки та запуску
1. Виконати скрипт `database.sql` у MySQL / phpMyAdmin.
2. Запустити модульні тести та зібрати сервер:
   ```bash
   cd server
   mvn clean test
   mvn clean package
   ```
3. Скопіювати `server/target/transport-server.war` у каталог `webapps` сервера Apache Tomcat 9.x та запустити Tomcat.
4. Запустити клієнтський застосунок:
   ```bash
   cd client
   mvn javafx:run
   ```

## Формат CSV-файлу
Файл `client/sample-data/vehicles.csv` (кодування UTF-8, роздільник `;`):
`route_id;board_number;model_name;capacity`
