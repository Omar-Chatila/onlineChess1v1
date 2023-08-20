package com.example.controller;

import chessModel.BishopMoveTracker;
import chessModel.Game;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import util.ApplicationData;
import util.IntIntPair;

import java.util.Objects;

public class ChessboardController {
    @FXML
    private GridPane chessboardGrid;
    @FXML
    private Button whiteKingButton;
    @FXML
    private Button blackKingButton;
    private Button selectedPiece;
    public static String movedPiece;
    public static String move;
    private IntIntPair startingSquare;
    private IntIntPair destinationSquare;

    @FXML
    private void initialize() {
        // Set up drag detection for each piece button
        for (Node node : chessboardGrid.getChildren()) {
            if (node instanceof StackPane current) {
                Integer rowIndexConstraint = GridPane.getRowIndex(current);
                Integer columnIndexConstraint = GridPane.getColumnIndex(current);
                String square;
                if (GameStates.isServerWhite() && GameStates.isServer()) {
                    square = Character.toString('a' + Objects.requireNonNullElse(columnIndexConstraint, 0))
                            + (8 - Objects.requireNonNullElse(rowIndexConstraint, 0));
                } else {
                    square = Character.toString('h' - Objects.requireNonNullElse(columnIndexConstraint, 0))
                            + (Objects.requireNonNullElse(rowIndexConstraint, 0) + 1);
                }
                current.setAccessibleText(square);
                for (Node button : current.getChildren()) {
                    if (button instanceof Button currentButton) {
                        currentButton.setStyle(currentButton.getStyle() + "-fx-background-radius: 0;");
                        currentButton.setOnDragDetected(new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                ApplicationData.getInstance().setIllegalMove(false);
                                Dragboard db = currentButton.startDragAndDrop(TransferMode.MOVE);
                                startingSquare = new IntIntPair(Objects.requireNonNullElse(GridPane.getRowIndex(currentButton.getParent()), 0), Objects.requireNonNullElse(GridPane.getColumnIndex(currentButton.getParent()), 0));
                                ClipboardContent content = new ClipboardContent();
                                content.putImage(((ImageView) currentButton.getGraphic()).getImage());
                                String imageUrl = ((ImageView) currentButton.getGraphic()).getImage().getUrl();
                                String movedP = imageUrl.substring(imageUrl.length() - 6);
                                System.out.println("moved piece  " + movedP);
                                boolean isWhitePiece = Character.toString(movedP.charAt(0)).equals("w");
                                movedPiece = movedP.charAt(1) != ('P') ? "" + movedP.charAt(1) : "";
                                System.out.println("startpunkt: " + startingSquare.getRow() + "," + startingSquare.getColumn());
                                if (movedPiece.matches("[bB]")) {
                                    System.out.println(BishopMoveTracker.possibleMoves(Game.board, startingSquare.getRow(), startingSquare.getColumn(), isWhitePiece));
                                    for (String coord : BishopMoveTracker.possibleMoves(Game.board, startingSquare.getRow(), startingSquare.getColumn(), isWhitePiece)) {
                                        for (Node node : chessboardGrid.getChildren()) {
                                            StackPane highlight = (StackPane) node;
                                            int rf = Integer.parseInt(coord);
                                            int row = Character.getNumericValue(highlight.getAccessibleText().charAt(1));
                                            int col = Character.getNumericValue(highlight.getAccessibleText().charAt(0));
                                            System.out.println("Reihe: " + row + " Col: " + col);
                                            if (row == rf / 10
                                                    && col == rf % 10) {
                                                highlight.getChildren().get(0).setStyle("-fx-background-color: transparent;" +
                                                        "                                                -fx-background-radius: 0;" +
                                                        "                                                -fx-shape: \"M 50 50 L 50 60 Q 50 70 60 70 L 70 70 Q 80 70 80 60 L 80 50 Q 80 40 70 40 L 60 40 Q 50 40 50 50 Z\";" +
                                                        "                                                -fx-fill: rgba(128, 128, 128, 0.5);" +
                                                        "                                                -fx-padding: 5;");
                                            }

                                        }
                                    }

                                }


                                db.setContent(content);
                                // Save reference to selected piece
                                selectedPiece = currentButton;
                            }
                        });
                        currentButton.setOnDragOver(new EventHandler<DragEvent>() {
                            public void handle(DragEvent event) {
                                if (event.getGestureSource() != currentButton && event.getDragboard().hasImage()) {
                                    event.acceptTransferModes(TransferMode.MOVE);
                                }
                            }
                        });

                        currentButton.setOnDragDropped(new EventHandler<DragEvent>() {
                            public void handle(DragEvent event) {
                                if (GameStates.isIsMyTurn()) {
                                    if (selectedPiece != null) {
                                        String file = "";
                                        if (movedPiece.isEmpty()) {
                                            if (GameStates.isServerWhite() && GameStates.isServer()) {
                                                file = Character.toString('a' + Objects.requireNonNullElse(GridPane.getColumnIndex(selectedPiece.getParent()), 0));
                                            } else {
                                                file = Character.toString('h' - Objects.requireNonNullElse(GridPane.getColumnIndex(selectedPiece.getParent()), 0));
                                            }
                                        }
                                        // Add the piece to the new position
                                        StackPane cell = (StackPane) currentButton.getParent();
                                        destinationSquare = new IntIntPair(Objects.requireNonNullElse(GridPane.getRowIndex(cell), 0), Objects.requireNonNullElse(GridPane.getColumnIndex(cell), 0));
                                        move = movedPiece + cell.getAccessibleText();
                                        if (cell.getChildren().size() == 2) {
                                            move = movedPiece + file + "x" + cell.getAccessibleText();
                                        }
                                        System.out.println(move);

                                        if (GameStates.isServer()) {
                                            ApplicationData.getInstance().getServer().sendMessageToClient(move);
                                            ApplicationData.getInstance().getServer().sendMessageToClient(startingSquare.toString() + "." + destinationSquare.toString());
                                        } else {
                                            ApplicationData.getInstance().getClient().sendMessageToServer(move);
                                            ApplicationData.getInstance().getClient().sendMessageToServer(startingSquare.toString() + "." + destinationSquare.toString());
                                        }
                                        if (!ApplicationData.getInstance().isIllegalMove()) {
                                            System.out.println("Legal");
                                            if (cell.getChildren().size() == 2) {
                                                cell.getChildren().remove(1);
                                            }
                                            ((StackPane) selectedPiece.getParent()).getChildren().remove(selectedPiece);
                                            cell.getChildren().add(selectedPiece);
                                            updateCheckStatus();
                                        }
                                        selectedPiece = null;
                                        event.setDropCompleted(true);
                                    }
                                }
                            }
                        });
                        currentButton.setOnDragDone(Event::consume);  // TODO Hier drag drp canceln bei illegalen moves
                    }
                }
            }
        }
    }

    private void updateCheckStatus() {
        System.out.println("White King checked: " + Game.kingChecked(true) + "\n Black checked: " + Game.kingChecked(false));
        if (Game.kingChecked(false)) {
            blackKingButton.setStyle("-fx-background-color: rgba(255, 0, 0, 0.5);");
        } else if (!Game.kingChecked(false)) {
            blackKingButton.setStyle("-fx-background-color: transparent;");
        }
        if (Game.kingChecked(true)) {
            whiteKingButton.setStyle("-fx-background-color: rgba(255, 0, 0, 0.5);");
        } else if (!Game.kingChecked(true)) {
            whiteKingButton.setStyle("-fx-background-color: transparent;");
        }
    }

    public void updateBoard(String opponentMove) {
        updateCheckStatus();
        System.out.println("transform " + opponentMove);
        int startRow = 7 - Character.getNumericValue(opponentMove.charAt(0));
        int startCol = 7 - Character.getNumericValue(opponentMove.charAt(1));
        int destRow = 7 - Character.getNumericValue(opponentMove.charAt(3));
        int destCol = 7 - Character.getNumericValue(opponentMove.charAt(4));
        StackPane startCell = null;
        for (Node node : chessboardGrid.getChildren()) {
            if (Objects.requireNonNullElse(GridPane.getRowIndex(node), 0) == startRow && Objects.requireNonNullElse(GridPane.getColumnIndex(node), 0) == startCol) {
                startCell = (StackPane) node;
            }
        }
        StackPane endCell = null;
        for (Node node : chessboardGrid.getChildren()) {
            if (Objects.requireNonNullElse(GridPane.getRowIndex(node), 0) == destRow && Objects.requireNonNullElse(GridPane.getColumnIndex(node), 0) == destCol) {
                endCell = (StackPane) node;
            }
        }
        assert startCell != null;
        Button movingPiece = (Button) startCell.getChildren().remove(1);
        assert endCell != null;
        endCell.getChildren().add(movingPiece);
        if (endCell.getChildren().size() == 3) {
            endCell.getChildren().remove(1);
        }
    }
}
