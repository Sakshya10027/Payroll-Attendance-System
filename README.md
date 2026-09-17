# Employee Payroll & Attendance Management System

A console-based Java application that lets an HR team run core people
operations: registering employees, marking daily attendance, and
generating monthly payslips with automatic, attendance-based deductions.

Built as the "Build Your Own Project" submission for the **Programming in
Java** course.

---

## Overview

Payroll mistakes are expensive and attendance records kept on paper or in
scattered spreadsheets make those mistakes easy to make. This project
models the whole loop as a small, testable Java application with no
external dependencies — just the JDK standard library — so it can be
compiled and run anywhere.

Data is persisted to plain text files under `data/`, so nothing is lost
between runs, and every significant action is written to `logs/app.log`
for traceability.

## Features

- **User Management** — Register as an Employee, or log in as HR
  Admin/Employee. Passwords are hashed with SHA-256 before ever touching
  disk.
- **Employee Management** — HR Admin registers employees with
  department, designation, and base salary; can list all staff.
- **Attendance Management** — HR Admin marks daily attendance
  (Present/Absent/Leave/Half-day) per employee; duplicate entries for
  the same employee/day are rejected.
- **Payroll Processing** — HR Admin generates a monthly payslip per
  employee: base salary is pro-rated against the days actually marked
  present that month, with automatic deduction for absences.
- **Self-Service** — Employees log in to view their own profile,
  attendance history, and payslip history.
- **Persistence & Logging** — All data survives a restart (flat-file
  storage); every action is timestamped and logged.

## Technologies / Tools Used

| Category         | Choice                                   |
|-------------------|-------------------------------------------|
| Language           | Java 21 (standard library only)          |
| Build              | `javac` (no external build tool required) |
| Persistence        | Plain text files (`data/*.txt`)          |
| Security           | `java.security.MessageDigest` (SHA-256)  |
| Version control    | Git / GitHub                             |

## Project Structure

```
PayrollAttendanceSystem/
├── src/main/java/com/payroll/
│   ├── Main.java                       # Console UI / entry point
│   ├── model/
│   │   ├── User.java                   # abstract base
│   │   ├── HRAdmin.java
│   │   ├── Employee.java
│   │   ├── AttendanceStatus.java       # enum
│   │   ├── AttendanceRecord.java
│   │   └── PayrollRecord.java
│   ├── service/
│   │   ├── AuthService.java            # Module 1: User Management
│   │   ├── AttendanceService.java      # Module 2: Attendance
│   │   └── PayrollService.java         # Module 3: Payroll Processing
│   ├── util/
│   │   ├── FileStorage.java
│   │   ├── Logger.java
│   │   └── PasswordUtil.java
│   └── exception/
│       ├── EmployeeNotFoundException.java
│       ├── InvalidCredentialsException.java
│       ├── DuplicateUserException.java
│       ├── AttendanceAlreadyMarkedException.java
│       └── PayrollAlreadyGeneratedException.java
├── diagrams/                           # architecture, workflow, UML diagrams
├── data/                               # created automatically at runtime
├── logs/                               # created automatically at runtime
├── statement.md
└── README.md
```

## Steps to Install & Run

**Prerequisite:** JDK 17 or newer installed (`java -version` to check).

```bash
# 1. Clone the repository
git clone https://github.com/<your-username>/PayrollAttendanceSystem.git
cd PayrollAttendanceSystem

# 2. Compile
find src -name "*.java" > sources.txt
javac -d out @sources.txt

# 3. Run
java -cp out com.payroll.Main
```

On first run the app seeds a default HR Admin account:

```
username: hradmin
password: hr12345
```

Use it to log in and start registering employees, or choose "Register
as Employee" from the welcome menu to create a self-service account.

## Instructions for Testing

Manual test flow (no external test framework needed to exercise it):

1. Start the app, log in as `hradmin` / `hr12345`.
2. Register a new employee (note the Employee ID printed, e.g. `E0001`).
3. Mark attendance for that employee across a few dates — try marking
   the same employee/date twice to confirm
   `AttendanceAlreadyMarkedException` is raised and handled.
4. Generate payroll for that employee for the current month and year;
   confirm the deduction matches (absent days / working days) × base
   salary.
5. Try generating payroll again for the same employee/month to confirm
   `PayrollAlreadyGeneratedException` is raised.
6. Log out, register/log in as that employee, and confirm **View My
   Attendance History** and **View My Payslip History** show the same
   records HR just created.
7. Inspect `data/*.txt` and `logs/app.log` to confirm persistence and
   logging are working.

## Screenshots

See the `diagrams/` folder for the architecture, workflow, use case,
class, sequence, and data-schema diagrams referenced in the project
report.
