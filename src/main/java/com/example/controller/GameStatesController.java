package com.example.controller;

import chessModel.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import themes.Theme;
import util.ApplicationData;
import util.GameHelper;

import java.util.Objects;

public class GameStatesController {

    private Theme theme;
    @FXML
    private GridPane chessboardGrid;

    @FXML
    private void initialize() {
        this.theme = ApplicationData.getInstance().getTheme();
        initBoard(0);
    }

    public void initBoard(int posNumber) {
        GameHelper.print(Game.playedPositions.get(posNumber));
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Label label = new Label();
                label.setPrefWidth(50);
                label.setPrefHeight(50);
                label.setMaxWidth(50);
                label.setMaxHeight(50);
                String style = ((i + j) % 2 == 0) ? theme.getLightPastStyle() : theme.getDarkPastStyle();
                label.setStyle(style);
                String imageName = getImageName(i, j, posNumber);
                if (!imageName.isBlank()) {
                    Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + imageName + ".png")));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    label.setGraphic(imageView);
                }
                chessboardGrid.add(label, j, i);

            }
        }
    }

    private static String getImageName(int i, int j, int posNumber) {
        String imageName = "";
        switch (Game.playedPositions.get(posNumber)[i][j]) {
            case "p" -> imageName = "bP";
            case "n" -> imageName = "bN";
            case "b" -> imageName = "bB";
            case "r" -> imageName = "bR";
            case "q" -> imageName = "bQ";
            case "k" -> imageName = "bK";
            case "P" -> imageName = "wP";
            case "N" -> imageName = "wN";
            case "B" -> imageName = "wB";
            case "R" -> imageName = "wR";
            case "Q" -> imageName = "wQ";
            case "K" -> imageName = "wK";
        }
        return imageName;
    }
}
