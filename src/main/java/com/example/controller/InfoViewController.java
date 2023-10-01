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
import util.SoundPlayer;

public class InfoViewController {
    public static boolean doubleClicked;

    @FXML
    private JFXButton addOppTime;
    @FXML
    private Label infoText;
    @FXML
    private Label myName;
    @FXML
    private HBox myTimeBox;
    @FXML
    private Label myTimeLabel;
    @FXML
    private JFXRadioButton myTurnIndicator;
    @FXML
    private FontAwesomeIcon myWinEmblem;
    @FXML
    private JFXButton offerDraw;
    @FXML
    private FontAwesomeIcon oppPlus;
    @FXML
    private Label oppTimeLabel;
    @FXML
    private JFXRadioButton oppTurnIndicator;
    @FXML
    private FontAwesomeIcon oppWinEmblem;
    @FXML
    private Label opponentName;
    @FXML
    private HBox opptimeBox;
    @FXML
    private JFXButton resignButton;
    private boolean iamWhite;

    public static final String REQUEST_DRAW = "/rdraw";
    public static final String RESIGN = "/resign";
    public static final String ACCCEPT_DRAW = "/adraw";

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
        setOfferDrawButton();
        addOppTime.setOnAction(e -> {
            if (GameStates.isServer()) {
                ApplicationData.getInstance().getServerClock2().addTime();
            } else {
                ApplicationData.getInstance().getClientClock1().addTime();
            }
            sendMessage("/pt");
        });

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
            oppPlus.setGlyphName("PLUS_SQUARE_ALT");
            oppPlus.setFill(Color.WHITE);
            myWinEmblem.setFill(Color.BLACK);
            oppWinEmblem.setFill(Color.WHITE);
        } else {
            this.iamWhite = false;
            myTurnIndicator.setSelected(false);
            oppTurnIndicator.setSelected(true);
            myName.setText("Black Player");
            opponentName.setText("White Player");
            oppPlus.setGlyphName("PLUS_SQUARE");
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

    private void setOfferDrawButton() {
        offerDraw.setOnAction(event -> {
            if (Game.drawClaimable) {
                sendMessage(ACCCEPT_DRAW);
                ApplicationData.getInstance().closeTimers();
                GameStates.setGameOver(true);
                new SoundPlayer().playGameEndSound();
                infoText.setText("Game ends in a Draw!");
                setEmblems(true, true);
                clearDrawButtonStyle();
                disableButtons();
            } else {
                highlightDrawButton();
                sendMessage(REQUEST_DRAW);
            }
        });
    }

    public void highlightDrawButton() {
        DropShadow dropShadow = new DropShadow();
        offerDraw.setEffect(dropShadow);
        offerDraw.setStyle("-fx-background-color: #0d90dc");
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), offerDraw);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        scaleTransition.setToZ(1.1);
        scaleTransition.setAutoReverse(true);
        if (scaleTransition.getStatus() == ScaleTransition.Status.RUNNING) {
            scaleTransition.stop();
        }
        scaleTransition.play();
    }

    public void clearDrawButtonStyle() {
        offerDraw.setStyle("");
        offerDraw.setScaleX(1);
        offerDraw.setScaleY(1);
        offerDraw.setScaleZ(1);
    }

    private void setResignButton() {
        DropShadow dropShadow = new DropShadow();
        resignButton.setEffect(dropShadow);
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), resignButton);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        scaleTransition.setToZ(1.1);
        scaleTransition.setAutoReverse(true);
        resignButton.setOnAction(event -> {
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
                resignButton.setStyle("-fx-background-color: #d32f2f;");
                resignButton.setStyle("-fx-background-color: #0d90dc;");
                if (scaleTransition.getStatus() == ScaleTransition.Status.RUNNING) {
                    scaleTransition.stop();
                }
                scaleTransition.play();
            } else {
                resignButton.setStyle("-fx-background-color: #d32f2f;");
                doubleClicked = true;
            }
        });

        resignButton.setOnMouseExited(event -> {
            doubleClicked = false;
            resignButton.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0, 0, 1);" +
                    "-fx-background-color: transparent;");
            resignButton.setScaleX(1);
            resignButton.setScaleY(1);
            resignButton.setScaleZ(1);
        });
        resignButton.setOnMouseEntered(event -> resignButton.setStyle("-fx-background-color: #d3b22f;"));
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
        this.myWinEmblem.setVisible(mine);
        this.oppWinEmblem.setVisible(opp);
    }
}
