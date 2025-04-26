package com.banking;

public class UserSession {
    private static UserSession instance;
    private String username;
    private boolean isDarkMode = true; // افتراضيًا الدارك مود مفعل
    private String verificationCode; // متغير لتخزين رمز التحقق

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public void clear() {
        username = null;
        verificationCode = null; // مسح رمز التحقق عند مسح الجلسة
    }
}