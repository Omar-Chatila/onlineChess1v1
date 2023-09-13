package util;

import Networking.Client;
import Networking.Server;
import com.example.controller.ChessboardController;
import com.example.controller.GameStates;
import com.example.controller.InfoViewController;

public class ApplicationData {
    private static final ApplicationData instance = new ApplicationData();
    private Client client;
    private Server server;
    private ChessboardController chessboardController;
    private boolean illegalMove;
    private String promotedPiece;
    private InfoViewController ivc;

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

}
