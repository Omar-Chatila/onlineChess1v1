package com.example.controller;

import chessModel.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import util.ApplicationData;
import util.IntIntPair;

import java.util.List;
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

    @FXML
    private void initialize() {
        // Set up drag detection for each piece button
        for (Node node : chessboardGrid.getChildren()) {
            if (node instanceof StackPane current) {
                setSquareAccessibleText(current);
                for (Node button : current.getChildren()) {
                    if (button instanceof Button currentButton) {
                        setButtonListeners(currentButton);
                    }
                }
            }
        }
    }

    private void setSquareAccessibleText(StackPane current) {
        Integer rowIndexConstraint = GridPane.getRowIndex(current);
        Integer columnIndexConstraint = GridPane.getColumnIndex(current);
        String square = (GameStates.isServerWhite() && GameStates.isServer()) ? Character.toString('a' + Objects.requireNonNullElse(columnIndexConstraint, 0))
                + (8 - Objects.requireNonNullElse(rowIndexConstraint, 0)) : Character.toString('h' - Objects.requireNonNullElse(columnIndexConstraint, 0))
                + (Objects.requireNonNullElse(rowIndexConstraint, 0) + 1);
        current.setAccessibleText(square);
    }

    private void setOnDragDetection(Button currentButton) {
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
        if (!movedPiece.isEmpty()) {
            highlightPossibleSquares(movedPiece, isWhitePiece);
        }
        db.setContent(content);
        // Save reference to selected piece
        selectedPiece = currentButton;
    }

    private void setButtonListeners(Button currentButton) {
        currentButton.setStyle(currentButton.getStyle() + "-fx-background-radius: 0;");
        currentButton.setOnDragDetected(event -> setOnDragDetection(currentButton));
        currentButton.setOnDragOver(event -> {
            if (event.getGestureSource() != currentButton && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
        });
        currentButton.setOnDragDropped(event -> setOnDragDropped(currentButton, event));
        currentButton.setOnDragDone(Event::consume);
    }

    private void setOnDragDropped(Button currentButton, DragEvent event) {
        clearHighlighting();
        if (GameStates.isIsMyTurn() && selectedPiece != null) {
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
            IntIntPair destinationSquare = new IntIntPair(Objects.requireNonNullElse(GridPane.getRowIndex(cell), 0), Objects.requireNonNullElse(GridPane.getColumnIndex(cell), 0));
            move = movedPiece + cell.getAccessibleText();
            if (cell.getChildren().size() == 2) {
                move = movedPiece + file + "x" + cell.getAccessibleText();
            }
            System.out.println(move);

            if (GameStates.isServer()) {
                ApplicationData.getInstance().getServer().sendMessageToClient(move);
                ApplicationData.getInstance().getServer().sendMessageToClient(startingSquare.toString() + "." + destinationSquare);
            } else {
                ApplicationData.getInstance().getClient().sendMessageToServer(move);
                ApplicationData.getInstance().getClient().sendMessageToServer(startingSquare.toString() + "." + destinationSquare);
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

    private StackPane getPaneFromCoordinate(IntIntPair rc) {
        StackPane result = null;
        for (Node node : chessboardGrid.getChildren()) {
            if (Objects.requireNonNullElse(GridPane.getRowIndex(node), 0) == rc.getRow() && Objects.requireNonNullElse(GridPane.getColumnIndex(node), 0) == rc.getColumn()) {
                result = (StackPane) node;
            }
        }
        return result;
    }

    private void highlightPossibleSquares(String movedPiece, boolean isWhitePiece) {
        List<String> list = null;
        if (movedPiece.matches("[bB]")) {
            list = BishopMoveTracker.possibleMoves(Game.board, startingSquare.getRow(), startingSquare.getColumn(), isWhitePiece);
        } else if (movedPiece.matches("[nN]")) {
            list = KnightMoveTracker.possibleMoves(Game.board, startingSquare.getRow(), startingSquare.getColumn(), isWhitePiece);
        } else if (movedPiece.matches("[qQ]")) {
            list = QueenMoveTracker.possibleMoves(Game.board, startingSquare.getRow(), startingSquare.getColumn(), isWhitePiece);
        } else if (movedPiece.matches("[rR]")) {
            list = RookMoveTracker.possibleMoves(Game.board, startingSquare.getRow(), startingSquare.getColumn(), isWhitePiece);
        } else if (movedPiece.matches("[kK]]")) {
            list = KingMoveTracker.possibleMoves(Game.board, startingSquare.getRow(), startingSquare.getColumn(), isWhitePiece);
        }
        assert list != null;
        for (String coordinate : list) {
            System.out.println("Koordinate" + coordinate);
            IntIntPair c = new IntIntPair(Character.getNumericValue(coordinate.charAt(0)), Character.getNumericValue(coordinate.charAt(1)));
            StackPane square = getPaneFromCoordinate(c);
            Button b = (Button) square.getChildren().get(0);
            Image highlight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/transparent.png")));
            ImageView h = new ImageView(highlight);
            b.setGraphic(h);
        }
    }

    private void clearHighlighting() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane square = getPaneFromCoordinate(new IntIntPair(i, j));
                Button button = (Button) square.getChildren().get(0);
                button.setGraphic(null); // Remove the graphic (highlight image)
            }
        }
    }

    public void updateBoard(String opponentMove) {
        updateCheckStatus();
        System.out.println("transform " + opponentMove);
        int startRow = 7 - Character.getNumericValue(opponentMove.charAt(0));
        int startCol = 7 - Character.getNumericValue(opponentMove.charAt(1));
        int destRow = 7 - Character.getNumericValue(opponentMove.charAt(3));
        int destCol = 7 - Character.getNumericValue(opponentMove.charAt(4));
        IntIntPair startCoordinates = new IntIntPair(startRow, startCol);
        IntIntPair endCoordinates = new IntIntPair(destRow, destCol);
        StackPane startCell = getPaneFromCoordinate(startCoordinates);
        StackPane endCell = getPaneFromCoordinate(endCoordinates);
        assert startCell != null;
        Button movingPiece = (Button) startCell.getChildren().remove(1);
        assert endCell != null;
        endCell.getChildren().add(movingPiece);
        if (endCell.getChildren().size() == 3) {
            endCell.getChildren().remove(1);
        }
    }
}
