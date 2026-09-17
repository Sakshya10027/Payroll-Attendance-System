package com.payroll.model;

public class HRAdmin extends User {

    private static final long serialVersionUID = 1L;

    public HRAdmin(String username, String passwordHash, String fullName) {
        super(username, passwordHash, fullName);
    }

    @Override
    public String getRole() {
        return "HRADMIN";
    }
}
