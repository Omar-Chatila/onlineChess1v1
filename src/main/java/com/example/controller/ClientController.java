package com.example.controller;

import Networking.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import tableView.ButtonTableCell;
import tableView.IntegerTableCell;
import tableView.Item;
import util.ApplicationData;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("CallToPrintStackTrace")
public class ClientController implements Initializable {

    @FXML
    private Button button_send;
    @FXML
    private AnchorPane chessBoardPane;
    @FXML
    private TableView<Item> movesTable;
    @FXML
    private Label roleLabel;
    @FXML
    private ScrollPane sp_main;
    @FXML
    private TextField tf_message;
    @FXML
    private VBox vbox_messages;
    private static String ip_Address;
    private static int portNr;
    private Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createMovesTable();
        addMove();
        try {
            client = new Client(new Socket(ip_Address, portNr));
            ApplicationData.getInstance().setClient(client);
            System.out.println("Connected to server");
            client.receiveMessageFromServer(vbox_messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
        vbox_messages.heightProperty().addListener((observableValue, number, t1) -> sp_main.setVvalue((Double) t1));
        try {
            loadChessBoard();
        } catch (Exception e) {
            e.printStackTrace();
        }
        button_send.setOnAction(actionEvent -> {
            String messageToSend = tf_message.getText();
            if (!messageToSend.isEmpty()) {
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setPadding(new Insets(5, 5, 5, 10));
                Text text = new Text(messageToSend);
                TextFlow textFlow = new TextFlow(text);
                textFlow.setStyle("fx-color: rgb(239,242,255);" +
                        "-fx-background-color: rgb(15,125,242);" +
                        "-fx-background-radius: 20px;");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                text.setFill(Color.color(0.934, 0.945, 0.996));
                hBox.getChildren().add(textFlow);
                if (GameStates.isIsMyTurn()) {
                    vbox_messages.getChildren().add(hBox);
                    client.sendMessageToServer(messageToSend);
                }
                tf_message.clear();
            }
        });
        try {
            loadChessBoard();
        } catch (Exception e) {
            e.printStackTrace();
        }
        roleLabel.setText("Chess - " + (!GameStates.isServerWhite() ? "White" : "Black"));
    }

    private void addMove() {
        movesTable.getItems().addAll(
                new Item(1, "e4", "e5"),
                new Item(2, "Nf3", "Nc6")
        );
    }

    private void createMovesTable() {
        TableColumn<Item, Integer> moveNumberColumn = new TableColumn<>("#");
        moveNumberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty().asObject());
        moveNumberColumn.setCellFactory(param -> new IntegerTableCell());

        TableColumn<Item, String> whiteMovesColumn = new TableColumn<>("W");
        whiteMovesColumn.setCellValueFactory(cellData -> cellData.getValue().whiteMoveProperty());
        whiteMovesColumn.setCellFactory(param -> new ButtonTableCell());

        TableColumn<Item, String> blackMovesColumn = new TableColumn<>("B");
        blackMovesColumn.setCellValueFactory(cellData -> cellData.getValue().blackMoveProperty());
        blackMovesColumn.setCellFactory(param -> new ButtonTableCell());

        moveNumberColumn.setPrefWidth(25);
        blackMovesColumn.setPrefWidth(50);
        whiteMovesColumn.setPrefWidth(50);
        moveNumberColumn.setMaxWidth(25);
        blackMovesColumn.setMaxWidth(50);
        whiteMovesColumn.setMaxWidth(50);

        movesTable.getColumns().addAll(moveNumberColumn, whiteMovesColumn, blackMovesColumn);
    }

    public static void addLabel(String msgFromServer, VBox vBox) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));
        Text text = new Text(msgFromServer);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233,233,235);" +
                "-fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hBox.getChildren().add(textFlow);
        Platform.runLater(() -> vBox.getChildren().add(hBox));
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
}