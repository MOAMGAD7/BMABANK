package com.banking;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        // إنشاء الجداول في قاعدة البيانات عند بدء التطبيق
        database_BankSystem.createTables();

        // تحميل واجهة تسجيل الدخول
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/login.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Banking System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}