package com.banking;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.Base64;

class database_BankSystem {
    private static final String DB_URL = "jdbc:sqlite:bank.db";

    public static void createTables() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // إنشاء جدول المستخدمين
            String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "full_name TEXT," +
                    "email TEXT," +
                    "mobile TEXT," +
                    "national_id TEXT," +
                    "profile_image TEXT," +
                    "total_balance REAL DEFAULT 0" +
                    ");";

            String transactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "type TEXT," +
                    "amount REAL," +
                    "date TEXT DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(user_id) REFERENCES users(id)" +
                    ");";

            String transfersTable = "CREATE TABLE IF NOT EXISTS transfers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "from_user TEXT NOT NULL," +
                    "to_user TEXT NOT NULL," +
                    "amount REAL NOT NULL," +
                    "status TEXT DEFAULT 'pending'," +
                    "date TEXT DEFAULT CURRENT_TIMESTAMP" +
                    ");";

            String investmentsTable = "CREATE TABLE IF NOT EXISTS investments (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "type TEXT," +
                    "amount REAL," +
                    "date TEXT DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(user_id) REFERENCES users(id)" +
                    ");";

            // لو العمودين دول اتضافوا قبل كده، تجاهل الخطأ
            try {
                stmt.executeUpdate("ALTER TABLE users ADD COLUMN profile_image TEXT;");
            } catch (SQLException e) {
                System.out.println("العمود profile_image موجود بالفعل.");
            }

            try {
                stmt.executeUpdate("ALTER TABLE users ADD COLUMN total_balance REAL DEFAULT 0;");
            } catch (SQLException e) {
                System.out.println("العمود total_balance موجود بالفعل.");
            }

            stmt.execute(usersTable);
            stmt.execute(transactionsTable);
            stmt.execute(transfersTable);
            stmt.execute(investmentsTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean registerUser(String username, String password, String name, String email,
                                       String mobile, String nationalId, String imagePath, double initialBalance) {
        try {
            String hashedPassword = hashPassword(password);

            Connection conn = DriverManager.getConnection(DB_URL);
            String sql = "INSERT INTO users (username, password, full_name, email, mobile, national_id, profile_image, total_balance) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, name);
            pstmt.setString(4, email);
            pstmt.setString(5, mobile);
            pstmt.setString(6, nationalId);
            pstmt.setString(7, imagePath);
            pstmt.setDouble(8, initialBalance); // القيمة الابتدائية اللي المستخدم يحددها

            pstmt.executeUpdate();

            pstmt.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean loginUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                return storedHash.equals(hashPassword(password));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String hashPassword(String password) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), new byte[16], 65536, 128);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    // دالة استعلام الرصيد
    public static double getBalance(String username) {
        String sql = "SELECT total_balance FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // دالة الإيداع
    public static boolean deposit(String username, double amount) {
        if (amount <= 0) return false;
        double current = getBalance(username);
        return updateBalance(username, current + amount);
    }

    // دالة السحب
    public static boolean withdraw(String username, double amount) {
        if (amount <= 0) return false;
        double current = getBalance(username);
        if (current < amount) return false;
        return updateBalance(username, current - amount);
    }

    // دالة تحديث الرصيد
    private static boolean updateBalance(String username, double newBalance) {
        String sql = "UPDATE users SET total_balance = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String getFullName(String username) {
        String sql = "SELECT full_name FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("full_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // دالة للحصول على البريد الإلكتروني بناءً على اسم المستخدم
    public static String getEmail(String username) {
        String sql = "SELECT email FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // دالة للحصول على رقم الهاتف بناءً على اسم المستخدم
    public static String getMobile(String username) {
        String sql = "SELECT mobile FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("mobile");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // دالة للحصول على رقم الهوية الوطنية بناءً على اسم المستخدم
    public static String getNationalId(String username) {
        String sql = "SELECT national_id FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("national_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // دالة للحصول على مسار صورة الملف الشخصي بناءً على اسم المستخدم
    public static String getProfileImage(String username) {
        String sql = "SELECT profile_image FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("profile_image");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // دالة للحصول على الرصيد الإجمالي بناءً على اسم المستخدم
    public static double getTotalBalance(String username) {
        String sql = "SELECT total_balance FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }    public static boolean transfer(String fromUsername, String toUsername, double amount) {
        // التأكد من أن المبلغ أكبر من صفر
        if (amount <= 0) {
            return false; // المبلغ غير صالح
        }

        // الحصول على الرصيد الحالي للمستخدم الذي سيتم التحويل منه
        double fromBalance = getBalance(fromUsername);

        // التأكد أن الحساب الذي سيتم التحويل منه يحتوي على رصيد كافي
        if (fromBalance < amount) {
            return false; // ليس هناك رصيد كافي في الحساب
        }

        // الحصول على الرصيد الحالي للمستخدم الذي سيتم التحويل إليه (اختياري: يمكن استخدامه لتحديث أو التحقق)
        double toBalance = getBalance(toUsername);

        // تحديث الرصيد في الحسابين
        boolean updateFromSuccess = updateBalance(fromUsername, fromBalance - amount);
        boolean updateToSuccess = updateBalance(toUsername, toBalance + amount);

        // التأكد من نجاح التحديث في الحسابين
        return updateFromSuccess && updateToSuccess;
    }
}
