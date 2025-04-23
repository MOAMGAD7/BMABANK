package com.example.maged;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

class HelloApplication {
    public static void main(String[] args) {
        Connection connection = null;

        try {
            // تحميل السائق (Driver)
            Class.forName("org.sqlite.JDBC");

            // الاتصال بقاعدة البيانات (إذا لم تكن موجودة، سيتم إنشاؤها)
            connection = DriverManager.getConnection("jdbc:sqlite:example.db");

            // إنشاء جدول كمثال
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users " +
                    "(id INTEGER PRIMARY KEY, name TEXT, email TEXT)";
            stmt.executeUpdate(sql);
            System.out.println("تم إنشاء الجدول بنجاح!");

            // غلق الاتصال
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
