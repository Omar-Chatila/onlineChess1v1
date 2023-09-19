package Networking;

import chessModel.Game;
import com.example.controller.ClientController;
import com.example.controller.GameStates;
import com.example.controller.LoginViewController;
import javafx.application.Platform;
import util.ApplicationData;
import util.ChessClock;

import java.io.*;
import java.net.Socket;

@SuppressWarnings("CallToPrintStackTrace")
public class Client {

    public void setCallback(ClientCallback callback) {
        this.callback = callback;
    }

    public interface ClientCallback {
        void onRoleReceived(boolean isServerWhite);
    }

    private void handleRoleInformation(boolean isServerWhite) {
        if (callback != null) {
            callback.onRoleReceived(isServerWhite);
        }
    }

    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private ChessClock serverClock;
    private ChessClock clientClock;
    private ClientCallback callback;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
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
                        clientClock.pauseClock();
                        if (Game.moveList.size() > 1)
                            serverClock.startTimer();
                        ApplicationData.getInstance().getIvc().toggleTurnIndicator();
                    }
                }
            }
            bufferedWriter.write(messageToServer);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void receiveMessageFromServer() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String messageFromServer = bufferedReader.readLine();
                    if (messageFromServer.startsWith("/cl")) {
                        GameStates.setTimeControl(Integer.parseInt(messageFromServer.substring(3)));
                        serverClock = new ChessClock(GameStates.getTimeControl(), false);
                        clientClock = new ChessClock(GameStates.getTimeControl(), true);
                        ApplicationData.getInstance().setClientClock1(serverClock);
                        ApplicationData.getInstance().setClientClock2(clientClock);
                    } else if (messageFromServer.startsWith("/t")) {
                        ClientController.getChatController().addLabel(messageFromServer.substring(2));
                        LoginViewController.getClientController().setMessageIndicatorVisibility(true);
                    } else {
                        if (messageFromServer.equals("true") || messageFromServer.equals("false")) {
                            GameStates.setServerIswhite(messageFromServer.equals("true"));
                            handleRoleInformation(messageFromServer.equals("true"));
                            if (!GameStates.isServerWhite()) {
                                GameStates.setIsMyTurn(true);
                            }
                        } else {
                            if (!messageFromServer.matches("[0-9]{2}\\.[0-9]{2}[A-R]?")) {
                                ApplicationData.getInstance().setIllegalMove(false);
                                Game.executeMove(messageFromServer, GameStates.isServerWhite());
                                if (!ApplicationData.getInstance().isIllegalMove()) {
                                    GameStates.setIsMyTurn(!GameStates.isIsMyTurn());
                                    serverClock.pauseClock();
                                    if (Game.moveList.size() > 1)
                                        clientClock.startTimer();
                                    ApplicationData.getInstance().getIvc().toggleTurnIndicator();
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
