package com.payroll.service;

import com.payroll.exception.DuplicateUserException;
import com.payroll.exception.InvalidCredentialsException;
import com.payroll.model.Employee;
import com.payroll.model.HRAdmin;
import com.payroll.model.User;
import com.payroll.util.FileStorage;
import com.payroll.util.Logger;
import com.payroll.util.PasswordUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AuthService {

    private static final String USERS_FILE = "data/users.txt";

    private final Map<String, User> usersByUsername = new HashMap<>();
    private int nextEmployeeSeq = 1;

    public AuthService() {
        loadUsers();
        seedDefaultAdminIfEmpty();
    }

    private void loadUsers() {
        List<String> lines = FileStorage.readLines(USERS_FILE);
        for (String line : lines) {
            String[] p = line.split("\\|", -1);
            if (p.length < 4) continue; 

            String role = p[0];
            String username = p[1];
            String passwordHash = p[2];
            String fullName = p[3];

            if (role.equals("HRADMIN")) {
                usersByUsername.put(username, new HRAdmin(username, passwordHash, fullName));
            } else if (role.equals("EMPLOYEE") && p.length == 8) {
                String employeeId = p[4];
                String department = p[5];
                String designation = p[6];
                double baseSalary = Double.parseDouble(p[7]);
                Employee emp = new Employee(employeeId, username, passwordHash, fullName,
                        department, designation, baseSalary);
                usersByUsername.put(username, emp);
                int seq = extractSeq(employeeId);
                if (seq >= nextEmployeeSeq) nextEmployeeSeq = seq + 1;
            }
        }
        Logger.info("Loaded " + usersByUsername.size() + " user account(s) from disk.");
    }

    private int extractSeq(String id) {
        try {
            return Integer.parseInt(id.substring(1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void seedDefaultAdminIfEmpty() {
        if (usersByUsername.isEmpty()) {
            HRAdmin defaultAdmin = new HRAdmin("hradmin", PasswordUtil.hash("hr12345"), "Default HR Administrator");
            usersByUsername.put(defaultAdmin.getUsername(), defaultAdmin);
            persistAll();
            Logger.info("No users found. Seeded default HR admin (username: hradmin / password: hr12345).");
        }
    }

    public HRAdmin registerAdmin(String username, String plainPassword, String fullName)
            throws DuplicateUserException {
        if (usersByUsername.containsKey(username)) {
            throw new DuplicateUserException("Username '" + username + "' is already taken.");
        }
        HRAdmin admin = new HRAdmin(username, PasswordUtil.hash(plainPassword), fullName);
        usersByUsername.put(username, admin);
        persistAll();
        Logger.info("Registered new HR Admin account: " + username);
        return admin;
    }

    public Employee registerEmployee(String username, String plainPassword, String fullName,
                                      String department, String designation, double baseSalary)
            throws DuplicateUserException {
        if (usersByUsername.containsKey(username)) {
            throw new DuplicateUserException("Username '" + username + "' is already taken.");
        }
        String employeeId = "E" + String.format("%04d", nextEmployeeSeq++);
        Employee emp = new Employee(employeeId, username, PasswordUtil.hash(plainPassword), fullName,
                department, designation, baseSalary);
        usersByUsername.put(username, emp);
        persistAll();
        Logger.info("Registered new Employee account: " + username + " (" + employeeId + ")");
        return emp;
    }

    public User login(String username, String plainPassword) throws InvalidCredentialsException {
        User user = usersByUsername.get(username);
        if (user == null || !PasswordUtil.matches(plainPassword, user.getPasswordHash())) {
            Logger.warn("Failed login attempt for username: " + username);
            throw new InvalidCredentialsException("Invalid username or password.");
        }
        Logger.info("User logged in: " + username + " (" + user.getRole() + ")");
        return user;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        for (User u : usersByUsername.values()) {
            if (u instanceof Employee) {
                employees.add((Employee) u);
            }
        }
        employees.sort((a, b) -> a.getEmployeeId().compareTo(b.getEmployeeId()));
        return employees;
    }

    public Employee findEmployeeById(String employeeId) {
        for (User u : usersByUsername.values()) {
            if (u instanceof Employee && ((Employee) u).getEmployeeId().equals(employeeId)) {
                return (Employee) u;
            }
        }
        return null;
    }

    public void persistAll() {
        List<String> records = new ArrayList<>();
        for (User user : usersByUsername.values()) {
            records.add(user.toRecord());
        }
        FileStorage.writeLines(USERS_FILE, records);
    }
}
