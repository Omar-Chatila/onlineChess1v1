package Networking;

import chessModel.Game;
import com.example.controller.ClientController;
import com.example.controller.GameStates;
import com.example.controller.LoginViewController;
import javafx.application.Platform;
import util.ApplicationData;

import java.io.*;
import java.net.Socket;

@SuppressWarnings("CallToPrintStackTrace")
public class Client {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Error creating client");
            e.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessageToServer(String messageToServer) {
        try {
            if (GameStates.isIsMyTurn()) {
                if (!messageToServer.matches("[0-9]{2}\\.[0-9]{2}[A-Q]?") && !messageToServer.startsWith("/t")) {
                    Game.executeMove(messageToServer, !GameStates.isServerWhite());
                    if (!ApplicationData.getInstance().isIllegalMove()) {
                        GameStates.setIsMyTurn(!GameStates.isIsMyTurn());
                    }
                }
            }
            bufferedWriter.write(messageToServer);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            System.out.println("Error sending message to client");
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void receiveMessageFromServer() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String messageFromServer = bufferedReader.readLine();
                    if (messageFromServer.startsWith("/t")) {
                        ClientController.getChatController().addLabel(messageFromServer.substring(2));
                        LoginViewController.getClientController().setMessageIndicatorVisibility(true);
                    } else {
                        if (messageFromServer.equals("true") || messageFromServer.equals("false")) {
                            GameStates.setServerIswhite(messageFromServer.equals("true"));
                        } else {
                            if (!messageFromServer.matches("[0-9]{2}\\.[0-9]{2}[A-Q]?")) {
                                ApplicationData.getInstance().setIllegalMove(false);
                                Game.executeMove(messageFromServer, GameStates.isServerWhite());
                                if (!ApplicationData.getInstance().isIllegalMove()) {
                                    GameStates.setIsMyTurn(!GameStates.isIsMyTurn());
                                }
                            } else {
                                Platform.runLater(() -> {
                                    if (!ApplicationData.getInstance().isIllegalMove()) {
                                        ApplicationData.getInstance().getChessboardController().updateBoard(messageFromServer);
                                    }
                                });
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error receiving message from the client");
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
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
            e.printStackTrace();
        }
    }
}
