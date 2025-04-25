package com.banking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    public void initialize() {
        // لا حاجة لتغيير الـ stylesheet هنا لأنه معرف في FXML
        // لكن يمكننا التأكد من تطبيق الوضع عند التنقل
    }

    @FXML
    protected void handleLogin(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // التحقق من بيانات الدخول باستخدام database_BankSystem
        boolean isValidLogin = database_BankSystem.loginUser(username, password);

        if (isValidLogin) {
            // تخزين username في UserSession
            UserSession session = UserSession.getInstance();
            session.setUsername(username);
            database_BankSystem.updateLastLogin(username);

            try {
                // تحميل واجهة الإعدادات
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/Settings.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().clear();
                scene.getStylesheets().add(session.isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
                stage.setScene(scene);
                stage.setTitle("Settings");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load Settings page: " + e.getMessage());
            }
        } else {
            showAlert("Error", "Invalid username or password.");
        }
    }

    @FXML
    protected void switchToSignup(ActionEvent event) throws IOException {
        UserSession session = UserSession.getInstance();
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/Signup.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().clear();
        scene.getStylesheets().add(session.isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
        stage.setScene(scene);
        stage.setTitle("Sign Up");
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}