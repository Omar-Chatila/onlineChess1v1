package com.example.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import util.ApplicationData;

public class InfoViewController {
    public static boolean doubleClicked;
    @FXML
    private Label myName;
    @FXML
    private Label myTimeLabel;
    @FXML
    private JFXRadioButton myTurnIndicator;
    @FXML
    private Label oppTimeLabel;
    @FXML
    private JFXRadioButton oppTurnIndicator;
    @FXML
    private Label infoText;
    @FXML
    private Label opponentName;
    @FXML
    private HBox myTimeBox;
    @FXML
    private HBox opptimeBox;
    @FXML
    private JFXButton resignButton;
    @FXML
    private JFXButton offerDraw;
    @FXML
    private FontAwesomeIcon myWinEmblem;
    @FXML
    private FontAwesomeIcon oppWinEmblem;
    private boolean iamWhite;

    public static final String REQUEST_DRAW = "/rdraw";
    public static final String RESIGN = "/resign";
    public static final String ACCCEPT_DRAW = "/adraw";

    private static void resign() {

    }

    private static void sendMessage(String text) {
        if (GameStates.isServer()) {
            ApplicationData.getInstance().getServer().sendMessageToClient(text);
        } else {
            ApplicationData.getInstance().getClient().sendMessageToServer(text);
        }
    }

    @FXML
    private void initialize() {
        setResignButton();
        //setOfferDrawButton();
        myTurnIndicator.setDisable(true);
        oppTurnIndicator.setDisable(true);
        myTurnIndicator.setOpacity(1);
        oppTurnIndicator.setOpacity(1);
        System.out.println(GameStates.getTimeControl() / 60);
        myTimeLabel.setText((GameStates.getTimeControl() / 60) + ":00");
        oppTimeLabel.setText((GameStates.getTimeControl() / 60) + ":00");
        if (GameStates.iAmWhite()) {
            this.iamWhite = true;
            myTurnIndicator.setSelected(true);
            oppTurnIndicator.setSelected(false);
            myName.setText("White Player");
            opponentName.setText("Black Player");
            infoText.setText("You play the white pieces. It's your turn!");
            oppTimeLabel.getStyleClass().add("black-label");
            myTimeLabel.getStyleClass().add("white-label");
            opptimeBox.setStyle("-fx-background-color: black");
            myTimeBox.setStyle("-fx-background-color: white");
            myWinEmblem.setFill(Color.BLACK);
            oppWinEmblem.setFill(Color.WHITE);
        } else {
            this.iamWhite = false;
            myTurnIndicator.setSelected(false);
            oppTurnIndicator.setSelected(true);
            myName.setText("Black Player");
            opponentName.setText("White Player");
            infoText.setText("You play the black pieces! It's your opponents turn");
            oppTimeLabel.getStyleClass().add("white-label");
            myTimeLabel.getStyleClass().add("black-label");
            myTimeBox.setStyle("-fx-background-color: black");
            opptimeBox.setStyle("-fx-background-color: white");
            myWinEmblem.setFill(Color.WHITE);
            oppWinEmblem.setFill(Color.BLACK);
        }
    }

    public void toggleTurnIndicator() {
        myTurnIndicator.setSelected(!myTurnIndicator.isSelected());
        oppTurnIndicator.setSelected(!oppTurnIndicator.isSelected());
    }

    private void setResignButton() {
        setResignButton(resignButton);
    }

    //private void setOfferDrawButton() {
    //buttonEffects(offerDraw);
    //}

    private void setResignButton(JFXButton button) {
        DropShadow dropShadow = new DropShadow();
        button.setEffect(dropShadow);
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        scaleTransition.setToZ(1.1);
        scaleTransition.setAutoReverse(true);
        button.setOnAction(event -> {
            if (doubleClicked) {
                if (this.iamWhite) {
                    infoText.setText("White resigned - Black is victorious!");
                } else {
                    infoText.setText("Black resigned - White is victorious!");
                }
                ApplicationData.getInstance().closeTimers();
                GameStates.setGameOver(true);
                sendMessage(RESIGN);
                disableButtons();
                oppWinEmblem.setVisible(true);
                button.setStyle("-fx-background-color: #d32f2f;");
                button.setStyle("-fx-background-color: #0d90dc;");
                if (scaleTransition.getStatus() == ScaleTransition.Status.RUNNING) {
                    scaleTransition.stop();
                }
                scaleTransition.play();
            } else {
                button.setStyle("-fx-background-color: #d32f2f;");
                doubleClicked = true;
            }
        });

        button.setOnMouseExited(event -> {
            doubleClicked = false;
            button.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0, 0, 1);" +
                    "-fx-background-color: transparent;");
            button.setScaleX(1);
            button.setScaleY(1);
            button.setScaleZ(1);
        });
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: #d3b22f;"));
    }

    public void updateMyClock(String time) {
        myTimeLabel.setText(time);
    }

    public void disableButtons() {
        this.resignButton.setDisable(true);
        this.offerDraw.setDisable(true);
    }

    public void updateOppClock(String time) {
        oppTimeLabel.setText(time);
    }

    public void updateInfoText(String message) {
        Platform.runLater(() -> infoText.setText(message));
    }

    public void showWinner(boolean white) {
        if (white) {
            if (this.iamWhite) {
                myWinEmblem.setVisible(true);
            } else {
                oppWinEmblem.setVisible(true);
            }
        } else {
            if (this.iamWhite) {
                oppWinEmblem.setVisible(true);
            } else {
                myWinEmblem.setVisible(true);
            }
        }
    }

    public void setEmblems(boolean mine, boolean opp) {
        this.myWinEmblem.setVisible(true);
        this.oppWinEmblem.setVisible(opp);
    }
}
