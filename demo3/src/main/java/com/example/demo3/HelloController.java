package com.example.demo3;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.Scene;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HelloController {

    @FXML
    private TextField usernameField;

    @FXML
    private ComboBox<String> colorSchemeCombo;

    @FXML
    private ComboBox<String> fontSizeCombo;

    @FXML
    private Button applyButton;

    private final String PREF_FILE = "preferences.txt";

    private String favColor = "blue";

    @FXML
    public void initialize() {
        colorSchemeCombo.getItems().addAll("Light", "Dark");
        fontSizeCombo.getItems().addAll("12px", "14px", "16px");

        loadPreferences();


        colorSchemeCombo.sceneProperty().addListener((obs, oldScene, newScene) -> {
                applyPreferences();

        });
    }

    @FXML
    private void applyChanges() {
        determineFavColor();
        savePreferences();
        applyPreferences();
    }

    private void applyPreferences() {
        String selectedColor = colorSchemeCombo.getValue();
        String selectedFontSize = fontSizeCombo.getValue();

        if (selectedColor == null || selectedFontSize == null) {
            return;
        }

        Scene scene = colorSchemeCombo.getScene();
        if (scene != null) {
            if (selectedColor.equalsIgnoreCase("Dark")) {
                scene.getRoot().setStyle("-fx-background-color: #2b2b2b; -fx-font-size: " + selectedFontSize + ";");
            } else {
                scene.getRoot().setStyle("-fx-background-color: white; -fx-font-size: " + selectedFontSize + ";");
            }
        } else {
            System.out.println("Scene is not available yet.");
        }


        String textColor = favColor.equals("green") ? "green" : "blue";


        String buttonTextColor = selectedColor.equalsIgnoreCase("Dark") ? "black" : "white";


        usernameField.setStyle("-fx-background-color: transparent; -fx-text-fill: " + textColor + "; -fx-font-size: " + selectedFontSize + ";");
        colorSchemeCombo.setStyle("-fx-text-fill: " + textColor + "; -fx-font-size: " + selectedFontSize + ";");
        fontSizeCombo.setStyle("-fx-text-fill: " + textColor + "; -fx-font-size: " + selectedFontSize + ";");
        applyButton.setStyle("-fx-background-color: " + favColor + "; -fx-text-fill: " + buttonTextColor + "; -fx-font-size: " + selectedFontSize + ";");
    }

    private void savePreferences() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            username = "defaultuser";
        }
        String selectedColor = colorSchemeCombo.getValue();
        String selectedFontSize = fontSizeCombo.getValue();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PREF_FILE))) {
            writer.write(username + "|" + selectedColor.toLowerCase() + "|" + selectedFontSize + "|" + favColor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPreferences() {
        if (!Files.exists(Paths.get(PREF_FILE))) {
            usernameField.setText("defaultuser");
            colorSchemeCombo.setValue("Light");
            fontSizeCombo.setValue("12px");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(PREF_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    String username = parts[0];
                    String colorScheme = parts[1];
                    String fontSize = parts[2];
                    favColor = parts[3];
                    usernameField.setText(username);
                    colorSchemeCombo.setValue(capitalize(colorScheme));
                    fontSizeCombo.setValue(fontSize);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void determineFavColor() {
        String selectedColor = colorSchemeCombo.getValue();
        if (selectedColor != null && selectedColor.equalsIgnoreCase("Dark")) {
            favColor = "green";
        } else {
            favColor = "blue";
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}