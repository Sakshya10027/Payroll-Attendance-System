package com.payroll.service;

import com.payroll.exception.AttendanceAlreadyMarkedException;
import com.payroll.model.AttendanceRecord;
import com.payroll.model.AttendanceStatus;
import com.payroll.util.FileStorage;
import com.payroll.util.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceService {

    private static final String ATTENDANCE_FILE = "data/attendance.txt";

    private final List<AttendanceRecord> records = new ArrayList<>();
    private int nextSeq = 1;

    public AttendanceService() {
        loadRecords();
    }

    private void loadRecords() {
        List<String> lines = FileStorage.readLines(ATTENDANCE_FILE);
        for (String line : lines) {
            String[] p = line.split("\\|", -1);
            if (p.length != 4) continue;
            AttendanceRecord r = new AttendanceRecord(
                    p[0], p[1], LocalDate.parse(p[2]), AttendanceStatus.valueOf(p[3]));
            records.add(r);
            int seq = extractSeq(p[0]);
            if (seq >= nextSeq) nextSeq = seq + 1;
        }
        Logger.info("Loaded " + records.size() + " attendance record(s) from disk.");
    }

    private int extractSeq(String id) {
        try {
            return Integer.parseInt(id.substring(1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public AttendanceRecord markAttendance(String employeeId, LocalDate date, AttendanceStatus status)
            throws AttendanceAlreadyMarkedException {
        boolean alreadyMarked = records.stream()
                .anyMatch(r -> r.getEmployeeId().equals(employeeId) && r.getDate().equals(date));
        if (alreadyMarked) {
            throw new AttendanceAlreadyMarkedException(
                    "Attendance for employee " + employeeId + " on " + date + " is already recorded.");
        }
        String recordId = "A" + String.format("%05d", nextSeq++);
        AttendanceRecord record = new AttendanceRecord(recordId, employeeId, date, status);
        records.add(record);
        persist();
        Logger.info("Attendance marked: " + employeeId + " -> " + status + " on " + date);
        return record;
    }

    public List<AttendanceRecord> getRecordsForEmployee(String employeeId) {
        List<AttendanceRecord> result = new ArrayList<>();
        for (AttendanceRecord r : records) {
            if (r.getEmployeeId().equals(employeeId)) {
                result.add(r);
            }
        }
        result.sort((a, b) -> a.getDate().compareTo(b.getDate()));
        return result;
    }

    public List<AttendanceRecord> getRecordsForMonth(String employeeId, int month, int year) {
        List<AttendanceRecord> result = new ArrayList<>();
        for (AttendanceRecord r : getRecordsForEmployee(employeeId)) {
            if (r.getDate().getMonthValue() == month && r.getDate().getYear() == year) {
                result.add(r);
            }
        }
        return result;
    }

    public double getPresentDaysForMonth(String employeeId, int month, int year) {
        double total = 0.0;
        for (AttendanceRecord r : getRecordsForMonth(employeeId, month, year)) {
            total += r.presentWeight();
        }
        return total;
    }

    public int getMarkedDaysForMonth(String employeeId, int month, int year) {
        return getRecordsForMonth(employeeId, month, year).size();
    }

    public List<AttendanceRecord> getAllRecords() {
        return new ArrayList<>(records);
    }

    private void persist() {
        List<String> lines = new ArrayList<>();
        for (AttendanceRecord r : records) {
            lines.add(r.toRecord());
        }
        FileStorage.writeLines(ATTENDANCE_FILE, lines);
    }
}
