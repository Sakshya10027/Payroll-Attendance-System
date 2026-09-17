package com.payroll.model;

import java.io.Serializable;
import java.time.LocalDate;


public class AttendanceRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String recordId;
    private final String employeeId;
    private final LocalDate date;
    private final AttendanceStatus status;

    public AttendanceRecord(String recordId, String employeeId, LocalDate date, AttendanceStatus status) {
        this.recordId = recordId;
        this.employeeId = employeeId;
        this.date = date;
        this.status = status;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public double presentWeight() {
        switch (status) {
            case PRESENT: return 1.0;
            case HALF_DAY: return 0.5;
            default: return 0.0; 
        }
    }

    public String toRecord() {
        return String.join("|", recordId, employeeId, date.toString(), status.name());
    }

    @Override
    public String toString() {
        return String.format("%s | Employee:%s | Date:%s | Status:%s",
                recordId, employeeId, date, status);
    }
}
