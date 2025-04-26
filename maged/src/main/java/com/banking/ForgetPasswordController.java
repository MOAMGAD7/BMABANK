package com.banking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ForgetPasswordController {

    @FXML private TextField emailField;
    @FXML private Label errorLabel;

    @FXML
    protected void handleSendCode(ActionEvent event) throws IOException {
        String email = emailField.getText();

        if (email.isEmpty()) {
            errorLabel.setText("Please enter your email.");
            return;
        }

        // البحث عن المستخدم بناءً على الإيميل
        String username = database_BankSystem.getUsernameByEmail(email);
        if (username == null) {
            errorLabel.setText("No user found with this email.");
            return;
        }

        // توليد كود تحقق وحفظه
        String code = database_BankSystem.generateAndSaveVerificationCode(username);
        if (code == null) {
            errorLabel.setText("Failed to generate verification code.");
            return;
        }

        // هنا المفروض ترسل الإيميل بالكود (لكن مش هننفذ ده دلوقتي)
        System.out.println("✅ Verification code sent to " + email + ": " + code);

        // ضبط الـ session
        UserSession session = UserSession.getInstance();
        session.setUsername(username);
        session.setPasswordReset(true); // ضبط الحالة عشان نعرف إن المستخدم جاي من Forget Password

        // الانتقال لصفحة التحقق
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/VerifyEmail.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().clear();
        scene.getStylesheets().add(session.isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
        stage.setScene(scene);
        stage.setTitle("Verify Email");
        stage.show();
    }

    @FXML
    protected void switchToLogin(ActionEvent event) throws IOException { // Renamed from handleBackToLogin to switchToLogin
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