package com.example.controller;

import Networking.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import util.ApplicationData;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;
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
            loadMovesTable();
            loadChessBoard();
            loadChatBox();
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
        System.out.println("Application is white?" + GameStates.isServerWhite());
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
                String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/noti.wav")).toString();
                Media sound = new Media(soundFile);
                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.play();
            }
        }
    }

    public static ChatController getChatController() {
        return chatController;
    }

    public static MovesTableController getMtc() {
        return mtc;
    }
}