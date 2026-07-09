CREATE DATABASE IF NOT EXISTS java_user_db;
CREATE DATABASE IF NOT EXISTS metro_database;

USE java_user_db;

CREATE TABLE IF NOT EXISTS java_users_database (
    id INT AUTO_INCREMENT PRIMARY KEY,
    Fname VARCHAR(100) NOT NULL,
    Lname VARCHAR(100) NOT NULL,
    Username VARCHAR(100) NOT NULL UNIQUE,
    Email VARCHAR(255) NOT NULL,
    Pass VARCHAR(255) NOT NULL
);

INSERT INTO java_users_database (Fname, Lname, Username, Email, Pass)
VALUES ('System', 'Admin', 'Admin', 'admin@metro.local', '123')
ON DUPLICATE KEY UPDATE Pass = VALUES(Pass);

USE metro_database;

CREATE TABLE IF NOT EXISTS metro_details (
    metro_no VARCHAR(50) PRIMARY KEY,
    m_name VARCHAR(100) NOT NULL,
    m_lineno VARCHAR(50) NOT NULL,
    m_dstation VARCHAR(100) NOT NULL,
    m_astation VARCHAR(100) NOT NULL,
    m_price DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    dstation VARCHAR(100) NOT NULL,
    mstation VARCHAR(100) NOT NULL,
    mnumber VARCHAR(50) NOT NULL,
    mname VARCHAR(100) NOT NULL,
    mprice DECIMAL(10, 2) NOT NULL,
    date DATE NOT NULL,
    tickets INT NOT NULL
);

INSERT INTO metro_details (metro_no, m_name, m_lineno, m_dstation, m_astation, m_price)
VALUES
    ('M001', 'Tunis Metro Line 1', '1', 'Tunis Marine', 'Ben Arous', 2.00),
    ('M002', 'Tunis Metro Line 2', '2', 'Place Barcelone', 'Ariana', 2.50)
ON DUPLICATE KEY UPDATE
    m_name = VALUES(m_name),
    m_lineno = VALUES(m_lineno),
    m_dstation = VALUES(m_dstation),
    m_astation = VALUES(m_astation),
    m_price = VALUES(m_price);
