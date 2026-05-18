CREATE DATABASE IF NOT EXISTS reservams_availability_db;

USE reservams_availability_db;

CREATE TABLE room_availability (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    available_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_room_date UNIQUE (room_id, available_date)
);