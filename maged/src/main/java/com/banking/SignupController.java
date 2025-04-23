package com.banking;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.regex.Pattern;
public class SignupController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField mobileField;
    @FXML private TextField nationalIdField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML private Label nameError, usernameError, passwordError, emailError, mobileError, nationalIdError;
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
        dobPicker.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());

        validateForm();
    }

    private void validateForm() {
        boolean valid = true;

        if (!nameField.getText().matches("[a-zA-Z\s]{3,}")) {
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

        signupButton.setDisable(!valid);
    }

    @FXML
    protected void handleSignup(ActionEvent event) {
        try {
            boolean isRegistered = database_BankSystem.registerUser(
                    usernameField.getText(),
                    passwordField.getText(),
                    nameField.getText(),
                    emailField.getText(),
                    mobileField.getText(),
                    nationalIdField.getText()
            );

            if (isRegistered) {
                showAlert("Success", "Registration successful!");
                switchToLogin(event);
            } else {
                showAlert("Error", "Registration failed. Username may be taken.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    @FXML
    protected void switchToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
