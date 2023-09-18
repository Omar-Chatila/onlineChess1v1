package com.example.controller;

import Networking.Server;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
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
    private Label roleLabel;
    @FXML
    private JFXButton toggleButton;
    @FXML
    private StackPane stackpane;
    @FXML
    private FontAwesomeIcon newMessage;
    @FXML
    private AnchorPane infoPane;
    @FXML
    private AnchorPane oppGraveyardPane;
    @FXML
    private AnchorPane myGraveYardPane;
    @FXML
    private FontAwesomeIcon toggleIcon;

    private static int serverPort;
    private AnchorPane chatBox;
    private AnchorPane tablePane;

    private static MovesTableController mtc;
    private static ChatController chatController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUIElements();
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
        this.chatBox = chatPane;
        newMessage.setVisible(false);
    }

    private void loadMovesTable() throws Exception {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("movesTable.fxml"));
        AnchorPane tablePane = loader.load();
        this.tablePane = tablePane;
        mtc = loader.getController();
        this.stackpane.getChildren().add(tablePane);
    }

    private void loadInfoPane() throws Exception {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("infoView.fxml"));
        AnchorPane infoPane = loader.load();
        ApplicationData.getInstance().setIvc(loader.getController());
        this.infoPane.getChildren().add(infoPane);
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

    @FXML
    void toggle() {
        if (toggleButton.getText().equals("Chat")) {
            toggleButton.setText("Moves");
            toggleIcon.setGlyphName("TABLE");
            Scene scene = toggleButton.getScene();
            chatBox.translateYProperty().set(scene.getHeight());

            stackpane.getChildren().add(chatBox);

            Timeline timeline = new Timeline();
            KeyValue kv = new KeyValue(chatBox.translateYProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
            timeline.getKeyFrames().add(kf);
            timeline.setOnFinished(t -> stackpane.getChildren().remove(tablePane));
            timeline.play();
        } else {
            toggleButton.setText("Chat");
            toggleIcon.setGlyphName("COMMENTS_ALT");
            Scene scene = toggleButton.getScene();
            tablePane.translateYProperty().set(scene.getHeight());
            stackpane.getChildren().add(tablePane);
            Timeline timeline = new Timeline();
            KeyValue kv = new KeyValue(tablePane.translateYProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
            timeline.getKeyFrames().add(kf);
            timeline.setOnFinished(t -> stackpane.getChildren().remove(chatBox));
            timeline.play();
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

    @FXML
    void close() {
        Platform.exit();
    }

    public static MovesTableController getMtc() {
        return mtc;
    }

    public static ChatController getChatController() {
        return chatController;
    }
}
