package com.example.controller;

import chessModel.Game;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.GameHelper;

import java.io.IOException;

public class Application extends javafx.application.Application {
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws IOException {
        GameHelper.initialize(Game.board);
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Pane root = fxmlLoader.getRoot();
        stage.setTitle("Chess-Game");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
