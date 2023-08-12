package com.example.messengerserver;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class ChessboardController {
    @FXML
    private GridPane chessboardGrid;
    private Button selectedPiece;
    public static String movedPiece;
    public static String move;

    @FXML
    private void initialize() {
        // Set up drag detection for each piece button
        for (Node node : chessboardGrid.getChildren()) {
            if (node instanceof StackPane current) {
                Integer rowIndexConstraint = GridPane.getRowIndex(current);
                Integer columnIndexConstraint = GridPane.getColumnIndex(current);
                String square = Character.toString('a' + Objects.requireNonNullElse(columnIndexConstraint, 0))
                        + (8 - Objects.requireNonNullElse(rowIndexConstraint, 0));
                current.setAccessibleText(square);
                for (Node button : current.getChildren()) {
                    if (button instanceof Button currentButton) {
                        System.out.println(button);
                        currentButton.setOnDragDetected(new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                Dragboard db = currentButton.startDragAndDrop(TransferMode.MOVE);
                                System.out.println(currentButton);
                                // Add image to the dragboard
                                ClipboardContent content = new ClipboardContent();
                                content.putImage(((ImageView) currentButton.getGraphic()).getImage());
                                String imageUrl = ((ImageView) currentButton.getGraphic()).getImage().getUrl();
                                String movedP = imageUrl.substring(imageUrl.length() - 6);
                                movedPiece = movedP.charAt(1) != ('P') ? "" + movedP.charAt(1) : "";
                                System.out.println("Moved Piece: " + movedPiece);
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
                                if (selectedPiece != null) {
                                    // Remove the piece from its original position
                                    String file = "";
                                    if (movedPiece.isEmpty()) {
                                        file = Character.toString('a' + GridPane.getColumnIndex(selectedPiece.getParent()));
                                    }
                                    ((StackPane) selectedPiece.getParent()).getChildren().remove(selectedPiece);
                                    // Add the piece to the new position
                                    StackPane cell = (StackPane) currentButton.getParent();
                                    move = movedPiece + cell.getAccessibleText();
                                    if (cell.getChildren().size() == 2) {
                                        cell.getChildren().remove(1);
                                        move = movedPiece + file + "x" + cell.getAccessibleText();
                                    }
                                    System.out.println(move);
                                    cell.getChildren().add(selectedPiece);
                                    selectedPiece = null;
                                    event.setDropCompleted(true);
                                }
                            }
                        });

                        currentButton.setOnDragDone(new EventHandler<DragEvent>() {
                            public void handle(DragEvent event) {
                                event.consume();
                            }
                        });
                    }
                }
            }
        }
    }

    private static String constructMove() {
        return null;
    }
}
