package com.banking;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // التأكد من إنشاء الجداول عند بدء التطبيق
        database_BankSystem.createTables();

        Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/login.fxml"));
        primaryStage.setTitle("Bank System - Login");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}