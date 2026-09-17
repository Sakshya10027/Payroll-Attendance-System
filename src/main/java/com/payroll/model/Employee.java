package com.payroll.model;


public class Employee extends User {

    private static final long serialVersionUID = 1L;

    private final String employeeId;
    private String department;
    private String designation;
    private double baseSalary;

    public Employee(String employeeId, String username, String passwordHash, String fullName,
                     String department, String designation, double baseSalary) {
        super(username, passwordHash, fullName);
        this.employeeId = employeeId;
        this.department = department;
        this.designation = designation;
        this.baseSalary = baseSalary;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    @Override
    public String getRole() {
        return "EMPLOYEE";
    }

    @Override
    public String toRecord() {
        return String.join("|", getRole(), username, passwordHash, fullName,
                employeeId, department, designation, String.valueOf(baseSalary));
    }

    @Override
    public String toString() {
        return String.format("[%s] %-20s | %-15s | %-15s | Base Salary: %.2f",
                employeeId, fullName, department, designation, baseSalary);
    }
}
