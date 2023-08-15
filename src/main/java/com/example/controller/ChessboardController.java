package com.example.controller;

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
                if (Main.isServerWhite() && Main.isServer()) {
                    square = Character.toString('a' + Objects.requireNonNullElse(columnIndexConstraint, 0))
                            + (8 - Objects.requireNonNullElse(rowIndexConstraint, 0));
                } else {
                    square = Character.toString('h' - Objects.requireNonNullElse(columnIndexConstraint, 0))
                            + (Objects.requireNonNullElse(rowIndexConstraint, 0) + 1);
                }
                current.setAccessibleText(square);
                for (Node button : current.getChildren()) {
                    if (button instanceof Button currentButton) {
                        currentButton.setOnDragDetected(new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                Dragboard db = currentButton.startDragAndDrop(TransferMode.MOVE);
                                startingSquare = new IntIntPair(Objects.requireNonNullElse(GridPane.getRowIndex(currentButton.getParent()), 0), Objects.requireNonNullElse(GridPane.getColumnIndex(currentButton.getParent()), 0));
                                ClipboardContent content = new ClipboardContent();
                                content.putImage(((ImageView) currentButton.getGraphic()).getImage());
                                String imageUrl = ((ImageView) currentButton.getGraphic()).getImage().getUrl();
                                String movedP = imageUrl.substring(imageUrl.length() - 6);
                                movedPiece = movedP.charAt(1) != ('P') ? "" + movedP.charAt(1) : "";
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
                                if (Main.isIsMyTurn()) {
                                    if (selectedPiece != null) {
                                        // Remove the piece from its original position
                                        String file = "";
                                        if (movedPiece.isEmpty()) {
                                            file = Character.toString('a' + Objects.requireNonNullElse(GridPane.getColumnIndex(selectedPiece.getParent()), 0));
                                        }
                                        ((StackPane) selectedPiece.getParent()).getChildren().remove(selectedPiece);
                                        // Add the piece to the new position
                                        StackPane cell = (StackPane) currentButton.getParent();
                                        destinationSquare = new IntIntPair(Objects.requireNonNullElse(GridPane.getRowIndex(cell), 0), Objects.requireNonNullElse(GridPane.getColumnIndex(cell), 0));
                                        move = movedPiece + cell.getAccessibleText();
                                        if (cell.getChildren().size() == 2) {
                                            cell.getChildren().remove(1);
                                            move = movedPiece + file + "x" + cell.getAccessibleText();
                                        }
                                        System.out.println(move);
                                        if (Main.isServer()) {
                                            ApplicationData.getInstance().getServer().sendMessageToClient(move);
                                            ApplicationData.getInstance().getServer().sendMessageToClient(startingSquare.toString() + "." + destinationSquare.toString());
                                        } else {
                                            ApplicationData.getInstance().getClient().sendMessageToServer(move);
                                            ApplicationData.getInstance().getClient().sendMessageToServer(startingSquare.toString() + "." + destinationSquare.toString());
                                        }
                                        cell.getChildren().add(selectedPiece);
                                        selectedPiece = null;
                                        event.setDropCompleted(true);
                                    }
                                }
                            }
                        });

                        currentButton.setOnDragDone(Event::consume);
                    }
                }
            }
        }
    }

    public void updateBoard(String opponentMove) {
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

        Button b = (Button) startCell.getChildren().remove(1);
        endCell.getChildren().add(b);
        if (endCell.getChildren().size() == 3) {
            endCell.getChildren().remove(1);
        }
    }
}
