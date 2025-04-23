package com.example.maged;

import java.sql.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

 class database_BankSystem {
    private static final String DB_URL = "jdbc:sqlite:bank.db";

    // Generate Salt
    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Hash password using PBKDF2
    public static String hashPassword(String password, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), 65536, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    // إنشاء جميع الجداول
    public static void createTables() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // جدول المستخدمين
            String userTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "full_name TEXT NOT NULL," +
                    "email TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "salt TEXT NOT NULL," +
                    "mobile_number TEXT," +
                    "national_id TEXT," +
                    "dob TEXT," +
                    "gender TEXT," +
                    "account_type TEXT NOT NULL," +
                    "balance REAL DEFAULT 0" +
                    ");";

            String transactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "type TEXT," +
                    "amount REAL," +
                    "date TEXT DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(user_id) REFERENCES users(id)" +
                    ");";

            String transfersTable = "CREATE TABLE IF NOT EXISTS pending_transfers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "from_user TEXT," +
                    "to_user TEXT," +
                    "amount REAL," +
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

            stmt.execute(userTable);
            stmt.execute(transactionsTable);
            stmt.execute(transfersTable);
            stmt.execute(investmentsTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // تسجيل مستخدم جديد
    public static boolean registerUser(String fullName, String email, String password, String mobile,
                                       String nationalId, String dob, String gender, String accountType) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        String sql = "INSERT INTO users(full_name, email, password, salt, mobile_number, national_id, dob, gender, account_type, balance) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 10000)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, salt);
            pstmt.setString(5, mobile);
            pstmt.setString(6, nationalId);
            pstmt.setString(7, dob);
            pstmt.setString(8, gender);
            pstmt.setString(9, accountType);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Email already exists.");
            return false;
        }
    }

    // تسجيل الدخول
    public static boolean login(String email, String password) {
        String sql = "SELECT password, salt FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password");
                String salt = rs.getString("salt");
                String inputHash = hashPassword(password, salt);
                return storedHash.equals(inputHash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // باقي الدوال: getBalance, updateBalance, addTransaction, requestTransfer, approveTransfer, rejectTransfer
    // كلها تقدر تتنقل من الكود اللي قدمته قبل كده بدون تغيير كبير، فقط استخدم email بدل username.

    // تابع لما تبغى نكمل كل هذه الدوال بالباقي زي ما هي أو نحسنها كمان.
}
