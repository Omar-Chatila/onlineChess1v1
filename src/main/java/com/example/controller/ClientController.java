package com.example.controller;

import Networking.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import util.ApplicationData;
import util.SoundPlayer;

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
    @FXML
    private ToggleButton toggleButton;
    @FXML
    private StackPane stackpane;
    @FXML
    private ImageView newMessage;
    @FXML
    private AnchorPane infopane;
    @FXML
    private AnchorPane oppGraveyardPane;
    @FXML
    private AnchorPane myGraveYardPane;

    private static String ip_Address;
    private static int portNr;
    private static MovesTableController mtc;
    private static ChatController chatController;

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
            loadUIElements();
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
        if (GameStates.isServerWhite()) {
            loader = new FXMLLoader(getClass().getResource("blackBoard.fxml"));
        } else {
            loader = new FXMLLoader(getClass().getResource("whiteBoard.fxml"));
        }
        GridPane gridPane = loader.load();
        chessBoardPane.getChildren().add(gridPane);
        AnchorPane.setTopAnchor(gridPane, 0.0);
        AnchorPane.setBottomAnchor(gridPane, 0.0);
        AnchorPane.setLeftAnchor(gridPane, 0.0);
        AnchorPane.setRightAnchor(gridPane, 0.0);
        ApplicationData.getInstance().setChessboardController(loader.getController());
    }

    private void loadGraveyards() throws IOException {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("whitegraveyard.fxml"));
        HBox graveYard = loader.load();
        ApplicationData.getInstance().setWgc(loader.getController());
        FXMLLoader loader2;
        loader2 = new FXMLLoader(getClass().getResource("blackgraveyard.fxml"));
        HBox graveYard2 = loader2.load();
        ApplicationData.getInstance().setBgc(loader2.getController());
        if (!GameStates.iAmWhite()) {
            this.myGraveYardPane.getChildren().add(graveYard);
            this.oppGraveyardPane.getChildren().add(graveYard2);
        } else {
            this.myGraveYardPane.getChildren().add(graveYard2);
            this.oppGraveyardPane.getChildren().add(graveYard);
        }
    }

    private void loadChatBox() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chatView.fxml"));
        AnchorPane chatPane = loader.load();
        chatController = loader.getController();
        stackpane.getChildren().add(chatPane);
        stackpane.getChildren().get(1).toBack();
        newMessage.setVisible(false);
    }

    private void loadMovesTable() throws Exception {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("movesTable.fxml"));
        VBox vBox = loader.load();
        mtc = loader.getController();
        tableBox.getChildren().add(vBox);
    }

    private void loadInfoPane() throws Exception {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("infoView.fxml"));
        AnchorPane infoPane = loader.load();
        ApplicationData.getInstance().setIvc(loader.getController());
        this.infopane.getChildren().add(infoPane);
    }

    @FXML
    void toggle() {
        stackpane.getChildren().get(0).toFront();
        if (toggleButton.getText().equals("Chat")) {
            toggleButton.setText("Moves");
        } else {
            toggleButton.setText("Chat");
        }
        setMessageIndicatorVisibility(false);
    }

    public void setMessageIndicatorVisibility(boolean visible) {
        if (stackpane.getChildren().get(1) instanceof AnchorPane) {
            newMessage.setVisible(false);
        } else {
            newMessage.setVisible(visible);
            if (visible) {
                new SoundPlayer().playNotificationSound();
            }
        }
    }

    private void loadUIElements() throws Exception {
        loadMovesTable();
        loadChessBoard();
        loadChatBox();
        loadInfoPane();
        loadGraveyards();
    }

    public static ChatController getChatController() {
        return chatController;
    }

    public static MovesTableController getMtc() {
        return mtc;
    }
}