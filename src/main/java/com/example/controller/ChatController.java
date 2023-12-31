package com.example.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import util.ApplicationData;

public class ChatController {
    @FXML
    private ScrollPane sp_main;
    @FXML
    private TextField tf_messages;
    @FXML
    private VBox vbox_messages;

    @FXML
    private void initialize() {
        sp_main.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp_main.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tf_messages.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                send();
            }
        });
        vbox_messages.heightProperty().addListener((observableValue, number, t1) -> sp_main.setVvalue((Double) t1));
    }

    private void send() {
        String message = tf_messages.getText();
        if (!message.isEmpty()) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5, 5, 5, 10));

            Text text = new Text(message);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-color: rgb(239, 242, 255);"
                    + "-fx-background-color: rgb(15, 125, 242);"
                    + "-fx-background-radius: 15px;");
            textFlow.setPadding(new Insets(5, 10, 5, 10));
            text.setFill(Color.color(0.934, 0.945, 0.996));
            hBox.getChildren().add(textFlow);
            vbox_messages.getChildren().add(hBox);
            if (GameStates.isServer()) {
                ApplicationData.getInstance().getServer().sendMessageToClient("/t" + message);
            } else {
                ApplicationData.getInstance().getClient().sendMessageToServer("/t" + message);
            }
            tf_messages.clear();
        }
    }

    public void addLabel(String message) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233, 233, 235);"
                + "-fx-background-radius: 15px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hBox.getChildren().add(textFlow);

        Platform.runLater(() -> vbox_messages.getChildren().add(hBox));
    }
}
