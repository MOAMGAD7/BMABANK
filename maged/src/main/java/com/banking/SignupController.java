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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

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
        imagePathField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateForm();
            updateImageView(newVal);
        });
        dobPicker.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        tb.textProperty().addListener((obs, oldVal, newVal) -> validateForm());

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

        // Validation for other fields (name, username, etc.) ...

        // Validation for Total Balance (tb)
        try {
            double totalBalance = parseDouble(tb.getText());
            if (totalBalance < 0) {
                tbError.setText("Total Balance cannot be negative.");
                valid = false;
            } else {
                tbError.setText("");
            }
        } catch (NumberFormatException e) {
            tbError.setText("Enter a valid number for Total Balance.");
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
                    imagePath, parseDouble(tb.getText())
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
