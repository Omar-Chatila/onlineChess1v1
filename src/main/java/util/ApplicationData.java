package util;

import Networking.Client;
import Networking.Server;
import com.example.controller.ChessboardController;
import com.example.controller.GameStates;

public class ApplicationData {
    private static final ApplicationData instance = new ApplicationData();
    private Client client;
    private Server server;
    private ChessboardController chessboardController;
    private boolean illegalMove;
    private boolean whitePlaying;

    private ApplicationData() {

    }

    public static ApplicationData getInstance() {
        return instance;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public ChessboardController getChessboardController() {
        return chessboardController;
    }

    public void setChessboardController(ChessboardController chessboardController) {
        this.chessboardController = chessboardController;
    }

    public boolean isIllegalMove() {
        return illegalMove;
    }

    public void setIllegalMove(boolean illegalMove) {
        this.illegalMove = illegalMove;
    }

    public boolean isWhitePlaying() {
        return GameStates.iAmWhite();
    }
}
