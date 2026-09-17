package com.payroll.model;

import java.io.Serializable;
import java.time.LocalDate;

public class PayrollRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String payrollId;
    private final String employeeId;
    private final int month; // 1-12
    private final int year;
    private final int workingDays;
    private final double presentDays; // weighted (half-days count 0.5)
    private final double grossPay;
    private final double deductions;
    private final double netPay;
    private final LocalDate generatedOn;

    public PayrollRecord(String payrollId, String employeeId, int month, int year,
                          int workingDays, double presentDays,
                          double grossPay, double deductions, double netPay,
                          LocalDate generatedOn) {
        this.payrollId = payrollId;
        this.employeeId = employeeId;
        this.month = month;
        this.year = year;
        this.workingDays = workingDays;
        this.presentDays = presentDays;
        this.grossPay = grossPay;
        this.deductions = deductions;
        this.netPay = netPay;
        this.generatedOn = generatedOn;
    }

    public String getPayrollId() {
        return payrollId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getWorkingDays() {
        return workingDays;
    }

    public double getPresentDays() {
        return presentDays;
    }

    public double getGrossPay() {
        return grossPay;
    }

    public double getDeductions() {
        return deductions;
    }

    public double getNetPay() {
        return netPay;
    }

    public LocalDate getGeneratedOn() {
        return generatedOn;
    }

    public String toRecord() {
        return String.join("|", payrollId, employeeId, String.valueOf(month), String.valueOf(year),
                String.valueOf(workingDays), String.valueOf(presentDays),
                String.valueOf(grossPay), String.valueOf(deductions), String.valueOf(netPay),
                generatedOn.toString());
    }

    @Override
    public String toString() {
        return String.format(
                "Payslip #%s | Employee:%s | %02d/%d | Working Days:%d | Present:%.1f | " +
                "Gross:%.2f | Deductions:%.2f | Net Pay:%.2f | Generated:%s",
                payrollId, employeeId, month, year, workingDays, presentDays,
                grossPay, deductions, netPay, generatedOn);
    }
}
