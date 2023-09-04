package com.example.controller;

import Networking.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import util.ApplicationData;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("CallToPrintStackTrace")
public class ClientController implements Initializable {

    @FXML
    private AnchorPane chessBoardPane;
    @FXML
    private Label roleLabel;
    @FXML
    private VBox tableBox;
    private static String ip_Address;
    private static int portNr;
    private static MovesTableController mtc;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Client client = new Client(new Socket(ip_Address, portNr));
            ApplicationData.getInstance().setClient(client);
            System.out.println("Connected to server");
            client.receiveMessageFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            loadMovesTable();
            loadChessBoard();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            loadChessBoard();
        } catch (Exception e) {
            e.printStackTrace();
        }
        roleLabel.setText("Chess - " + (!GameStates.isServerWhite() ? "White" : "Black"));
    }

    public static void setIp_Address(String ip_Address) {
        ClientController.ip_Address = ip_Address;
    }

    public static void setPortNr(int portNr) {
        ClientController.portNr = portNr;
    }

    private void loadChessBoard() throws Exception {
        FXMLLoader loader;
        System.out.println("Main is white?" + GameStates.isServerWhite());
        if (GameStates.isServerWhite()) {
            loader = new FXMLLoader(getClass().getResource("blackBoard.fxml"));
        } else {
            loader = new FXMLLoader(getClass().getResource("whiteBoard.fxml"));
        }
        GridPane gridPane = loader.load();
        // Set the loaded GridPane as a child of the AnchorPane
        chessBoardPane.getChildren().add(gridPane);
        // Adjust the size of the GridPane to fill the AnchorPane
        AnchorPane.setTopAnchor(gridPane, 0.0);
        AnchorPane.setBottomAnchor(gridPane, 0.0);
        AnchorPane.setLeftAnchor(gridPane, 0.0);
        AnchorPane.setRightAnchor(gridPane, 0.0);
        ApplicationData.getInstance().setChessboardController(loader.getController());
    }

    private void loadMovesTable() throws Exception {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("movesTable.fxml"));
        VBox vBox = loader.load();
        mtc = loader.getController();
        tableBox.getChildren().add(vBox);
    }

    public static MovesTableController getMtc() {
        return mtc;
    }
}