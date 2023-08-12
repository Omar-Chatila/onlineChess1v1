package com.example.messengerserver;

import chessModel.Game;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static boolean white;


    private static boolean isMyTurn;
    private boolean isServer;

    @Override
    public void start(Stage stage) throws IOException {
        Game.initialize(Game.board);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Chess-Game");
        stage.setScene(scene);
        stage.show();
    }

    public static boolean isWhite() {
        return white;
    }

    public static void setWhite(boolean w) {
        white = w;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        isServer = server;
    }

    public static boolean isIsMyTurn() {
        return isMyTurn;
    }

    public static void setIsMyTurn(boolean isMyTurn) {
        Main.isMyTurn = isMyTurn;
    }

    public static void main(String[] args) {
        launch();
    }
}
