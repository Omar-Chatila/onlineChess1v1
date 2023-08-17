package Networking;

import chessModel.Game;
import com.example.controller.ClientController;
import com.example.controller.GameStates;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
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
                if (!messageToServer.matches("[0-9]{2}\\.[0-9]{2}")) {
                    Game.executeMove(messageToServer, !GameStates.isServerWhite());
                    System.out.println("Other King in check? " + Game.kingChecked(GameStates.isServerWhite()));
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

    public void receiveMessageFromServer(VBox vBox) {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String messageFromServer = bufferedReader.readLine();
                    if (messageFromServer.equals("true") || messageFromServer.equals("false")) {
                        GameStates.setServerIswhite(messageFromServer.equals("true"));
                    } else {
                        if (!messageFromServer.matches("[0-9]{2}\\.[0-9]{2}")) {
                            ApplicationData.getInstance().setIllegalMove(false);
                            Game.executeMove(messageFromServer, GameStates.isServerWhite());
                            System.out.println("My King in check? " + Game.kingChecked(!GameStates.isServerWhite()));
                            if (!ApplicationData.getInstance().isIllegalMove()) {
                                GameStates.setIsMyTurn(!GameStates.isIsMyTurn());
                            }
                            if (!ApplicationData.getInstance().isIllegalMove()) {
                                ClientController.addLabel(messageFromServer, vBox);
                            }
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    if (!ApplicationData.getInstance().isIllegalMove()) {
                                        ApplicationData.getInstance().getChessboardController().updateBoard(messageFromServer);
                                    }
                                }
                            });
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
