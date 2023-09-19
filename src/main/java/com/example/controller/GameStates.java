package com.example.controller;

import util.ApplicationData;

public class GameStates {
    private static boolean serverIswhite;
    private static boolean isMyTurn;
    private static boolean isServer;
    private static boolean gameOver;

    public static Integer getTimeControl() {
        return timeControl;
    }

    public static void setTimeControl(Integer timeControl) {
        GameStates.timeControl = timeControl;
    }

    private static Integer timeControl;

    public static boolean isGameOver() {
        return gameOver;
    }

    public static void setGameOver(boolean gameOver) {
        if (gameOver) ApplicationData.getInstance().closeTimers();
        GameStates.gameOver = gameOver;
    }

    public static boolean isServerWhite() {
        return serverIswhite;
    }

    public static void setServerIswhite(boolean w) {
        serverIswhite = w;
    }

    public static boolean iAmWhite() {
        return isServer && isServerWhite() || !isServer && !isServerWhite();
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
