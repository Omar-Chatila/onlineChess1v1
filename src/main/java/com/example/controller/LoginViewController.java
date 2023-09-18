package com.example.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSlider;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.StageMover;

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
    @FXML
    private Label infoLabel;
    @FXML
    private JFXRadioButton clientRadioButton;
    @FXML
    private Hyperlink helpLink;
    private ScaleTransition scaleTransition;

    private static ServerController serverController;
    private static ClientController clientController;


    @FXML
    private void initialize() {
        setTimeSlider();
        closeButton.setOnAction(e -> Platform.exit());
        helpLink.setBorder(Border.EMPTY);
        portField.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) connectButton.fire();
        });
    }

    private void setTimeSlider() {
        timeLabel.setText("10:00");
        GameStates.setTimeControl(10 * 60);
        timeSlider.setMin(1);
        timeSlider.setMax(30);
        timeSlider.setValue(10);
        timeSlider.setBlockIncrement(1);

        timeSlider.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    int tc = (int) Math.ceil(newValue.doubleValue());
                    GameStates.setTimeControl(tc * 60);
                    timeLabel.setText(tc + ":00");
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
        System.out.println("connect");
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
                            Pane root = loader.getRoot();
                            new StageMover(root, window);
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
                            Pane root = loader.getRoot();
                            new StageMover(root, window);
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
        if (selected.getText().equals("Join")) {
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

    @FXML
    void mouseEntered(MouseEvent event) {
        if (event.getSource() instanceof Button hovered) {
            switch (hovered.getAccessibleText()) {
                case "white" -> {
                    infoLabel.setText("Play with the White Pieces!");
                    playScaleAnimation(whitePieceColor);
                }
                case "black" -> {
                    infoLabel.setText("Play with the Black Pieces!");
                    playScaleAnimation(blackPieceColor);
                }
                default -> {
                    infoLabel.setText("Play with a Random color!");
                    playScaleAnimation(randomPieceColor);
                }
            }
        } else if (event.getSource() instanceof JFXRadioButton radioButton) {
            if (radioButton.getText().equals("Create")) {
                infoLabel.setText("Create a new Game");
            } else {
                infoLabel.setText("Join an existing game");
            }
        } else {
            TextField hovered = (TextField) event.getSource();
            if (hovered.getAccessibleText().equals("Port")) {
                if (clientRadioButton.isSelected()) {
                    infoLabel.setText("Enter the Host's Port");
                } else {
                    infoLabel.setText("Set Port");
                }
            } else {
                if (clientRadioButton.isSelected()) {
                    infoLabel.setText("Enter the Host's IP-Address");
                }
            }
        }
    }

    @FXML
    void mouseExited() {
        infoLabel.setText("");
        if (scaleTransition != null)
            scaleTransition.stop();
        whitePieceColor.setScaleX(1.0);
        whitePieceColor.setScaleY(1.0);
        whitePieceColor.setScaleZ(1.0);
        blackPieceColor.setScaleX(1.0);
        blackPieceColor.setScaleY(1.0);
        blackPieceColor.setScaleZ(1.0);
        randomPieceColor.setScaleX(1);
        randomPieceColor.setScaleY(1);
        randomPieceColor.setScaleZ(1);
    }

    @FXML
    void minimize() {
        Stage stage = (Stage) infoLabel.getScene().getWindow();
        stage.setIconified(true);
        ipField.getParent().requestFocus();
    }

    private void playScaleAnimation(Button button) {
        scaleTransition = new ScaleTransition(Duration.seconds(0.5), button);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setCycleCount(ScaleTransition.INDEFINITE);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }

    @FXML
    void openGithub() {
        Stage helpStage = new Stage();
        WebView webView = new WebView();
        webView.getEngine().load("https://github.com/Omar-Chatila/onlineChess1v1/blob/main/README.md");
        Scene scene = new Scene(webView, 800, 600);
        helpStage.setScene(scene);
        helpStage.show();
    }
}