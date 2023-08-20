package Networking;

import chessModel.Game;
import com.example.controller.GameStates;
import com.example.controller.ServerController;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import util.ApplicationData;

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
            bufferedWriter.write(GameStates.isServerWhite() + "");
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
        try {
            if (GameStates.isIsMyTurn()) {
                if (!messageToClient.matches("[0-9]{2}\\.[0-9]{2}")) {
                    System.out.println("Wie oft?");
                    Game.executeMove(messageToClient, GameStates.isServerWhite());
                    if (!ApplicationData.getInstance().isIllegalMove()) {
                        GameStates.setIsMyTurn(!GameStates.isIsMyTurn());
                    }
                }
            }
            bufferedWriter.write(messageToClient);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            System.out.println("Error sending message to client");
            closeEverything(socket, bufferedReader, bufferedWriter);
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
                        if (!messageFromClient.matches("[0-9]{2}\\.[0-9]{2}")) {
                            ApplicationData.getInstance().setIllegalMove(false);
                            Game.executeMove(messageFromClient, !GameStates.isServerWhite());
                            if (!ApplicationData.getInstance().isIllegalMove()) {
                                ServerController.addLabel(messageFromClient, vBox);
                            }
                            if (!ApplicationData.getInstance().isIllegalMove()) {
                                GameStates.setIsMyTurn(!GameStates.isIsMyTurn());
                            }
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    if (!ApplicationData.getInstance().isIllegalMove()) {
                                        ApplicationData.getInstance().getChessboardController().updateBoard(messageFromClient);
                                    }
                                }
                            });
                        }
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