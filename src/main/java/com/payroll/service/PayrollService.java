package com.payroll.service;

import com.payroll.exception.PayrollAlreadyGeneratedException;
import com.payroll.model.Employee;
import com.payroll.model.PayrollRecord;
import com.payroll.util.FileStorage;
import com.payroll.util.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PayrollService {

    private static final String PAYROLL_FILE = "data/payroll.txt";

    private final List<PayrollRecord> payrollRecords = new ArrayList<>();
    private final AttendanceService attendanceService;
    private int nextSeq = 1;

    public PayrollService(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
        loadRecords();
    }

    private void loadRecords() {
        List<String> lines = FileStorage.readLines(PAYROLL_FILE);
        for (String line : lines) {
            String[] p = line.split("\\|", -1);
            if (p.length != 10) continue;
            PayrollRecord r = new PayrollRecord(
                    p[0], p[1], Integer.parseInt(p[2]), Integer.parseInt(p[3]),
                    Integer.parseInt(p[4]), Double.parseDouble(p[5]),
                    Double.parseDouble(p[6]), Double.parseDouble(p[7]), Double.parseDouble(p[8]),
                    LocalDate.parse(p[9]));
            payrollRecords.add(r);
            int seq = extractSeq(p[0]);
            if (seq >= nextSeq) nextSeq = seq + 1;
        }
        Logger.info("Loaded " + payrollRecords.size() + " payroll record(s) from disk.");
    }

    private int extractSeq(String id) {
        try {
            return Integer.parseInt(id.substring(1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public PayrollRecord generatePayroll(Employee employee, int month, int year)
            throws PayrollAlreadyGeneratedException {
        boolean alreadyGenerated = payrollRecords.stream()
                .anyMatch(r -> r.getEmployeeId().equals(employee.getEmployeeId())
                        && r.getMonth() == month && r.getYear() == year);
        if (alreadyGenerated) {
            throw new PayrollAlreadyGeneratedException(
                    "Payroll for employee " + employee.getEmployeeId()
                            + " for " + month + "/" + year + " has already been generated.");
        }

        int workingDays = attendanceService.getMarkedDaysForMonth(employee.getEmployeeId(), month, year);
        double presentDays = attendanceService.getPresentDaysForMonth(employee.getEmployeeId(), month, year);

        double grossPay = employee.getBaseSalary();
        double perDayRate = workingDays == 0 ? 0.0 : grossPay / workingDays;
        double absentDays = workingDays - presentDays;
        double deductions = Math.max(0.0, absentDays) * perDayRate;
        double netPay = grossPay - deductions;

        String payrollId = "P" + String.format("%05d", nextSeq++);
        PayrollRecord record = new PayrollRecord(payrollId, employee.getEmployeeId(), month, year,
                workingDays, presentDays, grossPay, deductions, netPay, LocalDate.now());
        payrollRecords.add(record);
        persist();
        Logger.info("Payroll generated: " + payrollId + " for " + employee.getEmployeeId()
                + " (" + month + "/" + year + ") -> Net Pay: " + netPay);
        return record;
    }

    public List<PayrollRecord> getPayrollHistoryForEmployee(String employeeId) {
        List<PayrollRecord> result = new ArrayList<>();
        for (PayrollRecord r : payrollRecords) {
            if (r.getEmployeeId().equals(employeeId)) {
                result.add(r);
            }
        }
        result.sort((a, b) -> {
            if (a.getYear() != b.getYear()) return a.getYear() - b.getYear();
            return a.getMonth() - b.getMonth();
        });
        return result;
    }

    public List<PayrollRecord> getAllPayrollRecords() {
        return new ArrayList<>(payrollRecords);
    }

    private void persist() {
        List<String> lines = new ArrayList<>();
        for (PayrollRecord r : payrollRecords) {
            lines.add(r.toRecord());
        }
        FileStorage.writeLines(PAYROLL_FILE, lines);
    }
}
