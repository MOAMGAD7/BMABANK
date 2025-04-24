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

            String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "full_name TEXT," +
                    "email TEXT," +
                    "mobile TEXT," +
                    "national_id TEXT," +
                    "profile_image TEXT" +  // ← أضفنا العمود ده
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
            try {
                stmt.executeUpdate("ALTER TABLE users ADD COLUMN profile_image TEXT;");
            } catch (SQLException e) {
                System.out.println("العمود profile_image موجود بالفعل.");
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
                                       String mobile, String nationalId, String imagePath) {
        try {
            String hashedPassword = hashPassword(password);

            Connection conn = DriverManager.getConnection("jdbc:sqlite:bank.db");
            String sql = "INSERT INTO users (username, password, full_name, email, mobile, national_id, profile_image) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, name);
            pstmt.setString(4, email);
            pstmt.setString(5, mobile);
            pstmt.setString(6, nationalId);
            pstmt.setString(7, imagePath);  // ← حفظ مسار الصورة
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
}