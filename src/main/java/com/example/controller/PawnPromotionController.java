package com.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.ApplicationData;

import java.util.Objects;

public class PawnPromotionController {
    @FXML
    private ImageView knightButton;
    @FXML
    private ImageView queenButton;
    @FXML
    private ImageView rookButton;
    @FXML
    private ImageView bishopButton;
    @FXML
    private VBox selectionBox;
    @FXML
    private Button closeButton;

    @FXML
    private void initialize() {
        if (ApplicationData.getInstance().isWhitePlaying()) {
            queenButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/wQ.png"))));
            knightButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/wN.png"))));
            rookButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/wR.png"))));
            bishopButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/wB.png"))));
            queenButton.setOnMouseClicked(e -> System.out.println("Queen Promotion"));
        }
    }

    @FXML
    void close() {
        Stage stage = (Stage) selectionBox.getScene().getWindow();
        stage.close();
    }
}