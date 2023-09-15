package com.example.controller;

import Networking.Server;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import util.ApplicationData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Objects;
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
    @FXML
    private ImageView newMessage;
    @FXML
    private AnchorPane infopane;
    @FXML
    private AnchorPane oppGraveyardPane;
    @FXML
    private AnchorPane myGraveYardPane;

    private static MovesTableController mtc;
    private static ChatController chatController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUIElements();
        stackpane.getChildren().get(0).setVisible(false);
        new Thread(this::startServer).start();
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

    private void loadGraveyards() throws IOException {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("whitegraveyard.fxml"));
        HBox graveYard = loader.load();
        ApplicationData.getInstance().setWgc(loader.getController());
        FXMLLoader loader2;
        loader2 = new FXMLLoader(getClass().getResource("blackgraveyard.fxml"));
        HBox graveYard2 = loader2.load();
        ApplicationData.getInstance().setBgc(loader2.getController());
        if (GameStates.iAmWhite()) {
            this.myGraveYardPane.getChildren().add(graveYard);
            this.oppGraveyardPane.getChildren().add(graveYard2);
        } else {
            this.myGraveYardPane.getChildren().add(graveYard2);
            this.oppGraveyardPane.getChildren().add(graveYard);
        }
    }

    @FXML
    void toggle() {
        setMessageIndicatorVisibility(false);
        stackpane.getChildren().get(1).setVisible(false);
        stackpane.getChildren().get(0).setVisible(true);
        stackpane.getChildren().get(0).toFront();

        if (toggleButton.getText().equals("Chat")) {
            toggleButton.setText("Moves");
        } else {
            toggleButton.setText("Chat");
        }
    }

    public void setMessageIndicatorVisibility(boolean visible) {
        if (stackpane.getChildren().get(1) instanceof AnchorPane) {
            newMessage.setVisible(false);
        } else {
            newMessage.setVisible(visible);
            if (visible) {
                String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/notify.mp3")).toString();
                Media sound = new Media(soundFile);
                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.play();

            }
        }
    }

    private void loadUIElements() {
        try {
            loadMovesTable();
            loadChessBoard();
            loadChatBox();
            loadInfoPane();
            loadGraveyards();
        } catch (Exception e) {
            e.printStackTrace();
        }
        roleLabel.setText("Chess - " + (GameStates.isServerWhite() ? "White" : "Black"));
    }

    private void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            Server server = new Server(serverSocket);
            ApplicationData.getInstance().setServer(server);
            server.receiveMessageFromClient();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static MovesTableController getMtc() {
        return mtc;
    }

    public static ChatController getChatController() {
        return chatController;
    }
}
