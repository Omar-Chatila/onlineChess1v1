package com.example.controller;

public class GameStates {
    private static boolean serverIswhite;
    private static boolean isMyTurn;
    private static boolean isServer;
    private static boolean gameOver;

    public static boolean isGameOver() {
        return gameOver;
    }

    public static void setGameOver(boolean gameOver) {
        GameStates.gameOver = gameOver;
    }

    public static boolean isServerWhite() {
        return serverIswhite;
    }

    public static void setServerIswhite(boolean w) {
        serverIswhite = w;
    }

    public static boolean isServer() {
        return isServer;
    }

    public static void setServer(boolean server) {
        isServer = server;
    }

    public static boolean isIsMyTurn() {
        return isMyTurn;
    }

    public static void setIsMyTurn(boolean isMyTurn) {
        GameStates.isMyTurn = isMyTurn;
    }
}
