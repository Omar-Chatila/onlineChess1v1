package util;

import Networking.Client;
import Networking.Server;
import com.example.controller.*;

import java.util.Arrays;

public class ApplicationData {
    private static final ApplicationData instance = new ApplicationData();
    private Client client;
    private Server server;
    private ChessboardController chessboardController;
    private boolean illegalMove;
    private String promotedPiece;
    private InfoViewController ivc;
    private WhiteGraveyardController wgc;
    private BlackGraveyardController bgc;
    private ChessClock serverClock1;
    private ChessClock serverClock2;
    private ChessClock clientClock1;
    private ChessClock clientClock2;

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

    public String getPromotedPiece() {
        return promotedPiece;
    }

    public void setPromotedPiece(String promotedPiece) {
        this.promotedPiece = promotedPiece;
    }

    public InfoViewController getIvc() {
        return ivc;
    }

    public void setIvc(InfoViewController ivc) {
        this.ivc = ivc;
    }

    public WhiteGraveyardController getWgc() {
        return wgc;
    }

    public void setWgc(WhiteGraveyardController wgc) {
        this.wgc = wgc;
    }

    public BlackGraveyardController getBgc() {
        return this.bgc;
    }

    public void setBgc(BlackGraveyardController bgc) {
        this.bgc = bgc;
    }

    public void setServerClock1(ChessClock serverClock1) {
        this.serverClock1 = serverClock1;
    }

    public void setServerClock2(ChessClock serverClock2) {
        this.serverClock2 = serverClock2;
    }

    public void setClientClock1(ChessClock clientClock1) {
        this.clientClock1 = clientClock1;
    }

    public void setClientClock2(ChessClock clientClock2) {
        this.clientClock2 = clientClock2;
    }

    public void closeTimers() {
        for (ChessClock chessClock : Arrays.asList(serverClock1, serverClock2, clientClock1, clientClock2)) {
            if (chessClock != null)
                chessClock.pauseClock();
        }
    }
}
