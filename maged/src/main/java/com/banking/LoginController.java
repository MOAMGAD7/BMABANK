package com.banking;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Hyperlink forgetPasswordLink; // رابط "Forget Password"

    private int failedAttempts = 0; // عدد المحاولات الخاطئة
    private final int MAX_ATTEMPTS = 5; // الحد الأقصى للمحاولات
    private boolean isLocked = false; // حالة القفل
    private Timeline lockTimeline; // عداد 30 ثانية

    @FXML
    public void initialize() {
        forgetPasswordLink.setVisible(false); // إخفاء الرابط في البداية
    }

    @FXML
    protected void handleLogin(ActionEvent event) throws IOException {
        // إذا كان الحساب مقفول، نمنع تسجيل الدخول
        if (isLocked) {
            errorLabel.setText("Too many failed attempts. Please wait.");
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        // التحقق من إن الحقول مش فاضية
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password");
            return;
        }

        // التحقق من بيانات تسجيل الدخول باستخدام قاعدة البيانات
        if (database_BankSystem.loginUser(username, password)) {
            // تحديث تاريخ آخر تسجيل دخول
            database_BankSystem.updateLastLogin(username);

            // تخزين username في UserSession
            UserSession session = UserSession.getInstance();
            session.setUsername(username);

            // الانتقال إلى الصفحة الرئيسية
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/Settings.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(session.isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
            stage.setScene(scene);
            stage.setTitle("Main Page");
            stage.show();

            // إعادة تعيين عدد المحاولات بعد تسجيل دخول ناجح
            failedAttempts = 0;
            forgetPasswordLink.setVisible(false);
        } else {
            failedAttempts++;
            errorLabel.setText("Invalid username or password. Attempt " + failedAttempts + " of " + MAX_ATTEMPTS);

            // التحقق إذا وصلنا للحد الأقصى للمحاولات
            if (failedAttempts >= MAX_ATTEMPTS) {
                isLocked = true;
                loginButton.setDisable(true); // تعطيل زرار تسجيل الدخول
                forgetPasswordLink.setVisible(true); // إظهار رابط "Forget Password"

                // إعداد عداد 30 ثانية
                lockTimeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> {
                    isLocked = false;
                    loginButton.setDisable(false);
                    failedAttempts = 0;
                    errorLabel.setText("You can try again now.");
                    forgetPasswordLink.setVisible(false);
                }));
                lockTimeline.setCycleCount(1);
                lockTimeline.play();

                errorLabel.setText("Too many failed attempts. Wait 30 seconds to try again.");
            }
        }
    }

    @FXML
    protected void handleForgetPassword(ActionEvent event) throws IOException {
        // الانتقال إلى صفحة "Forget Password"
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/ForgetPassword.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        UserSession session = UserSession.getInstance();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(session.isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
        stage.setScene(scene);
        stage.setTitle("Forget Password");
        stage.show();
    }

    @FXML
    protected void switchToSignup(ActionEvent event) throws IOException {
        UserSession session = UserSession.getInstance();
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/signup.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().clear();
        scene.getStylesheets().add(session.isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
        stage.setScene(scene);
        stage.setTitle("Sign Up");
        stage.show();
    }
}