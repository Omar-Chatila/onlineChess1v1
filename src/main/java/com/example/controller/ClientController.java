package com.example.controller;

import Networking.Client;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import themes.Theme;
import util.ApplicationData;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

@SuppressWarnings("CallToPrintStackTrace")
public class ClientController implements Initializable, Client.ClientCallback {

    public AnchorPane backGroundPane;
    @FXML
    private AnchorPane chessBoardPane;
    @FXML
    private AnchorPane infoPane;
    @FXML
    private AnchorPane myGraveYardPane;
    @FXML
    private FontAwesomeIcon newMessage;
    @FXML
    private AnchorPane oppGraveyardPane;
    @FXML
    private Label roleLabel;
    @FXML
    private StackPane stackpane;
    @FXML
    private JFXButton toggleButton;
    @FXML
    private FontAwesomeIcon toggleIcon;

    private static String ip_Address;
    private static int portNr;
    private static MovesTableController mtc;
    private static ChatController chatController;
    private AnchorPane chatBox;
    private AnchorPane tablePane;
    private boolean tableOpened;
    private Theme theme;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Client client = new Client(new Socket(ip_Address, portNr));
            ApplicationData.getInstance().setClient(client);
            System.out.println("Connected to server");
            client.setCallback(this);
            client.receiveMessageFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            loadUIElements();
        } catch (Exception e) {
            e.printStackTrace();
        }
        newMessage.setOnMouseClicked(e -> toggle());
        roleLabel.setText("Chess - " + (!GameStates.isServerWhite() ? "White" : "Black"));
    }

    public static void setIp_Address(String ip_Address) {
        ClientController.ip_Address = ip_Address;
    }

    public static void setPortNr(int portNr) {
        ClientController.portNr = portNr;
    }

    private void loadChessBoard(boolean isServerWhite) throws Exception {
        FXMLLoader loader;
        if (isServerWhite) {
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

    @FXML
    void toggle() {
        setMessageIndicatorVisibility(false);
        if (toggleButton.getText().equals("Chat")) {
            tableOpened = true;
            toggleButton.setText("Moves");
            toggleIcon.setGlyphName("TABLE");
            chatBox.translateXProperty().set(stackpane.getWidth());
            try {
                stackpane.getChildren().add(chatBox);
            } catch (IllegalArgumentException e) {
                return;
            }
            Timeline timeline = new Timeline();
            KeyValue kv = new KeyValue(chatBox.translateXProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.4), kv);
            timeline.getKeyFrames().add(kf);
            timeline.setOnFinished(t -> stackpane.getChildren().remove(tablePane));
            timeline.play();
        } else {
            tableOpened = false;
            toggleButton.setText("Chat");
            toggleIcon.setGlyphName("COMMENTS_ALT");
            tablePane.translateXProperty().set(stackpane.getWidth());
            try {
                stackpane.getChildren().add(tablePane);
            } catch (IllegalArgumentException e) {
                return;
            }
            Timeline timeline = new Timeline();
            KeyValue kv = new KeyValue(tablePane.translateXProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.4), kv);
            timeline.getKeyFrames().add(kf);
            timeline.setOnFinished(t -> stackpane.getChildren().remove(chatBox));
            timeline.play();
        }
    }

    private ScaleTransition scaleTransition;

    private void playScaleAnimation(FontAwesomeIcon button) {
        scaleTransition = new ScaleTransition(Duration.seconds(0.5), button);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setCycleCount(4);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }

    public void setMessageIndicatorVisibility(boolean visible) {
        if (tableOpened) {
            newMessage.setVisible(false);
            newMessage.setScaleX(1);
            newMessage.setScaleY(1);
            newMessage.setScaleZ(1);
            if (scaleTransition != null)
                scaleTransition.stop();
        } else {
            newMessage.setVisible(visible);
            playScaleAnimation(newMessage);
            if (visible) {
                String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/notify.mp3")).toString();
                Media sound = new Media(soundFile);
                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.play();
            }
        }
    }


    private void loadUIElements() throws Exception {
        this.theme = ApplicationData.getInstance().getTheme();
        backGroundPane.setStyle(theme.getBackGround());
        loadMovesTable();
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

    public void close() {
        Platform.exit();
    }

    @Override
    public void onRoleReceived(boolean isServerWhite) {
        try {
            Platform.runLater(() ->
            {
                try {
                    loadChessBoard(isServerWhite);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}