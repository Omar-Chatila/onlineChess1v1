package com.example.messengerserver;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginViewController {

    @FXML
    private Button blackPieceColor;
    @FXML
    private RadioButton clientButton;
    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private Button randomPieceColor;
    @FXML
    private RadioButton serverButton;
    @FXML
    private ToggleGroup toggleGroup;
    @FXML
    private Button whitePieceColor;

    private Main main;

    public LoginViewController() {
        this.main = new Main();
    }

    @FXML
    void randomPieces(ActionEvent event) {
        boolean white = Math.random() < 0.5;
        main.setWhite(white);
        connect();
    }

    @FXML
    void whitePieces(ActionEvent event) {
        main.setWhite(true);
        connect();
    }

    @FXML
    void blackPieces(ActionEvent event) {
        main.setWhite(false);
        connect();
    }

    @FXML
    void serverToggle(ActionEvent event) {
        this.ipField.setDisable(true);
        main.setServer(true);
    }

    @FXML
    void clientToggle(ActionEvent event) {
        this.ipField.setDisable(false);
        main.setServer(false);
    }

    private void connect() {
        if (!this.portField.getText().isEmpty()) {
            main.setServer(true);
            int port = Integer.parseInt(this.portField.getText());
            if (port > 1 && port <= 65535) {
                main.setPort(port);
            }
            RadioButton selected = (RadioButton) toggleGroup.getSelectedToggle();
            if (selected.getText().equals("Client")) {
                main.setServer(false);
                String ip = this.ipField.getText();
                main.setIpAddress(ip);
            }
        }
        if (main.isServer()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Scene scene = null;
                    if (main.isServer()) {
                        try {
                            Parent mainWindowParent = FXMLLoader.load(getClass().getResource("serverView.fxml"));
                            Scene mainWindowScene = new Scene(mainWindowParent);
                            Stage window = (Stage) whitePieceColor.getScene().getWindow();
                            window.setScene(mainWindowScene);
                            window.show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {

                    }
                }
            });
        }
    }
}