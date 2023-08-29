package com.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.ApplicationData;

import java.util.Objects;

public class PawnPromotionController {
    @FXML
    private HBox bishopBox;
    @FXML
    private ImageView bishopButton;
    @FXML
    private Button closeButton;
    @FXML
    private HBox knightBox;
    @FXML
    private ImageView knightButton;
    @FXML
    private HBox queenBox;
    @FXML
    private ImageView queenButton;
    @FXML
    private HBox rookBox;
    @FXML
    private ImageView rookButton;
    @FXML
    private VBox selectionBox;

    @FXML
    private void initialize() {
        if (ApplicationData.getInstance().isWhitePlaying()) {
            queenButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/wQ.png"))));
            knightButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/wN.png"))));
            rookButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/wR.png"))));
            bishopButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/wB.png"))));
            queenButton.setOnMouseClicked(e -> System.out.println("Queen Promotion"));
        } else {
            queenButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bQ.png"))));
            knightButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bN.png"))));
            rookButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bR.png"))));
            bishopButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bB.png"))));
        }
        setStyles();
    }

    private void setStyles() {
        queenButton.setOnMouseEntered(e -> mouseHovered(queenBox));
        rookButton.setOnMouseEntered(e -> mouseHovered(rookBox));
        knightButton.setOnMouseEntered(e -> mouseHovered(knightBox));
        bishopButton.setOnMouseEntered(e -> mouseHovered(bishopBox));
        closeButton.setOnMouseEntered(e -> closeButton.setStyle("fx-background-color: #d6566b"));

        queenButton.setOnMouseExited(e -> mouseExited(queenBox));
        rookButton.setOnMouseExited(e -> mouseExited(rookBox));
        knightButton.setOnMouseExited(e -> mouseExited(knightBox));
        bishopButton.setOnMouseExited(e -> mouseExited(bishopBox));
        closeButton.setOnMouseExited(e -> closeButton.setStyle("-fx-background-color: transparent"));
    }

    private void mouseHovered(HBox hBox) {
        hBox.setStyle("-fx-background-color: #79b389");
    }

    private void mouseExited(HBox hBox) {
        hBox.setStyle("-fx-background-color: transparent");
    }

    @FXML
    void close() {
        Stage stage = (Stage) selectionBox.getScene().getWindow();
        stage.close();
    }
}