package com.example.controller;

import Networking.Server;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import util.ApplicationData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("CallToPrintStackTrace")
public class ServerController implements Initializable {

    @FXML
    private AnchorPane chessBoardPane;
    @FXML
    private VBox tableBox;
    private static int serverPort;
    @FXML
    private Label roleLabel;
    @FXML
    private ToggleButton toggleButton;
    @FXML
    private StackPane stackpane;

    private static MovesTableController mtc;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Server server;
        try {
            server = new Server(new ServerSocket(serverPort));
            ApplicationData.getInstance().setServer(server);
        } catch (IOException e) {
            System.out.println("Error creating Server");
            throw new RuntimeException(e);
        }
        try {
            loadMovesTable();
            loadChessBoard();
            loadChatBox();
        } catch (Exception e) {
            e.printStackTrace();
        }
        roleLabel.setText("Chess - " + (GameStates.isServerWhite() ? "White" : "Black"));
        server.receiveMessageFromClient();
    }

    public static void setServerPort(int serverPort) {
        ServerController.serverPort = serverPort;
    }

    private void loadChessBoard() throws Exception {
        FXMLLoader loader;
        if (GameStates.isServerWhite()) {
            loader = new FXMLLoader(getClass().getResource("whiteBoard.fxml"));
        } else {
            loader = new FXMLLoader(getClass().getResource("blackBoard.fxml"));
        }
        GridPane gridPane = loader.load();
        chessBoardPane.getChildren().add(gridPane);
        AnchorPane.setTopAnchor(gridPane, 0.0);
        AnchorPane.setBottomAnchor(gridPane, 0.0);
        AnchorPane.setLeftAnchor(gridPane, 0.0);
        AnchorPane.setRightAnchor(gridPane, 0.0);
        ApplicationData.getInstance().setChessboardController(loader.getController());
    }

    private void loadChatBox() throws Exception {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("chatView.fxml"));
        AnchorPane chatPane = loader.load();
        stackpane.getChildren().add(chatPane);
        stackpane.getChildren().get(1).toBack();
    }

    private void loadMovesTable() throws Exception {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("movesTable.fxml"));
        VBox vBox = loader.load();
        mtc = loader.getController();
        tableBox.getChildren().add(vBox);
    }

    @FXML
    void toggle() {
        stackpane.getChildren().get(0).toFront();
        if (toggleButton.getText().equals("Chat")) {
            toggleButton.setText("Moves");
        } else {
            toggleButton.setText("Chat");
        }
    }

    public static MovesTableController getMtc() {
        return mtc;
    }
}