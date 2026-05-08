# StudyVault

A Java desktop application that helps students organize their study materials
by course and section — Theory, Practical, Assignments, and Projects — along
with personal notes and a to-do list, so everything needed for exams is always
in one place.

---

## Student Details

| Full Name    | CMS ID      | Section |
|--------------|-------------|---------|
| Abdul Qadeer | 023-25-0003 | D       |

---

## Problem Statement

Students struggle to find their study materials during exam time. Notes are
scattered across WhatsApp, files are on different drives, and there is no
single organized place to find everything. StudyVault solves this by
organizing all academic resources by course and section in one desktop
application.

---

## Main Modules

```
STUDYVAULT/
├── src/
│   ├── MainApp.java           Entry point — launches the application
│   ├── DBConnection.java      JDBC database connection to MySQL
│   ├── LoginScreen.java       Student login with SHA-256 password check
│   ├── RegisterScreen.java    New student account registration
│   ├── HomeScreen.java        Main dashboard — 3 tiles
│   ├── CoursesScreen.java     Course list with search, add and remove
│   ├── MaterialsScreen.java   Study materials per course in 4 sections
│   ├── NotesScreen.java       Note editor linked to courses with search
│   └── TodoScreen.java        Task list with priority and due dates
├── lib/
│   └── mysql-connector-j-9.6.0.jar
├── bin/                       Compiled .class files (auto-generated)
├── database/
│   └── studyvault.sql
└── run.bat                    One-click compile and run
```

---

## Key Features

- **Study Materials** — organized by course, split into Theory, Practical,
  Assignments and Projects. Files can be opened directly from the app by
  double-clicking any row.
- **Notes** — write and save notes linked to a course. Searchable list
  on the left, full text editor on the right.
- **To Do List** — add tasks with High, Medium or Low priority and an
  optional due date. Mark tasks done or delete them. Colour-coded status.
- **Secure Login** — passwords stored as SHA-256 hash using MySQL's SHA2()
  function. Never stored as plain text.
- **Search** — courses and notes both have a live search bar.
- **File Open** — double-clicking a material row opens the file using the
  system default application via Java Desktop API.

---

## OOP Concepts Applied

| Concept | Where Applied |
|---------|---------------|
| **Classes & Objects** | Every screen is a class. Student, Course, Material, Note, TodoItem are model objects created from database data |
| **Encapsulation** | Model classes have private fields accessed only through public getters and setters |
| **Inheritance** | All screens extend BaseScreen abstract class |
| **Polymorphism** | loadData() and buildTopBar() are abstract in BaseScreen and overridden differently in each screen |
| **Abstract Class** | BaseScreen defines abstract methods every screen must implement |
| **Interface** | Searchable interface implemented by CoursesScreen and NotesScreen |
| **Collections** | ArrayList used in every screen to store model objects loaded from database |
| **Exception Handling** | try-catch on every database operation throughout the app |

---

## Database Design

```
students
   └── courses          (student_id → FK to students)
         └── materials  (course_id  → FK to courses)
                        (section    = Theory | Practical | Assignments | Projects)
   └── notes            (student_id → FK to students)
                        (course_id  → optional FK to courses)
   └── todos            (student_id → FK to students)
```

**5 tables:** students, courses, materials, notes, todos

**Foreign keys** with `ON DELETE CASCADE` — deleting a student removes
all their courses, materials, notes and tasks automatically.

**Passwords** hashed using MySQL `SHA2(password, 256)` — never plain text.

---

## How to Run

### Requirements
- Java JDK 8 or above
- MySQL 8.x installed and running
- `mysql-connector-j-9.6.0.jar` in the `lib/` folder

### Step 1 — Set up the database
Open MySQL Workbench and run:
```
database/studyvault.sql
```

### Step 2 — Set your MySQL password
Open `src/DBConnection.java` and update line 5:
```java
private static final String PASS = "your_password_here";
```
Leave it empty `""` if you have no MySQL password set.

### Step 3 — Compile and run
Double-click `run.bat`

Or manually in terminal from the project root:
```
javac -encoding UTF-8 -cp "lib\mysql-connector-j-9.6.0.jar" -d bin src\*.java
java -cp "bin;lib\mysql-connector-j-9.6.0.jar" MainApp
```

---

## Video Demo

[Watch Demo on Google Drive](https://drive.google.com/file/d/1mt-_8PSw43MHizo6ReyF1dIR_-ZNbaUw/view?usp=sharing)

## GitHub Repository

[github.com/Qadyyr/StudyVault_0](https://github.com/Qadyyr/StudyVault_0)
