package com.example.controller;

public class GameStates {
    private static boolean serverIswhite;
    static boolean isMyTurn;
    private static boolean isServer;

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
