package com.example.controller;

import Networking.Server;
import chessModel.Game;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import themes.Theme;
import util.ApplicationData;
import util.SoundPlayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("CallToPrintStackTrace")
public class ServerController implements Initializable {
    @FXML
    private AnchorPane backGroundPane;
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
    @FXML
    private StackPane boardStackPane;
    private AnchorPane chatBox;
    private AnchorPane tablePane;
    private boolean tableOpened;
    private boolean keyPressHandled = false;
    private static int serverPort;
    private static MovesTableController mtc;
    private static ChatController chatController;
    private GridPane gameStates;
    public static int currentPositionNr;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUIElements();
        new Thread(this::startServer).start();
        newMessage.setOnMouseClicked(e -> toggle());
        addKeyListeners();
        ApplicationData.getInstance().setServerController(this);
    }

    public static void setServerPort(int serverPort) {
        ServerController.serverPort = serverPort;
    }

    private void addKeyListeners() {
        backGroundPane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
            Scene scene = backGroundPane.getScene();
            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (!keyPressHandled) {
                    if (event.getCode() == KeyCode.LEFT) {
                        showPreviousBoard();
                    } else if (event.getCode() == KeyCode.RIGHT) {
                        showNextBoard();
                    }
                    keyPressHandled = true;
                }
            });
            scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> keyPressHandled = false);
        });
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

    private void loadStateBoard() throws IOException {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("gamestate.fxml"));
        this.gameStates = loader.load();
        ApplicationData.getInstance().setGameStatesController(loader.getController());
        boardStackPane.getChildren().add(gameStates);
        gameStates.toBack();
    }

    public void showPreviousBoard() {
        chessBoardPane.toBack();
        chessBoardPane.setVisible(false);
        gameStates.toFront();
        if (currentPositionNr > 0)
            ApplicationData.getInstance().getGameStatesController().initBoard(--currentPositionNr);
    }

    public void showNextBoard() {
        chessBoardPane.toBack();
        chessBoardPane.setVisible(false);
        gameStates.toFront();
        if (currentPositionNr < Game.moveList.size() - 1) {
            ApplicationData.getInstance().getGameStatesController().initBoard(++currentPositionNr);
        } else {
            chessBoardPane.toFront();
            chessBoardPane.setVisible(true);
        }
    }

    public void showBoardAt(int posNumber) {
        if (posNumber == Game.moveList.size()) {
            showLastBoard();
        } else {
            chessBoardPane.toBack();
            chessBoardPane.setVisible(false);
            gameStates.toFront();
            ApplicationData.getInstance().getGameStatesController().initBoard(posNumber);
        }
    }

    public void showLastBoard() {
        chessBoardPane.toFront();
        chessBoardPane.setVisible(true);
    }

    public void showFirstBoard() {
        chessBoardPane.toBack();
        chessBoardPane.setVisible(false);
        gameStates.toFront();
        ApplicationData.getInstance().getGameStatesController().initBoard(0);
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
                new SoundPlayer().playNotificationSound();
            }
        }
    }

    private void loadUIElements() {
        Theme theme = ApplicationData.getInstance().getTheme();
        backGroundPane.setStyle(theme.getBackGround());
        try {
            loadMovesTable();
            loadChessBoard();
            loadChatBox();
            loadInfoPane();
            loadGraveyards();
            loadStateBoard();
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
