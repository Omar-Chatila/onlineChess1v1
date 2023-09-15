package com.example.controller;

import chessModel.Game;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.util.Objects;

public class BlackGraveyardController {
    @FXML
    private Label bLabel;
    @FXML
    private ImageView bishopImage;
    @FXML
    private Label diffLabel;
    @FXML
    private ImageView knightImage;
    @FXML
    private Label nLabel;
    @FXML
    private Label pLabel;
    @FXML
    private ImageView pawnImage;
    @FXML
    private Label qLabel;
    @FXML
    private ImageView queenImage;
    @FXML
    private Label rLabel;
    @FXML
    private ImageView rookImage;

    @FXML
    private void initialize() {
        diffLabel.setAlignment(Pos.CENTER);
        queenImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bQG.png"))));
        rookImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bRG.png"))));
        bishopImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bBG.png"))));
        knightImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bNG.png"))));
        pawnImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bPG.png"))));
        queenImage.setVisible(false);
        rookImage.setVisible(false);
        bishopImage.setVisible(false);
        knightImage.setVisible(false);
        pawnImage.setVisible(false);
    }

    public void addPiece(String piece) {
        switch (piece) {
            case "Q" -> {
                this.queenImage.setVisible(true);
                int num = Game.getBlackGraveyard().count("Q");
                if (num > 1) qLabel.setGraphic(createIcon(num));
            }
            case "R" -> {
                this.rookImage.setVisible(true);
                int num = Game.getBlackGraveyard().count("R");
                if (num > 1) rLabel.setGraphic(createIcon(num));
            }
            case "B" -> {
                this.bishopImage.setVisible(true);
                int num = Game.getBlackGraveyard().count("B");
                if (num > 1) bLabel.setGraphic(createIcon(num));
            }
            case "N" -> {
                this.knightImage.setVisible(true);
                int num = Game.getBlackGraveyard().count("N");
                if (num > 1) nLabel.setGraphic(createIcon(num));
            }
            case "P" -> {
                this.pawnImage.setVisible(true);
                int num = Game.getBlackGraveyard().count("P");
                if (num > 1) pLabel.setGraphic(createIcon(num));
            }
        }
    }

    private StackPane createIcon(int number) {
        StackPane stackPane = new StackPane();
        Circle circle = new Circle(6);
        circle.setFill(Color.RED);
        Label label = new Label(number + "");
        label.setFont(Font.font("Arial", 11));
        label.setTextFill(Color.WHITE);
        stackPane.getChildren().addAll(circle, label);
        return stackPane;
    }

    public void updateDiffLabel() {
        if (Game.getMaterialDifference() > 0) {
            diffLabel.setText("+" + Math.abs(Game.getMaterialDifference()));
        } else {
            diffLabel.setText("");
        }
    }
}