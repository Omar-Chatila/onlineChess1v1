package com.example.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class BlackGraveyardController {
    @FXML
    private ImageView bishopImage;
    @FXML
    private HBox gyBox;
    @FXML
    private ImageView knightImage;
    @FXML
    private ImageView pawnImage;
    @FXML
    private ImageView queenImage;
    @FXML
    private ImageView rookImage;

    @FXML
    private void initialize() {
        queenImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bQ.png"))));
        rookImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bR.png"))));
        bishopImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bB.png"))));
        knightImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bN.png"))));
        pawnImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bP.png"))));
        queenImage.setVisible(false);
        rookImage.setVisible(false);
        bishopImage.setVisible(false);
        knightImage.setVisible(false);
        pawnImage.setVisible(false);
    }

    public void addPiece(String piece) {
        switch (piece) {
            case "Q" -> this.queenImage.setVisible(true);
            case "R" -> this.rookImage.setVisible(true);
            case "B" -> this.bishopImage.setVisible(true);
            case "N" -> this.knightImage.setVisible(true);
            case "P" -> this.pawnImage.setVisible(true);
        }
    }

}