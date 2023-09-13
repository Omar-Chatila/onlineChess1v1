package Networking;

import chessModel.Game;
import com.example.controller.GameStates;
import com.example.controller.LoginViewController;
import com.example.controller.ServerController;
import javafx.application.Platform;
import util.ApplicationData;
import util.ChessClock;
import util.SoundPlayer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private final ChessClock serverClock;
    private final ChessClock clientClock;

    public Server(ServerSocket serverSocket) {
        serverClock = new ChessClock(GameStates.getTimeControl(), true);
        clientClock = new ChessClock((GameStates.getTimeControl()), false);
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
                if (!messageToClient.matches("[0-9]{2}\\.[0-9]{2}[A-Q]?") && !messageToClient.startsWith("/t") && !messageToClient.startsWith("/cl")) {
                    Game.executeMove(messageToClient, GameStates.isServerWhite());
                    if (!ApplicationData.getInstance().isIllegalMove()) {
                        GameStates.setIsMyTurn(!GameStates.isIsMyTurn());
                        serverClock.pauseClock();
                        if (Game.moveList.size() > 1)
                            clientClock.startTimer();
                        ApplicationData.getInstance().getIvc().toggleTurnIndicator();
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

    static boolean soundPlayed;

    public void receiveMessageFromClient() {
        new Thread(() -> {
            while (socket.isConnected()) {
                if (!soundPlayed) {
                    soundPlayed = true;
                    new SoundPlayer().playGameStartSound();
                    ApplicationData.getInstance().getServer().sendMessageToClient("/cl" + GameStates.getTimeControl());
                }
                try {
                    String messageFromClient = bufferedReader.readLine();
                    if (messageFromClient.startsWith("/t")) {
                        ServerController.getChatController().addLabel(messageFromClient.substring(2));
                        LoginViewController.getServerController().setMessageIndicatorVisibility(true);
                    } else {
                        if (!messageFromClient.matches("[0-9]{2}\\.[0-9]{2}[A-R]?")) {
                            ApplicationData.getInstance().setIllegalMove(false);
                            Game.executeMove(messageFromClient, !GameStates.isServerWhite());
                            if (!ApplicationData.getInstance().isIllegalMove()) {
                                GameStates.setIsMyTurn(!GameStates.isIsMyTurn());
                                if (Game.moveList.size() > 1)
                                    serverClock.startTimer();
                                clientClock.pauseClock();
                                ApplicationData.getInstance().getIvc().toggleTurnIndicator();
                            }
                        } else {
                            Platform.runLater(() -> {
                                if (!ApplicationData.getInstance().isIllegalMove()) {
                                    ApplicationData.getInstance().getChessboardController().updateBoard(messageFromClient);
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
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    public boolean hasConnection() {
        return this.socket.isConnected();
    }

}
