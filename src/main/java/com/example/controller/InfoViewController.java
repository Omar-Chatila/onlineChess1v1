package com.example.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;

public class InfoViewController {
    @FXML
    private Label myName;
    @FXML
    private Label myTimeLabel;
    @FXML
    private RadioButton myTurnIndicator;
    @FXML
    private Label oppTimeLabel;
    @FXML
    private RadioButton oppTurnIndicator;
    @FXML
    private Label infoText;
    @FXML
    private Label opponentName;

    @FXML
    private void initialize() {
        if (GameStates.iAmWhite()) {
            myTurnIndicator.setSelected(true);
            oppTurnIndicator.setSelected(false);
            myName.setText("White Player");
            opponentName.setText("Black Player");
            infoText.setText("You play the white pieces                                 it's your turn!");
        } else {
            myTurnIndicator.setSelected(false);
            oppTurnIndicator.setSelected(true);
            myName.setText("Black Player");
            opponentName.setText("White Player");
            infoText.setText("You play the black pieces                                 it's your opponents turn");
        }

    }

    public void toggleTurnIndicator() {
        myTurnIndicator.setSelected(!myTurnIndicator.isSelected());
        oppTurnIndicator.setSelected(!oppTurnIndicator.isSelected());
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

}
