package com.banking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class SettingsController {
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField nationalIdField;
    @FXML private TextField balanceField;
    @FXML private ImageView profileImage;
    @FXML private Button editButton;
    @FXML private Button saveButton;
    @FXML private Button logoutButton;
    @FXML private Button toggleThemeButton;

    private String currentUsername;
    private String newImagePath;

    public void initialize() {
        currentUsername = UserSession.getInstance().getUsername();

        if (currentUsername == null || currentUsername.isEmpty()) {
            System.out.println("No user logged in.");
            return;
        }

        // تحميل التنسيق بناءً على UserSession
        applyTheme();

        // تحديث نص زر التبديل
        updateToggleThemeButtonText();

        // تحميل بيانات المستخدم
        loadUserData();
    }

    private void applyTheme() {
        boolean isDarkMode = UserSession.getInstance().isDarkMode();
        String cssPath = isDarkMode ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css";
        Scene scene = toggleThemeButton != null ? toggleThemeButton.getScene() : null;
        if (scene != null) {
            scene.getStylesheets().clear();
            try {
                if (getClass().getResource(cssPath) == null) {
                    System.err.println("CSS file not found: " + cssPath);
                } else {
                    scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
                    scene.getRoot().applyCss();
                    System.out.println("Applied CSS: " + cssPath);
                }
            } catch (Exception e) {
                System.err.println("Error loading CSS " + cssPath + ": " + e.getMessage());
            }
        }
    }

    private void loadUserData() {
        database_BankSystem.UserDetails userDetails = database_BankSystem.getUserDetails(currentUsername);
        if (userDetails != null) {
            fullNameField.setText(userDetails.getFullName());
            emailField.setText(userDetails.getEmail());
            phoneField.setText(userDetails.getMobile());
            nationalIdField.setText(userDetails.getNationalId());
            balanceField.setText(String.format("%.2f", userDetails.getTotalBalance()));
            String imagePath = userDetails.getProfileImage();
            if (imagePath != null && !imagePath.isEmpty()) {
                profileImage.setImage(new Image("file:" + imagePath));
            }
        } else {
            System.out.println("Failed to load user details for: " + currentUsername);
        }
    }

    @FXML
    private void enableEditing() {
        fullNameField.setEditable(true);
        emailField.setEditable(true);
        phoneField.setEditable(true);
        nationalIdField.setEditable(true);
        balanceField.setEditable(false);

        saveButton.setDisable(false);
        editButton.setDisable(true);
    }

    @FXML
    private void saveChanges() {
        boolean updated = database_BankSystem.updateUserDetails(
                currentUsername,
                fullNameField.getText(),
                emailField.getText(),
                phoneField.getText(),
                newImagePath != null ? newImagePath : getCurrentImagePath()
        );

        if (updated) {
            System.out.println("User data updated successfully!");
        } else {
            System.out.println("Failed to update user data.");
        }

        fullNameField.setEditable(false);
        emailField.setEditable(false);
        phoneField.setEditable(false);
        nationalIdField.setEditable(false);
        saveButton.setDisable(true);
        editButton.setDisable(false);

        loadUserData();
    }

    @FXML
    private void changeProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            newImagePath = selectedFile.getAbsolutePath();
            profileImage.setImage(new Image("file:" + newImagePath));
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            UserSession session = UserSession.getInstance();
            boolean isDarkMode = session.isDarkMode();
            session.clear();

            Parent root = FXMLLoader.load(getClass().getResource("/com/example/maged/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().clear();
            String cssPath = isDarkMode ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css";
            try {
                if (getClass().getResource(cssPath) == null) {
                    System.err.println("CSS file not found: " + cssPath);
                } else {
                    scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
                    System.out.println("Applied CSS on login: " + cssPath);
                }
            } catch (Exception e) {
                System.err.println("Error loading CSS on login: " + cssPath + ": " + e.getMessage());
            }
            stage.setScene(scene);
            stage.setTitle("Bank System - Login");
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading Login page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleTheme(ActionEvent event) {
        UserSession session = UserSession.getInstance();
        boolean isDarkMode = !session.isDarkMode();
        session.setDarkMode(isDarkMode);

        Scene scene = toggleThemeButton.getScene();
        scene.getStylesheets().clear();
        String cssPath = isDarkMode ? "/com/example/maged/DarkMode.css" : "/com/example/maged/LightMode.css";
        try {
            if (getClass().getResource(cssPath) == null) {
                System.err.println("CSS file not found: " + cssPath);
            } else {
                scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
                scene.getRoot().applyCss();
                System.out.println("Loaded CSS: " + cssPath);
            }
        } catch (Exception e) {
            System.err.println("Error loading CSS " + cssPath + ": " + e.getMessage());
        }

        updateToggleThemeButtonText();
    }

    private void updateToggleThemeButtonText() {
        boolean isDarkMode = UserSession.getInstance().isDarkMode();
        toggleThemeButton.setText(isDarkMode ? "Switch to Light Mode" : "Switch to Dark Mode");
    }

    private String getCurrentImagePath() {
        database_BankSystem.UserDetails userDetails = database_BankSystem.getUserDetails(currentUsername);
        if (userDetails != null) {
            return userDetails.getProfileImage();
        }
        return null;
    }
}