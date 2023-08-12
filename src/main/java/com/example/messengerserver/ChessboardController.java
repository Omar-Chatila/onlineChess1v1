package com.example.messengerserver;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class ChessboardController {
    @FXML
    private GridPane chessboardGrid;
    private Button selectedPiece;

    @FXML
    private void initialize() {
        // Set up drag detection for each piece button
        for (Node node : chessboardGrid.getChildren()) {
            System.out.println(node);
            StackPane current = (StackPane) node;
            System.out.println(current.getChildren());
            for (Node button : current.getChildren()) {
                if (button instanceof Button) {
                    Button currentButton = (Button) button;
                    System.out.println(button);
                    currentButton.setOnDragDetected(new EventHandler<MouseEvent>() {
                        public void handle(MouseEvent event) {
                            Dragboard db = currentButton.startDragAndDrop(TransferMode.MOVE);
                            System.out.println(currentButton);
                            // Add image to the dragboard
                            ClipboardContent content = new ClipboardContent();
                            content.putImage(((ImageView) currentButton.getGraphic()).getImage());
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
                                ((StackPane) selectedPiece.getParent()).getChildren().remove(selectedPiece);
                                // Add the piece to the new position
                                StackPane cell = (StackPane) currentButton.getParent();
                                if (cell.getChildren().size() == 2) {
                                    cell.getChildren().remove(1);
                                }
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
