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

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.IOException;
import java.util.Properties;

public class ForgetPasswordController {

    @FXML private TextField emailField;
    @FXML private Label errorLabel;

    // Email settings (SMTP)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SENDER_EMAIL = "mohamedamgad7777@gmail.com"; // Replace with your Gmail address
    private static final String SENDER_PASSWORD = "xnpvkxlplwtqscbg"; // Replace with your App Password

    @FXML
    public void initialize() {
        // Debugging to check if fields are initialized
        if (emailField == null) {
            System.out.println("❌ emailField is null - FXML binding issue");
        }
        if (errorLabel == null) {
            System.out.println("❌ errorLabel is null - FXML binding issue");
        }
    }

    @FXML
    protected void handleSendCode(ActionEvent event) throws IOException {
        String email = emailField.getText();

        if (email.isEmpty()) {
            if (errorLabel != null) {
                errorLabel.setText("Please enter your email");
            } else {
                System.out.println("Error: Cannot set error message - errorLabel is null");
            }
            return;
        }

        // Find user by email
        String username = database_BankSystem.getUsernameByEmail(email);
        if (username == null) {
            if (errorLabel != null) {
                errorLabel.setText("No user found with this email");
            } else {
                System.out.println("Error: Cannot set error message - errorLabel is null");
            }
            return;
        }

        // Generate and save verification code
        String code = database_BankSystem.generateAndSaveVerificationCode(username);
        if (code == null) {
            if (errorLabel != null) {
                errorLabel.setText("Failed to generate verification code, please try again");
            } else {
                System.out.println("Error: Cannot set error message - errorLabel is null");
            }
            return;
        }

        // Send the verification code via email
        boolean emailSent = sendVerificationEmail(email, code);
        if (!emailSent) {
            if (errorLabel != null) {
                errorLabel.setText("Failed to send email, please try again");
            } else {
                System.out.println("Error: Cannot set error message - errorLabel is null");
            }
            return;
        }

        // Update session
        UserSession session = UserSession.getInstance();
        session.setUsername(username);
        session.setPasswordReset(true);
        session.setRequestSource("ForgetPassword"); // تعيين المصدر
        System.out.println("Setting Request Source in ForgetPasswordController: " + session.getRequestSource()); // سجل للتأكد

        // Navigate to the verification page
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
    protected void switchToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/Login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        UserSession session = UserSession.getInstance();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(session.isDarkMode() ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css");
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    // Method to send email
    private boolean sendVerificationEmail(String recipientEmail, String verificationCode) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // Trust Gmail's SMTP server
        props.put("mail.debug", "true"); // Enable debug output for more details

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Password Reset Verification Code");
            message.setText("Hello,\n\nYour verification code to reset your password is: " + verificationCode + "\n\nBest regards,\nBank Team");

            Transport.send(message);
            System.out.println("✅ Verification email sent successfully to " + recipientEmail);
            return true;
        } catch (MessagingException e) {
            System.out.println("❌ Error sending verification email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}