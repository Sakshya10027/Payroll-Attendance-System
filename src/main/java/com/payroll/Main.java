package com.payroll;

import com.payroll.exception.AttendanceAlreadyMarkedException;
import com.payroll.exception.DuplicateUserException;
import com.payroll.exception.InvalidCredentialsException;
import com.payroll.exception.PayrollAlreadyGeneratedException;
import com.payroll.model.AttendanceRecord;
import com.payroll.model.AttendanceStatus;
import com.payroll.model.Employee;
import com.payroll.model.PayrollRecord;
import com.payroll.model.User;
import com.payroll.service.AttendanceService;
import com.payroll.service.AuthService;
import com.payroll.service.PayrollService;
import com.payroll.util.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AuthService authService = new AuthService();
    private static final AttendanceService attendanceService = new AttendanceService();
    private static final PayrollService payrollService = new PayrollService(attendanceService);

    public static void main(String[] args) {
        Logger.info("=== Employee Payroll & Attendance Management System starting up ===");
        printBanner();

        boolean running = true;
        while (running) {
            System.out.println("\n===== WELCOME =====");
            System.out.println("1. Login");
            System.out.println("2. Register as Employee");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1": handleLogin(); break;
                case "2": handleEmployeeRegister(); break;
                case "3": running = false; break;
                default: System.out.println("Invalid option. Please try again.");
            }
        }

        Logger.info("=== Application shut down cleanly ===");
        System.out.println("Goodbye!");
    }

    private static void printBanner() {
        System.out.println("===================================================");
        System.out.println("   EMPLOYEE PAYROLL & ATTENDANCE MANAGEMENT SYSTEM");
        System.out.println("===================================================");
        System.out.println("Default HR Admin login -> username: hradmin | password: hr12345");
    }



    private static void handleEmployeeRegister() {
        System.out.print("Choose a username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Choose a password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Full name: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Department: ");
        String department = scanner.nextLine().trim();
        System.out.print("Designation: ");
        String designation = scanner.nextLine().trim();
        double baseSalary = readDouble("Base monthly salary: ");

        try {
            Employee emp = authService.registerEmployee(username, password, fullName,
                    department, designation, baseSalary);
            System.out.println("Registration successful! Your Employee ID is " + emp.getEmployeeId()
                    + ". You can now log in.");
        } catch (DuplicateUserException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            User user = authService.login(username, password);
            System.out.println("\nLogin successful. Welcome, " + user.getFullName()
                    + " (" + user.getRole() + ")");
            if ("HRADMIN".equals(user.getRole())) {
                hrAdminMenu(user);
            } else {
                employeeMenu((Employee) user);
            }
        } catch (InvalidCredentialsException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }


    private static void hrAdminMenu(User admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n----- HR ADMIN MENU (" + admin.getUsername() + ") -----");
            System.out.println("1. Register New Employee");
            System.out.println("2. View All Employees");
            System.out.println("3. Mark Attendance");
            System.out.println("4. View Attendance Records (by Employee)");
            System.out.println("5. Generate Payroll for an Employee");
            System.out.println("6. View All Payroll Records");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1": handleEmployeeRegister(); break;
                case "2": viewAllEmployees(); break;
                case "3": markAttendanceFlow(); break;
                case "4": viewAttendanceForEmployeeFlow(); break;
                case "5": generatePayrollFlow(); break;
                case "6": viewAllPayrollRecords(); break;
                case "7": loggedIn = false; break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void viewAllEmployees() {
        List<Employee> employees = authService.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees registered yet.");
            return;
        }
        employees.forEach(e -> System.out.println(e.toString()));
    }

    private static void markAttendanceFlow() {
        System.out.print("Enter Employee ID: ");
        String employeeId = scanner.nextLine().trim();
        Employee emp = authService.findEmployeeById(employeeId);
        if (emp == null) {
            System.out.println("No employee found with ID: " + employeeId);
            return;
        }
        System.out.print("Enter date (YYYY-MM-DD), or leave blank for today: ");
        String dateInput = scanner.nextLine().trim();
        LocalDate date = dateInput.isEmpty() ? LocalDate.now() : LocalDate.parse(dateInput);

        System.out.println("Status: 1=PRESENT 2=ABSENT 3=LEAVE 4=HALF_DAY");
        String statusChoice = scanner.nextLine().trim();
        AttendanceStatus status;
        switch (statusChoice) {
            case "1": status = AttendanceStatus.PRESENT; break;
            case "2": status = AttendanceStatus.ABSENT; break;
            case "3": status = AttendanceStatus.LEAVE; break;
            case "4": status = AttendanceStatus.HALF_DAY; break;
            default:
                System.out.println("Invalid status choice.");
                return;
        }

        try {
            AttendanceRecord record = attendanceService.markAttendance(employeeId, date, status);
            System.out.println("Attendance marked: " + record.toString());
        } catch (AttendanceAlreadyMarkedException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAttendanceForEmployeeFlow() {
        System.out.print("Enter Employee ID: ");
        String employeeId = scanner.nextLine().trim();
        List<AttendanceRecord> records = attendanceService.getRecordsForEmployee(employeeId);
        if (records.isEmpty()) {
            System.out.println("No attendance records found for this employee.");
            return;
        }
        records.forEach(r -> System.out.println(r.toString()));
    }

    private static void generatePayrollFlow() {
        System.out.print("Enter Employee ID: ");
        String employeeId = scanner.nextLine().trim();
        Employee emp = authService.findEmployeeById(employeeId);
        if (emp == null) {
            System.out.println("No employee found with ID: " + employeeId);
            return;
        }
        int month = (int) readDouble("Month (1-12): ");
        int year = (int) readDouble("Year (e.g. 2026): ");

        try {
            PayrollRecord record = payrollService.generatePayroll(emp, month, year);
            System.out.println("Payroll generated successfully:");
            System.out.println(record.toString());
        } catch (PayrollAlreadyGeneratedException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAllPayrollRecords() {
        List<PayrollRecord> records = payrollService.getAllPayrollRecords();
        if (records.isEmpty()) {
            System.out.println("No payroll records generated yet.");
            return;
        }
        records.forEach(r -> System.out.println(r.toString()));
    }


    private static void employeeMenu(Employee employee) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n----- EMPLOYEE MENU (" + employee.getEmployeeId() + ") -----");
            System.out.println("1. View My Profile");
            System.out.println("2. View My Attendance History");
            System.out.println("3. View My Payslip History");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1": System.out.println(employee.toString()); break;
                case "2": viewOwnAttendance(employee); break;
                case "3": viewOwnPayslips(employee); break;
                case "4": loggedIn = false; break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void viewOwnAttendance(Employee employee) {
        List<AttendanceRecord> records = attendanceService.getRecordsForEmployee(employee.getEmployeeId());
        if (records.isEmpty()) {
            System.out.println("No attendance records yet.");
            return;
        }
        records.forEach(r -> System.out.println(r.toString()));
    }

    private static void viewOwnPayslips(Employee employee) {
        List<PayrollRecord> records = payrollService.getPayrollHistoryForEmployee(employee.getEmployeeId());
        if (records.isEmpty()) {
            System.out.println("No payslips generated yet.");
            return;
        }
        records.forEach(r -> System.out.println(r.toString()));
    }


    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
