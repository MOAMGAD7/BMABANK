package com.banking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label usernameError;
    @FXML private Label passwordError;
    @FXML private Button loginButton;

    @FXML
    public void initialize() {
        // التحقق من أن الحقول ليست null قبل إضافة المستمعين
        if (usernameField != null) {
            usernameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        }
        if (passwordField != null) {
            passwordField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        }
        validateForm();
    }

    private void validateForm() {
        boolean valid = true;

        // التحقق من usernameField
        if (usernameField == null || usernameField.getText().isEmpty()) {
            if (usernameError != null) {
                usernameError.setText("Username is required");
            }
            valid = false;
        } else {
            if (usernameError != null) {
                usernameError.setText("");
            }
        }

        // التحقق من passwordField
        if (passwordField == null || passwordField.getText().isEmpty()) {
            if (passwordError != null) {
                passwordError.setText("Password is required");
            }
            valid = false;
        } else {
            if (passwordError != null) {
                passwordError.setText("");
            }
        }

        // تعطيل زر الدخول إذا لم تكن البيانات صالحة
        if (loginButton != null) {
            loginButton.setDisable(!valid);
        }
    }

    @FXML
    protected void handleLogin(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // التحقق مما إذا كان المستخدم قد تحقق من بريده الإلكتروني
        if (!database_BankSystem.isUserVerified(username)) {
            showAlert("Error", "Please verify your email address before logging in.");
            // الانتقال إلى صفحة التحقق
            UserSession session = UserSession.getInstance();
            session.setUsername(username);
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/VerifyEmail.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(session.isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
            stage.setScene(scene);
            stage.setTitle("Verify Email");
            stage.show();
            return;
        }

        boolean isAuthenticated = database_BankSystem.loginUser(username, password);
        if (isAuthenticated) {
            UserSession session = UserSession.getInstance();
            session.setUsername(username);
            database_BankSystem.updateLastLogin(username);

            Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/Settings.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(session.isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
            stage.setScene(scene);
            stage.setTitle("Settings");
            stage.show();
        } else {
            showAlert("Error", "Invalid username or password");
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
        stage.setTitle("Signup");
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