package com.banking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;

public class ChangePasswordController {

    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private String username; // هنخزن الـ username اللي جاي من VerifyEmailController

    // دالة لتمرير الـ username
    public void setUsername(String username) {
        this.username = username;
    }

    @FXML
    protected void handleChangePassword(ActionEvent event) throws IOException {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // التحقق من إن الحقول مش فاضية
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Please fill in both fields.");
            return;
        }

        // التحقق من إن كلمة المرور الجديدة مطابقة للتأكيد
        if (!newPassword.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        // التحقق من طول كلمة المرور (اختياري، على الأقل 6 أحرف)
        if (newPassword.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters long.");
            return;
        }

        // تحديث كلمة المرور في قاعدة البيانات
        boolean updated = database_BankSystem.updatePassword(username, newPassword);
        if (updated) {
            // لو التحديث ناجح، نرجع المستخدم لصفحة تسجيل الدخول
            errorLabel.setText("Password changed successfully!");
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            UserSession session = UserSession.getInstance();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(session.isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } else {
            errorLabel.setText("Failed to change password. Try again.");
        }
    }

    @FXML
    protected void handleBackToLogin(ActionEvent event) throws IOException {
        // العودة لصفحة تسجيل الدخول
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        UserSession session = UserSession.getInstance();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(session.isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
}