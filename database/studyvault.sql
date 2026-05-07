-- StudyVault - Full Database
-- Run this in MySQL Workbench

CREATE DATABASE IF NOT EXISTS studyvault;
USE studyvault;

-- Students
CREATE TABLE IF NOT EXISTS students (
    id       INT          AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(64)  NOT NULL
);

-- Courses
CREATE TABLE IF NOT EXISTS courses (
    id         INT          AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(150) NOT NULL,
    code       VARCHAR(20)  NOT NULL,
    student_id INT          NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- Materials (section = Theory | Practical | Assignments | Projects)
CREATE TABLE IF NOT EXISTS materials (
    id          INT          AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    file_path   VARCHAR(500),
    section     VARCHAR(50)  NOT NULL,
    course_id   INT          NOT NULL,
    student_id  INT          NOT NULL,
    FOREIGN KEY (course_id)  REFERENCES courses(id)  ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- Notes
CREATE TABLE IF NOT EXISTS notes (
    id         INT  AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(200) NOT NULL,
    content    TEXT,
    course_id  INT,
    student_id INT NOT NULL,
    FOREIGN KEY (course_id)  REFERENCES courses(id)  ON DELETE SET NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- To Do List
CREATE TABLE IF NOT EXISTS todos (
    id         INT          AUTO_INCREMENT PRIMARY KEY,
    task       VARCHAR(300) NOT NULL,
    priority   VARCHAR(10)  NOT NULL DEFAULT 'Medium',
    due_date   DATE,
    is_done    BOOLEAN      NOT NULL DEFAULT FALSE,
    student_id INT          NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);