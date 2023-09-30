package Networking;

import Exceptions.IllegalMoveException;
import chessModel.Game;
import com.example.controller.GameStates;
import com.example.controller.InfoViewController;
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

    static boolean soundPlayed;
    private final ChessClock serverClock;
    private final ChessClock clientClock;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Server(ServerSocket serverSocket) {
        serverClock = new ChessClock(GameStates.getTimeControl(), true);
        clientClock = new ChessClock((GameStates.getTimeControl()), false);
        ApplicationData.getInstance().setServerClock1(serverClock);
        ApplicationData.getInstance().setServerClock2(clientClock);
        try {
            this.socket = serverSocket.accept();
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write(GameStates.isServerWhite() + "");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessageToClient(String messageToClient) {
        try {
            if (GameStates.isIsMyTurn()) {
                if (!messageToClient.matches("[0-9]{2}\\.[0-9]{2}[A-Q]?") && !messageToClient.startsWith("/")) {
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
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

    }

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
                    } else if (messageFromClient.startsWith(InfoViewController.REQUEST_DRAW)) {
                        Platform.runLater(() -> {
                            Game.drawClaimable = true;
                            ApplicationData.getInstance().getIvc().updateInfoText("Opponent offers a draw");
                            ApplicationData.getInstance().getIvc().highlightDrawButton();
                        });
                    } else if (messageFromClient.startsWith(InfoViewController.ACCCEPT_DRAW)) {
                        GameStates.setGameOver(true);
                        Platform.runLater(() -> {
                            InfoViewController ivc = ApplicationData.getInstance().getIvc();
                            ivc.updateInfoText("Game ends in a Draw");
                            ivc.setEmblems(true, true);
                            ivc.disableButtons();
                            ivc.clearDrawButtonStyle();
                            ApplicationData.getInstance().closeTimers();
                        });
                    } else if (messageFromClient.equals(InfoViewController.RESIGN)) {
                        GameStates.setGameOver(true);
                        Platform.runLater(() -> {
                            InfoViewController ivc = ApplicationData.getInstance().getIvc();
                            ivc.showWinner(GameStates.iAmWhite());
                            ivc.updateInfoText(GameStates.iAmWhite() ?
                                    "Black resigned - White is victorious"
                                    : "White resigned - Black is victorious");
                            ivc.disableButtons();
                        });
                        new SoundPlayer().playGameEndSound();
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
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                } catch (IllegalMoveException e) {
                    new SoundPlayer().playIllegalMoveSound();
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
