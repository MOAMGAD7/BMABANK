package com.banking;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

import static java.lang.Double.parseDouble;

public class SignupController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField mobileField;
    @FXML private TextField nationalIdField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField imagePathField;
    @FXML private ImageView imageView;
    @FXML private TextField tb;

    @FXML private Label nameError, usernameError, passwordError, emailError, mobileError, nationalIdError, tbError;
    @FXML private Button signupButton;

    @FXML
    public void initialize() {
        ChangeListener<String> validator = (obs, oldVal, newVal) -> validateForm();

        nameField.textProperty().addListener(validator);
        usernameField.textProperty().addListener(validator);
        passwordField.textProperty().addListener(validator);
        emailField.textProperty().addListener(validator);
        mobileField.textProperty().addListener(validator);
        nationalIdField.textProperty().addListener(validator);
        tb.textProperty().addListener(validator);
        imagePathField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateForm();
            updateImageView(newVal);
        });
        dobPicker.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());

        validateForm();
    }

    private void updateImageView(String path) {
        try {
            if (path != null && !path.trim().isEmpty()) {
                String url = "file:/" + path.trim().replace("\\", "/").replace(" ", "%20");
                Image img = new Image(url, true);
                imageView.setImage(img);
            } else {
                imageView.setImage(null);
            }
        } catch (Exception e) {
            imageView.setImage(null);
        }
    }

    private void validateForm() {
        boolean valid = true;

        if (!nameField.getText().matches("[a-zA-Z\\s]{3,}")) {
            nameError.setText("Enter a valid name (letters only)");
            valid = false;
        } else nameError.setText("");

        if (!usernameField.getText().matches("[a-zA-Z0-9_]{4,}")) {
            usernameError.setText("Username must be 4+ chars (no spaces)");
            valid = false;
        } else usernameError.setText("");

        if (passwordField.getText().length() < 6) {
            passwordError.setText("Password must be at least 6 chars");
            valid = false;
        } else passwordError.setText("");

        if (!emailField.getText().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            emailError.setText("Enter a valid email");
            valid = false;
        } else emailError.setText("");

        if (!mobileField.getText().matches("[0-9]{10,15}")) {
            mobileError.setText("Enter valid mobile number");
            valid = false;
        } else mobileError.setText("");

        if (!nationalIdField.getText().matches("[0-9]{10,}")) {
            nationalIdError.setText("Enter valid national ID");
            valid = false;
        } else nationalIdError.setText("");

        if (dobPicker.getValue() == null) {
            valid = false;
        }

        if (!imagePathField.getText().toLowerCase().endsWith(".png") &&
                !imagePathField.getText().toLowerCase().endsWith(".jpg")) {
            valid = false;
        }

        try {
            double balance = Double.parseDouble(tb.getText());
            if (balance < 0) {
                tbError.setText("Balance must be zero or positive");
                valid = false;
            } else {
                tbError.setText("");
            }
        } catch (NumberFormatException e) {
            tbError.setText("Enter a valid number for balance");
            valid = false;
        }

        signupButton.setDisable(!valid);
    }

    @FXML
    protected void handleSignup(ActionEvent event) {
        try {
            String imagePath = imagePathField.getText();
            if (!imagePath.toLowerCase().endsWith(".png") && !imagePath.toLowerCase().endsWith(".jpg")) {
                showAlert("Error", "Image must be in .png or .jpg format");
                return;
            }

            boolean isRegistered = database_BankSystem.registerUser(
                    usernameField.getText(),
                    passwordField.getText(),
                    nameField.getText(),
                    emailField.getText(),
                    mobileField.getText(),
                    nationalIdField.getText(),
                    imagePath,
                    parseDouble(tb.getText())
            );

            if (isRegistered) {
                // تخزين username في UserSession
                UserSession session = UserSession.getInstance();
                session.setUsername(usernameField.getText());
                session.setRequestSource("Signup"); // تعيين المصدر
                System.out.println("Setting Request Source in SignupController: " + session.getRequestSource()); // سجل للتأكد

                // إرسال بريد إلكتروني تحقق مع رمز التحقق
                boolean emailSent = sendVerificationEmail(emailField.getText(), usernameField.getText());
                if (emailSent) {
                    showAlert("Success", "Registration successful! A verification email with a code has been sent to your email address.");
                } else {
                    showAlert("Success", "Registration successful! However, we couldn't send a verification email. Please check your email settings.");
                    return; // إذا فشل إرسال البريد، لا ننتقل إلى صفحة التحقق
                }

                // الانتقال إلى صفحة التحقق
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/VerifyEmail.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().clear();
                scene.getStylesheets().add(session.isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
                stage.setScene(scene);
                stage.setTitle("Verify Email");
                stage.show();
            } else {
                showAlert("Error", "Registration failed. Username may be taken.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    private boolean sendVerificationEmail(String toEmail, String username) {
        // إعدادات SMTP لـ Gmail
        String host = "smtp.gmail.com";
        String port = "587";
        String mailFrom = "mohamedamgad7777@gmail.com";
        String password = "xnpvkxlplwtqscbg";

        // إعداد خصائص البريد
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.debug", "true");

        // إنشاء جلسة بريد
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailFrom, password);
            }
        });

        try {
            // توليد وتخزين كود التحقق باستخدام قاعدة البيانات
            String verificationCode = database_BankSystem.generateAndSaveVerificationCode(username);
            if (verificationCode == null) {
                System.out.println("❌ Failed to generate or save verification code for " + username);
                return false;
            }

            // إنشاء رسالة بريد إلكتروني
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailFrom));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("Welcome to BMA Bank - Verify Your Email");

            // إعداد محتوى الرسالة بتنسيق HTML مع رمز التحقق
            String htmlContent = """
                <div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #f9f9f9;'>
                    <h2 style='color: #2c3e50; text-align: center;'>Welcome to BMA Bank!</h2>
                    <p style='color: #34495e; font-size: 16px;'>Dear %s,</p>
                    <p style='color: #34495e; font-size: 16px;'>Thank you for joining BMA Bank! To complete your registration, please verify your email address using the code below:</p>
                    <div style='text-align: center; margin: 20px 0;'>
                        <h3 style='color: #4CAF50; background-color: #e8f5e9; display: inline-block; padding: 10px 20px; border-radius: 5px; letter-spacing: 2px;'>
                            %s
                        </h3>
                    </div>
                    <p style='color: #34495e; font-size: 16px;'>Enter this code in the app to activate your account. <strong>This code is valid for 10 minutes.</strong></p>
                    <p style='color: #34495e; font-size: 16px;'>If you didn’t request this, please ignore this email or contact our support team.</p>
                    <p style='color: #34495e; font-size: 16px;'>
                        Need help? Feel free to reach out at 
                        <a href='mailto:support@bmabank.com' style='color: #4CAF50; text-decoration: none;'>support@bmabank.com</a>.
                    </p>
                    <p style='color: #34495e; font-size: 16px; text-align: center; margin-top: 30px;'>
                        Best regards,<br>
                        <strong style='color: #2c3e50;'>BMA Bank Team</strong>
                    </p>
                </div>
                """.formatted(username, verificationCode);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            // إرسال الرسالة
            Transport.send(message);
            System.out.println("✅ Verification email sent successfully to " + toEmail + " with code: " + verificationCode);

            // تحقق إضافي إن الكود اتخزن في قاعدة البيانات
            String storedCode = database_BankSystem.getVerificationCode(username);
            if (storedCode == null || !storedCode.equals(verificationCode)) {
                System.out.println("❌ Verification code not found in database after sending email for " + username);
                return false;
            }

            return true;
        } catch (MessagingException e) {
            System.out.println("❌ Failed to send verification email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    protected void switchToLogin(ActionEvent event) throws IOException {
        UserSession session = UserSession.getInstance();
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