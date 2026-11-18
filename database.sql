DROP DATABASE IF EXISTS public_transport_db;
CREATE DATABASE public_transport_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE public_transport_db;

CREATE TABLE route (
    id INT AUTO_INCREMENT PRIMARY KEY,
    route_code VARCHAR(25) NOT NULL UNIQUE,
    route_name VARCHAR(160) NOT NULL,
    importance_score DOUBLE NOT NULL
);

CREATE TABLE vehicle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    route_id INT NOT NULL,
    board_number VARCHAR(40) NOT NULL,
    model_name VARCHAR(80) NOT NULL,
    capacity INT NOT NULL,
    CONSTRAINT fk_vehicle_route FOREIGN KEY (route_id)
        REFERENCES route(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE incident (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    affected_passengers INT NOT NULL,
    duration_score DOUBLE NOT NULL,
    state VARCHAR(25) NOT NULL,
    f1 DOUBLE NOT NULL,
    f2 DOUBLE NOT NULL,
    f3 DOUBLE NOT NULL,
    integral_index DOUBLE NOT NULL,
    CONSTRAINT fk_incident_vehicle FOREIGN KEY (vehicle_id)
        REFERENCES vehicle(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE response_task (
    id INT AUTO_INCREMENT PRIMARY KEY,
    incident_id INT NOT NULL,
    assigned_unit VARCHAR(100) NOT NULL,
    action_plan VARCHAR(255) NOT NULL,
    deadline_date VARCHAR(30) NOT NULL,
    CONSTRAINT fk_task_incident FOREIGN KEY (incident_id)
        REFERENCES incident(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO route (route_code, route_name, importance_score) VALUES
('ТР-15', 'Кільцевий (Центр — Половки — Алмазний)', 80.0),
('А-21', 'Південний вокзал — Інститут зв''язку', 60.0);

INSERT INTO vehicle (route_id, board_number, model_name, capacity) VALUES
(1, 'ТП-104', 'Богдан Т70117', 100),
(2, 'АТ-208', 'Ataman A092H6', 50);

INSERT INTO incident (vehicle_id, description, affected_passengers, duration_score, state, f1, f2, f3, integral_index) VALUES
(1, 'Пошкодження струмоприймача на перехресті', 65, 50.0, 'REPORTED', 65.0, 80.0, 50.0, 66.50),
(2, 'Технічна несправність гальмівної системи', 20, 40.0, 'CONFIRMED', 40.0, 60.0, 40.0, 47.00);

INSERT INTO response_task (incident_id, assigned_unit, action_plan, deadline_date) VALUES
(1, 'Аварійна бригада КМ №2', 'Заміна контактної вставки та відновлення руху', '2026-11-20'),
(2, 'Ремзона депо №1', 'Евакуація автобуса тягачем та випуск резерву', '2026-11-21');
