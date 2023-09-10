package com.example.controller;

import javafx.application.Platform;
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
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private Button randomPieceColor;
    @FXML
    private ToggleGroup toggleGroup;
    @FXML
    private Button whitePieceColor;
    @FXML
    private Button connectButton;

    private static ServerController serverController;
    private static ClientController clientController;


    @FXML
    void randomPieces() {
        boolean white = Math.random() < 0.5;
        GameStates.setServerIswhite(white);
        GameStates.setIsMyTurn(white);
        connect();
    }

    @FXML
    void whitePieces() {
        GameStates.setServerIswhite(true);
        GameStates.setIsMyTurn(true);
        connect();
    }

    @FXML
    void blackPieces() {
        GameStates.setServerIswhite(false);
        GameStates.setIsMyTurn(false);
        connect();
    }

    @FXML
    void serverToggle() {
        this.ipField.setDisable(true);
        GameStates.setServer(true);
        this.connectButton.setVisible(false);
        this.blackPieceColor.setVisible(true);
        this.whitePieceColor.setVisible(true);
        this.randomPieceColor.setVisible(true);
    }

    @FXML
    void clientToggle() {
        this.ipField.setDisable(false);
        GameStates.setServer(false);
        this.connectButton.setVisible(true);
        this.blackPieceColor.setVisible(false);
        this.whitePieceColor.setVisible(false);
        this.randomPieceColor.setVisible(false);
    }

    @FXML
    void connectClient() {
        connect();
    }

    private void connect() {
        setParameters();
        Platform.runLater(() -> {
                    if (GameStates.isServer()) {
                        try {
                            System.out.println("Server");
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("serverView.fxml"));
                            Parent mainWindowParent = loader.load();
                            serverController = loader.getController();
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
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("clientView.fxml"));
                            Parent mainWindowParent = loader.load();
                            clientController = loader.getController();
                            Scene mainWindowScene = new Scene(mainWindowParent);
                            Stage window = (Stage) whitePieceColor.getScene().getWindow();
                            window.setScene(mainWindowScene);
                            window.show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
    }

    private void setParameters() {
        int port = 0;
        if (!this.portField.getText().isEmpty()) {
            port = Integer.parseInt(this.portField.getText());
        }
        RadioButton selected = (RadioButton) toggleGroup.getSelectedToggle();
        if (selected.getText().equals("Client")) {
            GameStates.setServer(false);
            String ip = this.ipField.getText();
            ClientController.setIp_Address(ip);
            ClientController.setPortNr(port);
        } else {
            GameStates.setServer(true);
            ServerController.setServerPort(port);
        }
    }

    public static ServerController getServerController() {
        return serverController;
    }

    public static ClientController getClientController() {
        return clientController;
    }
}