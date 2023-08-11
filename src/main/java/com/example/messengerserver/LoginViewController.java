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
import java.util.Objects;

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
    @FXML
    private Button connectButton;

    private final Main main;

    public LoginViewController() {
        this.main = new Main();
    }

    @FXML
    void randomPieces(ActionEvent event) {
        boolean white = Math.random() < 0.5;
        Main.setWhite(white);
        connect();
    }

    @FXML
    void whitePieces(ActionEvent event) {
        Main.setWhite(true);
        connect();
    }

    @FXML
    void blackPieces(ActionEvent event) {
        Main.setWhite(false);
        connect();
    }

    @FXML
    void serverToggle(ActionEvent event) {
        this.ipField.setDisable(true);
        main.setServer(true);
        this.connectButton.setVisible(false);
        this.blackPieceColor.setVisible(true);
        this.whitePieceColor.setVisible(true);
        this.randomPieceColor.setVisible(true);
    }

    @FXML
    void clientToggle(ActionEvent event) {
        this.ipField.setDisable(false);
        main.setServer(false);
        this.connectButton.setVisible(true);
        this.blackPieceColor.setVisible(false);
        this.whitePieceColor.setVisible(false);
        this.randomPieceColor.setVisible(false);
    }

    @FXML
    void connectClient(ActionEvent event) {
        connect();
    }

    private void connect() {
        setParameters();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (main.isServer()) {
                    try {
                        System.out.println("Server");
                        Parent mainWindowParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("serverView.fxml")));
                        Scene mainWindowScene = new Scene(mainWindowParent);
                        Stage window = (Stage) whitePieceColor.getScene().getWindow();
                        window.setScene(mainWindowScene);
                        window.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("Client");
                    try {
                        Parent mainWindowParent = null;
                        mainWindowParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("clientView.fxml")));
                        Scene mainWindowScene = new Scene(mainWindowParent);
                        Stage window = (Stage) whitePieceColor.getScene().getWindow();
                        window.setScene(mainWindowScene);
                        window.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void setParameters() {
        int port = 0;
        if (!this.portField.getText().isEmpty()) {
            port = Integer.parseInt(this.portField.getText());
            if (port > 1 && port <= 65535) {
                main.setPort(port);
            }
        }
        RadioButton selected = (RadioButton) toggleGroup.getSelectedToggle();
        if (selected.getText().equals("Client")) {
            main.setServer(false);
            String ip = this.ipField.getText();
            ClientController.setIp_Address(ip);
            ClientController.setPortNr(port);
            main.setIpAddress(ip);
        } else {
            main.setServer(true);
            ServerController.setServerPort(port);
        }
    }
}