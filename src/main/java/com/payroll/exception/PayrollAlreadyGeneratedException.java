package com.payroll.exception;

public class PayrollAlreadyGeneratedException extends Exception {
    public PayrollAlreadyGeneratedException(String message) {
        super(message);
    }
}
