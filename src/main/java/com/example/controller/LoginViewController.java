package com.example.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    @FXML
    private Label timeLabel;
    @FXML
    private JFXSlider timeSlider;
    @FXML
    private Label tcLabel;
    @FXML
    private JFXButton closeButton;

    private static ServerController serverController;
    private static ClientController clientController;


    @FXML
    private void initialize() {
        setTimeSlider();
        closeButton.setOnAction(e -> Platform.exit());
    }

    private void setTimeSlider() {
        timeLabel.setText("10:00");
        GameStates.setTimeControl(10 * 60);
        timeSlider.setMin(1);
        timeSlider.setMax(30);
        timeSlider.setValue(10);
        timeSlider.setBlockIncrement(1);

        timeSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        int tc = (int) Math.ceil(newValue.doubleValue());
                        GameStates.setTimeControl(tc * 60);
                        timeLabel.setText(tc + ":00");
                    }
                });
    }

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
        toggleVisibility(true);
    }

    @FXML
    void clientToggle() {
        this.ipField.setDisable(false);
        GameStates.setServer(false);
        this.connectButton.setVisible(true);
        toggleVisibility(false);
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

    private void toggleVisibility(boolean visible) {
        this.blackPieceColor.setVisible(visible);
        this.whitePieceColor.setVisible(visible);
        this.randomPieceColor.setVisible(visible);
        this.timeLabel.setVisible(visible);
        this.timeSlider.setVisible(visible);
        this.tcLabel.setVisible(visible);
    }
}