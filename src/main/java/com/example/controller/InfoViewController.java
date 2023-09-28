package com.example.controller;

import chessModel.Game;
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
    private static boolean doubleClicked;
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

    private static void requestDraw() {
        if (GameStates.isServer()) {
            ApplicationData.getInstance().getServer().sendMessageToClient("/rdraw");
        } else {
            ApplicationData.getInstance().getClient().sendMessageToServer("/rdraw");
        }
    }

    private static void acceptDraw() {
        if (GameStates.isServer()) {
            ApplicationData.getInstance().getServer().sendMessageToClient("/adraw");
        } else {
            ApplicationData.getInstance().getClient().sendMessageToServer("/adraw");
        }
    }

    @FXML
    private void initialize() {
        setResignButton();
        setOfferDrawButton();
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
        buttonEffects(resignButton, true);
    }

    private void setOfferDrawButton() {
        buttonEffects(offerDraw, false);
    }

    private void buttonEffects(JFXButton button, boolean resign) {
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
                    if (resign) {
                        infoText.setText("You resigned - Black is victorious!");
                    } else {
                        if (Game.drawClaimable) {
                            infoText.setText("Game ends in a Draw!");
                            acceptDraw();
                            GameStates.setGameOver(true);
                        }
                    }
                } else {
                    if (resign)
                        infoText.setText("You resigned - White is victorious!");
                    else {
                        if (Game.drawClaimable) {
                            acceptDraw();
                            infoText.setText("Game ends in a Draw!");
                            GameStates.setGameOver(true);
                        }
                    }
                }
                ApplicationData.getInstance().closeTimers();
                if (resign)
                    GameStates.setGameOver(true);
                resignButton.setDisable(true);
                offerDraw.setDisable(true);
                oppWinEmblem.setVisible(true);
                if (!resign)
                    myWinEmblem.setVisible(true);
            } else {
                requestDraw();
            }
            if (resign)
                button.setStyle("-fx-background-color: #d32f2f;");
            else
                button.setStyle("-fx-background-color: #0d90dc;");
            if (scaleTransition.getStatus() == ScaleTransition.Status.RUNNING) {
                scaleTransition.stop();
            }
            scaleTransition.play();
            doubleClicked = true;
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

    public JFXButton getOfferDraw() {
        return this.offerDraw;
    }
}
