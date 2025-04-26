//package com.banking;
//
//import java.sql.*;
//import java.util.Random;
//
//public class VerificationCodeManager {
//
//    private static final String DB_URL = "jdbc:sqlite:bank.db";
//
//    // توليد وتخزين كود التحقق
//    public static String generateAndSaveVerificationCode(String username) {
//        Random random = new Random();
//        String code = String.format("%06d", random.nextInt(1000000));
//
//        String sql = "INSERT INTO verification_codes (username, code) VALUES (?, ?) " +
//                "ON CONFLICT(username) DO UPDATE SET code = excluded.code";
//
//        try (Connection conn = DriverManager.getConnection(DB_URL);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, username);
//            pstmt.setString(2, code);
//            pstmt.executeUpdate();
//            System.out.println("✅ Verification code generated and saved for " + username + ": " + code);
//            return code;
//        } catch (SQLException e) {
//            System.out.println("❌ Error saving verification code: " + e.getMessage());
//            return null;
//        }
//    }
//
//    // جلب كود التحقق
//    public static String getVerificationCode(String username) {
//        String sql = "SELECT code FROM verification_codes WHERE username = ?";
//        try (Connection conn = DriverManager.getConnection(DB_URL);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, username);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                return rs.getString("code");
//            }
//        } catch (SQLException e) {
//            System.out.println("❌ Error fetching verification code: " + e.getMessage());
//        }
//        return null;
//    }
//
//    // تحقق من الكود
//    public static boolean verifyCode(String username, String enteredCode) {
//        String code = getVerificationCode(username);
//        if (code != null && code.equals(enteredCode)) {
//            deleteVerificationCode(username);
//            return true;
//        }
//        return false;
//    }
//
//    // حذف كود التحقق بعد نجاح التحقق
//    public static void deleteVerificationCode(String username) {
//        String sql = "DELETE FROM verification_codes WHERE username = ?";
//        try (Connection conn = DriverManager.getConnection(DB_URL);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, username);
//            pstmt.executeUpdate();
//            System.out.println("✅ Verification code deleted for " + username);
//        } catch (SQLException e) {
//            System.out.println("❌ Error deleting verification code: " + e.getMessage());
//        }
//    }
//}
