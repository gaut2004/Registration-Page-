CREATE DATABASE RegistrationDB;
USE RegistrationDB;
CREATE TABLE RegistrationDetails (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(15),
    gender VARCHAR(10),
    city VARCHAR(50),
    state VARCHAR(50),
    description TEXT
);