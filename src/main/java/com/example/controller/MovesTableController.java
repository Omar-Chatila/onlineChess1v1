package com.example.controller;

import chessModel.Game;
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

        moveNumberColumn.setPrefWidth(40);
        blackMovesColumn.setPrefWidth(80);
        whiteMovesColumn.setPrefWidth(80);
        moveNumberColumn.setMaxWidth(40);
        blackMovesColumn.setMaxWidth(80);
        whiteMovesColumn.setMaxWidth(80);

        movesTable.getColumns().addAll(moveNumberColumn, whiteMovesColumn, blackMovesColumn);
    }

    public Integer getCurrentMove() {
        return currentMove;
    }

    public void addMove(Integer moveNumber, String whiteMove, String blackMove) {
        System.out.println(moveNumber);
        if (whiteMove != null) {
            if (Game.kingChecked(false, Game.board)) {
                if (Game.checkMated(false)) {
                    whiteMove = whiteMove + "#";
                } else {
                    whiteMove = whiteMove + "+";
                }
            }
            movesList.add(new Item(moveNumber, whiteMove, null));
            this.currentMove++;
        }
        if (blackMove != null) {
            if (!movesList.isEmpty()) {
                Item lastItem = movesList.get(movesList.size() - 1);
                if (Game.kingChecked(true, Game.board)) {
                    if (Game.checkMated(true)) {
                        blackMove = blackMove + "#";
                    } else {
                        blackMove = blackMove + "+";
                    }
                }
                lastItem.setBlackMove(blackMove);
            }
        }
        movesTable.refresh();
    }
}
