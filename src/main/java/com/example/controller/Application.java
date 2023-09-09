package com.example.controller;

import chessModel.Game;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.GameHelper;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        GameHelper.initialize(Game.board);
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Chess-Game");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
