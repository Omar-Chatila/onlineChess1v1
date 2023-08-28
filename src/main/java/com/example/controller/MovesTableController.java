package com.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import tableView.ButtonTableCell;
import tableView.IntegerTableCell;
import tableView.Item;

public class MovesTableController {
    @FXML
    private TableView<Item> movesTable;

    @FXML
    private void initialize() {
        createMovesTable();
        addMove();
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

    private void addMove() {
        movesTable.getItems().addAll(
                new Item(1, "e4", "e5"),
                new Item(2, "Nf3", "Nc6")
        );
    }
}
