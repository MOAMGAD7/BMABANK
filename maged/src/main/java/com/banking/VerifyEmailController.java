package com.banking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.IOException;
import java.util.Properties;

public class VerifyEmailController {

    @FXML private TextField codeField;
    @FXML private Label codeError;

    @FXML
    protected void handleVerify(ActionEvent event) throws IOException {
        String enteredCode = codeField.getText().trim();
        UserSession session = UserSession.getInstance();
        String username = session.getUsername();

        // التحقق إن الـ session لسه موجودة
        if (username == null || username.isEmpty()) {
            codeError.setText("Session expired. Please try again.");
            return;
        }

        // التحقق إن الكود مش فاضي
        if (enteredCode.isEmpty()) {
            codeError.setText("Please enter the verification code");
            return;
        }

        // التحقق إن الكود 6 أرقام
        if (!enteredCode.matches("\\d{6}")) {
            codeError.setText("Verification code must be 6 digits");
            return;
        }

        // التحقق من الكود باستخدام database_BankSystem
        if (database_BankSystem.verifyCode(username, enteredCode)) {
            // إرسال إيميل تأكيد التحقق
            String userEmail = getUserEmail(username);
            if (userEmail != null) {
                boolean emailSent = sendVerificationSuccessEmail(userEmail, username);
                if (emailSent) {
                    System.out.println("✅ Verification success email sent successfully to " + userEmail);
                } else {
                    System.out.println("❌ Failed to send verification success email to " + userEmail);
                }
            } else {
                System.out.println("❌ Could not retrieve email for user " + username);
            }

            // عرض رسالة نجاح
            showAlert("Success", "Email verified successfully!");

            // التحقق من مصدر الطلب (Forget Password أو Sign Up)
            if (session.isPasswordReset()) {
                // الحالة: المستخدم جاي من Forget Password
                // الانتقال إلى صفحة تغيير كلمة المرور
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/maged/ChangePassword.fxml"));
                Parent root = loader.load();
                ChangePasswordController controller = loader.getController();
                controller.setUsername(username); // تمرير الـ username لصفحة تغيير كلمة المرور

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().clear();
                scene.getStylesheets().add(session.isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
                stage.setScene(scene);
                stage.setTitle("Change Password");
                stage.show();

                // تنظيف حالة password reset من الـ session
                session.setPasswordReset(false);
            } else {
                // الحالة: المستخدم جاي من Sign Up
                // الانتقال إلى صفحة تسجيل الدخول
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/login.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().clear();
                scene.getStylesheets().add(session.isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
                stage.setScene(scene);
                stage.setTitle("Login");
                stage.show();

                // تنظيف الـ session بالكامل بعد التحقق الناجح
                session.clear();
            }
        } else {
            codeError.setText("Invalid or expired verification code");
        }
    }

    private String getUserEmail(String username) {
        database_BankSystem.UserDetails userDetails = database_BankSystem.getUserDetails(username);
        if (userDetails != null) {
            return userDetails.getEmail();
        }
        return null;
    }

    private boolean sendVerificationSuccessEmail(String toEmail, String username) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            System.out.println("❌ Email address is null or empty. Cannot send verification email.");
            return false;
        }

        String host = "smtp.gmail.com";
        String port = "587";
        String mailFrom = "mohamedamgad7777@gmail.com";
        String password = "xnpvkxlplwtqscbg";

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.debug", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailFrom, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailFrom));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("BMA Bank - Email Verification Successful");

            String htmlContent = """
                <div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #f9f9f9;'>
                    <h2 style='color: #2c3e50; text-align: center;'>Email Verification Successful!</h2>
                    <p style='color: #34495e; font-size: 16px;'>Dear %s,</p>
                    <p style='color: #34495e; font-size: 16px;'>Congratulations! Your email address has been successfully verified.</p>
                    <p style='color: #34495e; font-size: 16px;'>You can now log in to your BMA Bank account and start exploring our services.</p>
                    <p style='color: #34495e; font-size: 16px; text-align: center; margin: 20px 0;'>
                        <a href='#' style='background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>
                            Log In Now
                        </a>
                    </p>
                    <p style='color: #34495e; font-size: 16px;'>If you have any questions or need assistance, feel free to reach out to us at 
                        <a href='mailto:support@bmabank.com' style='color: #4CAF50; text-decoration: none;'>support@bmabank.com</a>
                    </p>
                    <p style='color: #34495e; font-size: 16px; text-align: center; margin-top: 30px;'>
                        Welcome aboard,<br>
                        <strong style='color: #2c3e50;'>BMA Bank Team</strong>
                    </p>
                </div>
                """.formatted(username);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            System.out.println("❌ Failed to send verification success email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    protected void switchToLogin(ActionEvent event) throws IOException {
        UserSession session = UserSession.getInstance();
        session.clear();
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().clear();
        scene.getStylesheets().add(session.isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}