package com.banking;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize database tables
        database_BankSystem.createTables();

        // Load the initial scene (e.g., Login page)
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/Login.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(UserSession.getInstance().isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Bank App");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}