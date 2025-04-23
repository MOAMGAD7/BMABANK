// Main.java
package com.banking;

import com.banking.database_BankSystem;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // إنشاء الجداول عند بدء التطبيق
        database_BankSystem.createTables();

        try {
            System.out.println("Loading FXML...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/maged/login.fxml"));
            Scene scene = new Scene(loader.load());
            System.out.println("Scene loaded!");
            primaryStage.setTitle("Banking System - Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.out.println("ثقق!");
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
