package com.example.messengerserver;

import chessModel.Game;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Server(ServerSocket serverSocket) {
        try {
            this.socket = serverSocket.accept();
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write(Main.isWhite() + "");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("Error creating server.");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessageToClient(String messageToClient) {
        if (Main.isIsMyTurn()) {
            try {
                Game.executeMove(messageToClient, Main.isWhite());
                bufferedWriter.write(messageToClient);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                Main.setIsMyTurn(!Main.isIsMyTurn());
            } catch (IOException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
                System.out.println("Error sending message to client");
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void receiveMessageFromClient(VBox vBox) {
        new Thread(new Runnable() {
            @SuppressWarnings("CallToPrintStackTrace")
            @Override
            public void run() {
                while (socket.isConnected()) {
                    try {
                        String messageFromClient = bufferedReader.readLine();
                        Game.executeMove(messageFromClient, !Main.isWhite());
                        ServerController.addLabel(messageFromClient, vBox);
                        Main.setIsMyTurn(!Main.isIsMyTurn());
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error receiving message from the client");
                        closeEverything(socket, bufferedReader, bufferedWriter);
                        break;
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

}
