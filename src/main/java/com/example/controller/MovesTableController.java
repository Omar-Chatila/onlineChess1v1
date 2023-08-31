package com.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import tableView.ButtonTableCell;
import tableView.IntegerTableCell;
import tableView.Item;

public class MovesTableController {
    @FXML
    private TableView<Item> movesTable;
    private final ObservableList<Item> movesList = FXCollections.observableArrayList();
    private Integer currentMove = 1;

    @FXML
    private void initialize() {
        createMovesTable();
        movesTable.setItems(movesList);
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

    public Integer getCurrentMove() {
        return currentMove;
    }

    public void addMove(Integer moveNumber, String whiteMove, String blackMove) {
        System.out.println(moveNumber);
        if (whiteMove != null) {
            System.out.println("add move " + whiteMove);
            movesList.add(new Item(moveNumber, whiteMove, null)); // Add the white move
            this.currentMove++;
        }
        if (blackMove != null) {
            if (!movesList.isEmpty()) {
                Item lastItem = movesList.get(movesList.size() - 1);
                lastItem.setBlackMove(blackMove); // Set the black move for the last white move
            }
        }
        movesTable.refresh();
    }
}
