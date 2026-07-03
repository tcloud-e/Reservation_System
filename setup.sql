CREATE DATABASE IF NOT EXISTS classroom_reservation_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'classroom_user'@'localhost' IDENTIFIED BY 'classroom_password';
GRANT ALL PRIVILEGES ON classroom_reservation_db.* TO 'classroom_user'@'localhost';
FLUSH PRIVILEGES;
