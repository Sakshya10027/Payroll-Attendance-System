# Project Statement

## Problem Statement

Small and mid-sized organizations frequently rely on paper registers or
disconnected spreadsheets to track who came to work and how much each
person should be paid. This creates three recurring problems: attendance
gets recorded inconsistently or not at all, payroll calculations are
done by hand and prone to error, and there is no single place to check
an employee's attendance and pay history together. The **Employee
Payroll & Attendance Management System** solves this by giving HR staff
and employees a single, consistent console application to manage
registration, daily attendance, and monthly payroll generation, with the
pay calculation always derived directly from the attendance records
actually on file.

## Scope of the Project

The project is a self-contained, offline Java console application. In
scope:

- Registering and authenticating two kinds of users: HR Admin and
  Employee (self-service).
- Employee record management: registration with department,
  designation, and base salary; listing all employees.
- Daily attendance marking per employee (Present / Absent / Leave /
  Half-day), with protection against duplicate entries for the same
  employee on the same day.
- Monthly payroll generation per employee: gross pay pro-rated against
  attendance for that month, with automatic deduction for absent days,
  and protection against generating payroll twice for the same period.
- Self-service views: an employee can see their own profile, full
  attendance history, and full payslip history.
- Persisting all data to local files so the system's state survives a
  restart, and logging every significant action for traceability.

Out of scope (explicitly not attempted, to keep the project focused): a
graphical user interface, a networked/multi-user server, integration
with a real relational database, tax-slab-based statutory deductions,
and leave-balance accrual rules all reasonable future enhancements but
not required to demonstrate the core Java concepts this course covers.

## Target Users

- **HR Admin** — registers employees, marks attendance, and generates
  monthly payroll.
- **Employee** — logs in to check their own attendance record and pay
  history without needing to go through HR for every query.

## High-Level Features

1. **User Management Module** — registration, login, SHA-256 password
   hashing, role-based menu (HR Admin vs Employee).
2. **Attendance Management Module** — mark and view daily attendance per
   employee, with duplicate-entry protection.
3. **Payroll Processing Module** — generate a monthly payslip per
   employee from base salary and that month's attendance, with
   duplicate-generation protection and full payroll history.

Together these three modules cover the full lifecycle of a monthly HR
cycle, which is what the course project brief asks for: a meaningful
problem, a designed solution, an implementation using the tools learned
in the course (OOP, collections, exceptions, file I/O), and
documentation to demonstrate understanding.
