package com.banking;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ChatBotController {

    @FXML
    private TextField userInput;

    @FXML
    private VBox chatBox;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    protected void handleSend() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) return;

        addMessage("You: " + input, "#00ff88");
        userInput.clear();

        String response = getBotResponse(input.toLowerCase());
        addMessage("Bot: " + response, "#ffffff");
    }

    private void addMessage(String message, String color) {
        Text text = new Text(message);
        text.setStyle("-fx-fill: " + color + "; -fx-font-size: 14px;");
        TextFlow textFlow = new TextFlow(text);
        textFlow.setMaxWidth(400);
        chatBox.getChildren().add(textFlow);

        chatScrollPane.setVvalue(1.0); // Auto-scroll to bottom
    }

    private String getBotResponse(String message) {
        if (message.contains("balance")) {
            return "Your current balance is 5,250 EGP.";
        } else if (message.contains("transfer")) {
            return "You can make a transfer through the transfer page.";
        } else if (message.contains("problem") || message.contains("issue")) {
            return "Please describe your issue, and we will assist you.";
        } else if (message.contains("invest") || message.contains("investment")) {
            return "We offer investment plans starting from 1,000 EGP.";
        } else if (message.contains("thanks") || message.contains("thank you")) {
            return "You're welcome! Is there anything else I can help you with?";
        } else {
            return "Sorry, I didn’t understand that. Could you rephrase?";
        }
    }
}
