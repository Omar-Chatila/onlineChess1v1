package com.example.controller;

import chessModel.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.ApplicationData;
import util.IntIntPair;

import java.io.IOException;
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
    private int pawnFile;
    private int pawnRank;
    public static String movedPiece;
    public static String move;
    private IntIntPair startingSquare;
    private static MovesTableController mtc;


    @FXML
    private void initialize() {
        setMovesTableController();
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

    private void setButtonListeners(Button currentButton) {
        currentButton.setStyle(currentButton.getStyle() + "-fx-background-radius: 0;");
        currentButton.setOnDragDetected(event -> setOnDragDetection(currentButton));
        currentButton.setOnDragOver(event -> {
            if (event.getGestureSource() != currentButton && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
        });
        currentButton.setOnDragDropped(event -> setOnDragDropped(currentButton, event));
        currentButton.setOnDragDone(event -> {
            clearHighlighting();
            updateCheckStatus();
        });
    }

    private void setOnDragDetection(Button currentButton) {
        if (!GameStates.isGameOver()) {
            ApplicationData.getInstance().setIllegalMove(false);
            currentButton.getGraphic().setOpacity(0.5);
            Dragboard db = currentButton.startDragAndDrop(TransferMode.MOVE);
            startingSquare = new IntIntPair(Objects.requireNonNullElse(GridPane.getRowIndex(currentButton.getParent()), 0), Objects.requireNonNullElse(GridPane.getColumnIndex(currentButton.getParent()), 0));
            ClipboardContent content = new ClipboardContent();
            content.putImage(((ImageView) currentButton.getGraphic()).getImage());
            String imageUrl = ((ImageView) currentButton.getGraphic()).getImage().getUrl();
            boolean isWhitePiece;
            if (imageUrl != null) {
                String movedP = imageUrl.substring(imageUrl.length() - 6);
                System.out.println("moved piece  " + movedP);
                isWhitePiece = Character.toString(movedP.charAt(0)).equals("w");
                movedPiece = movedP.charAt(1) != ('P') ? "" + movedP.charAt(1) : "";
            } else {
                movedPiece = currentButton.getAccessibleText().charAt(1) + "";
                isWhitePiece = currentButton.getAccessibleText().charAt(0) == 'w';
            }
            if (movedPiece.isEmpty()) {
                this.pawnFile = startingSquare.getColumn();
                this.pawnRank = startingSquare.getRow();
            }
            if (GameStates.isIsMyTurn()) {
                if (GameStates.iAmWhite() && isWhitePiece || !GameStates.iAmWhite() && !isWhitePiece)
                    highlightPossibleSquares(movedPiece, isWhitePiece);
            }
            db.setContent(content);
            selectedPiece = currentButton;
        }
    }

    private void setOnDragDropped(Button currentButton, DragEvent event) {
        clearHighlighting();
        if (!GameStates.isIsMyTurn() || selectedPiece == null) return;
        StackPane cell = (StackPane) currentButton.getParent();
        IntIntPair destinationSquare = new IntIntPair(Objects.requireNonNullElse(GridPane.getRowIndex(cell), 0), Objects.requireNonNullElse(GridPane.getColumnIndex(cell), 0));
        String move = generateMove(destinationSquare, cell);
        if (move.equals("wrong")) return;
        System.out.println(move);
        handleMoveTransmission(destinationSquare);
        applyMoveToBoardAndUI(cell);
        if (GameStates.iAmWhite()) {
            mtc.addMove(mtc.getCurrentMove(), move, null);
        } else {
            mtc.addMove(mtc.getCurrentMove(), null, move);
        }
        selectedPiece = null;
        event.setDropCompleted(true);
    }

    private void updateCheckStatus() {
        System.out.println("White King checked: " + Game.kingChecked(true) + "\n Black checked: " + Game.kingChecked(false));
        if (Game.kingChecked(false) && !Game.checkMated(false)) {
            blackKingButton.setStyle("-fx-background-color: rgba(255, 0, 0, 0.5);");
        } else if (Game.checkMated(false)) {
            blackKingButton.setStyle("-fx-background-color: #990c02;");
        } else if (!Game.kingChecked(false)) {
            blackKingButton.setStyle("-fx-background-color: transparent;");
        }
        if (Game.kingChecked(true) && !Game.checkMated(true)) {
            whiteKingButton.setStyle("-fx-background-color: rgba(255, 0, 0, 0.5);");
        } else if (Game.checkMated(true)) {
            whiteKingButton.setStyle("-fx-background-color: #990c02;");
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
        } else if (movedPiece.matches("[kK]")) {
            list = KingMoveTracker.possibleMoves(Game.board, startingSquare.getRow(), startingSquare.getColumn(), isWhitePiece);
        } else if (movedPiece.isEmpty()) {
            list = PawnMoveTracker.possibleMoves(Game.board, startingSquare.getRow(), startingSquare.getColumn(), isWhitePiece);
        }
        assert list != null;
        for (String coordinate : list) {
            IntIntPair c = new IntIntPair(Character.getNumericValue(coordinate.charAt(0)), Character.getNumericValue(coordinate.charAt(1)));
            StackPane square = getPaneFromCoordinate(c);
            Button b = (Button) square.getChildren().get(0);
            Image highlight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/transparent.png")));
            ImageView h = new ImageView(highlight);
            h.setOpacity(0.5);
            b.setGraphic(h);
        }
    }

    private void clearHighlighting() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane square = getPaneFromCoordinate(new IntIntPair(i, j));
                Button button = (Button) square.getChildren().get(0);
                if (square.getChildren().size() > 1) {
                    Button movedPiece = (Button) square.getChildren().get(1);
                    movedPiece.getGraphic().setOpacity(1);
                }
                button.setGraphic(null);
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
        if (opponentMove.matches("[0-9]{2}\\.[0-9]{2}[A-Q]")) {
            String piece = GameStates.iAmWhite() ? "b" : "w" + opponentMove.charAt(opponentMove.length() - 1);
            Image promotion = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + piece + ".png")));
            ImageView h = new ImageView(promotion);
            h.setFitHeight(50);
            h.setFitWidth(50);
            movingPiece.setGraphic(h);
            movingPiece.setAccessibleText(piece);
        }
        assert endCell != null;
        endCell.getChildren().add(movingPiece);
        if (endCell.getChildren().size() == 3) {
            endCell.getChildren().remove(1);
        }
        if (GameStates.iAmWhite()) {
            mtc.addMove(mtc.getCurrentMove(), null, Game.moveList.get(Game.moveList.size() - 1));
        } else {
            mtc.addMove(mtc.getCurrentMove(), Game.moveList.get(Game.moveList.size() - 1), null);
        }
    }

    private String generateMove(IntIntPair destinationSquare, StackPane cell) {
        String file = "";
        boolean isWhite = GameStates.isServerWhite() && GameStates.isServer() || !GameStates.isServerWhite() && !GameStates.isServer();
        if (movedPiece.isEmpty()) {
            if (isWhite) {
                file = Character.toString('a' + Objects.requireNonNullElse(GridPane.getColumnIndex(selectedPiece.getParent()), 0));
            } else {
                file = Character.toString('h' - Objects.requireNonNullElse(GridPane.getColumnIndex(selectedPiece.getParent()), 0));
            }
        }
        move = movedPiece + cell.getAccessibleText();
        if (cell.getChildren().size() == 2) {
            if (!move.contains("O")) {
                move = movedPiece + file + "x" + cell.getAccessibleText();
            }
        }
        if (Game.isAmbiguousMove(move, isWhite, destinationSquare)) {
            System.out.println("AMBIGUOUS MOVE!!! -- " + move);
            if (Game.pieceOnSameFile(move, isWhite, destinationSquare, startingSquare)) {
                int rank;
                if (isWhite)
                    rank = 8 - Objects.requireNonNullElse(GridPane.getRowIndex(selectedPiece.getParent()), 0);
                else
                    rank = Objects.requireNonNullElse(GridPane.getRowIndex(selectedPiece.getParent()), 0) + 1;
                move = movedPiece + rank + cell.getAccessibleText();
                if (cell.getChildren().size() == 2) {
                    if (!move.contains("O")) {
                        move = movedPiece + rank + "x" + cell.getAccessibleText();
                    }
                }
            } else {
                if (isWhite) {
                    file = Character.toString('a' + Objects.requireNonNullElse(GridPane.getColumnIndex(selectedPiece.getParent()), 0));
                } else {
                    file = Character.toString('h' - Objects.requireNonNullElse(GridPane.getColumnIndex(selectedPiece.getParent()), 0));
                }
                move = movedPiece + file + cell.getAccessibleText();
                if (cell.getChildren().size() == 2) {
                    if (!move.contains("O")) {
                        move = movedPiece + file + "x" + cell.getAccessibleText();
                    }
                }
            }
        }
        if (movedPiece.isEmpty() && !move.contains("x") && (destinationSquare.getColumn() != pawnFile || destinationSquare.getRow() >= pawnRank)) {
            move = "wrong";
        }
        if (movedPiece.isEmpty() && move.contains("x") && (Math.abs(destinationSquare.getColumn() - pawnFile) != 1 || destinationSquare.getRow() + 1 != pawnRank)) {
            move = "wrong";
        }
        if (Game.board[7][4].equals("K") && (move.equals("Kg1") || move.equals("Kxh1"))
                || Game.board[0][4].equals("k") && (move.equals("Kg8") || move.equals("Kxh8"))) {
            move = "O-O";
        } else if (Game.board[7][4].equals("K") && (move.equals("Kc1") || move.equals("Kb1") || move.equals("Kxa1"))
                || Game.board[0][4].equals("k") && (move.equals("Kb8") || move.equals("Kc8") || move.equals("Kxa8"))) {
            move = "O-O-O";
        }
        System.out.println(move);
        if (move.matches("([a-h]x)?[a-h]8") || move.matches("([a-h]x)?[a-h]1")) {
            Button movingButton = (Button) getPaneFromCoordinate(startingSquare).getChildren().get(1);
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("PawnPromotion.fxml"));
            try {
                Stage mainStage = (Stage) this.chessboardGrid.getScene().getWindow();
                Scene scene = new Scene(fxmlLoader.load());
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.getDialogPane().setContent(scene.getRoot());
                dialog.initOwner(mainStage);
                dialog.initModality(Modality.WINDOW_MODAL);
                dialog.initStyle(StageStyle.UNDECORATED);
                //dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
                movingButton.getGraphic().setOpacity(0.5);
                dialog.showAndWait();
                System.out.println("Canceled");
                move += "=" + ApplicationData.getInstance().getPromotedPiece();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return move;
    }

    private void handleMoveTransmission(IntIntPair destinationSquare) {
        if (GameStates.isServer()) {
            ApplicationData.getInstance().getServer().sendMessageToClient(move);
            String message = startingSquare.toString() + "." + destinationSquare;
            if (move.contains("=")) {
                message = message + move.charAt(move.length() - 1);
            }
            ApplicationData.getInstance().getServer().sendMessageToClient(message);
            if (move.equals("O-O")) {
                if (GameStates.isServer() && GameStates.isServerWhite() || !GameStates.isServer() && !GameStates.isServerWhite()) {
                    ApplicationData.getInstance().getServer().sendMessageToClient("77.75");
                } else {
                    ApplicationData.getInstance().getServer().sendMessageToClient("70.72");
                }
            } else if (move.equals("O-O-O")) {
                if (GameStates.isServer() && GameStates.isServerWhite() || !GameStates.isServer() && !GameStates.isServerWhite()) {
                    ApplicationData.getInstance().getServer().sendMessageToClient("70.73");
                } else {
                    ApplicationData.getInstance().getServer().sendMessageToClient("77.74");
                }
            }
        } else {
            ApplicationData.getInstance().getClient().sendMessageToServer(move);
            String message = startingSquare.toString() + "." + destinationSquare;
            if (move.contains("=")) {
                message = message + move.charAt(move.length() - 1);
            }
            ApplicationData.getInstance().getClient().sendMessageToServer(message);
            if (move.equals("O-O")) {
                if (GameStates.isServer() && GameStates.isServerWhite() || !GameStates.isServer() && !GameStates.isServerWhite()) {
                    ApplicationData.getInstance().getClient().sendMessageToServer("77.75");
                } else {
                    ApplicationData.getInstance().getClient().sendMessageToServer("70.72");
                }
            } else if (move.equals("O-O-O")) {
                if (GameStates.isServer() && GameStates.isServerWhite() || !GameStates.isServer() && !GameStates.isServerWhite()) {
                    ApplicationData.getInstance().getClient().sendMessageToServer("70.73");
                } else {
                    ApplicationData.getInstance().getClient().sendMessageToServer("77.74");
                }
            }
        }
    }

    private void applyMoveToBoardAndUI(StackPane cell) {
        if (!ApplicationData.getInstance().isIllegalMove() && !move.equals("O-O") && !move.equals("O-O-O")) {
            if (cell.getChildren().size() == 2) {
                cell.getChildren().remove(1);
            }
            if (move.contains("=")) {
                String piece = "";
                switch (ApplicationData.getInstance().getPromotedPiece()) {
                    case "Q" -> piece = (GameStates.iAmWhite()) ? "wQ" : "bQ";
                    case "N" -> piece = (GameStates.iAmWhite()) ? "wN" : "bN";
                    case "R" -> piece = (GameStates.iAmWhite()) ? "wR" : "bR";
                    case "B" -> piece = (GameStates.iAmWhite()) ? "wB" : "bB";
                }
                Image promotion = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + piece + ".png")));
                ImageView h = new ImageView(promotion);
                h.setFitHeight(50);
                h.setFitWidth(50);
                selectedPiece.setGraphic(h);
                selectedPiece.setAccessibleText(piece);
                ((StackPane) selectedPiece.getParent()).getChildren().remove(selectedPiece);
                cell.getChildren().add(selectedPiece);
                setButtonListeners(selectedPiece);
            } else {
                ((StackPane) selectedPiece.getParent()).getChildren().remove(selectedPiece);
                cell.getChildren().add(selectedPiece);
            }
        } else if ((move.equals("O-O") || move.equals("O-O-O")) && !ApplicationData.getInstance().isIllegalMove()) {
            if (GameStates.isServerWhite() && GameStates.isServer() || !GameStates.isServerWhite() && !GameStates.isServer()) {
                StackPane kingSquare;
                StackPane rookSquare;
                if (move.equals("O-O")) {
                    kingSquare = getPaneFromCoordinate(new IntIntPair(7, 6));
                    rookSquare = getPaneFromCoordinate(new IntIntPair(7, 5));
                } else {
                    kingSquare = getPaneFromCoordinate(new IntIntPair(7, 2));
                    rookSquare = getPaneFromCoordinate(new IntIntPair(7, 3));
                }
                Button kingButton = (Button) getPaneFromCoordinate(new IntIntPair(7, 4)).getChildren().get(1);
                Button rookButton = (Button) getPaneFromCoordinate(new IntIntPair(7, move.equals("O-O") ? 7 : 0)).getChildren().get(1);
                kingSquare.getChildren().add(kingButton);
                rookSquare.getChildren().add(rookButton);
                getPaneFromCoordinate(new IntIntPair(7, 4)).getChildren().remove(1);
                getPaneFromCoordinate(new IntIntPair(7, move.equals("O-O") ? 7 : 0)).getChildren().remove(1);
            } else {
                StackPane kingSquare;
                StackPane rookSquare;
                if (move.equals("O-O")) {
                    kingSquare = getPaneFromCoordinate(new IntIntPair(7, 1));
                    rookSquare = getPaneFromCoordinate(new IntIntPair(7, 2));
                } else {
                    kingSquare = getPaneFromCoordinate(new IntIntPair(7, 5));
                    rookSquare = getPaneFromCoordinate(new IntIntPair(7, 4));
                }
                Button kingButton = (Button) getPaneFromCoordinate(new IntIntPair(7, 3)).getChildren().get(1);
                Button rookButton = (Button) getPaneFromCoordinate(new IntIntPair(7, move.equals("O-O") ? 0 : 7)).getChildren().get(1);
                kingSquare.getChildren().add(kingButton);
                rookSquare.getChildren().add(rookButton);
                getPaneFromCoordinate(new IntIntPair(7, 3)).getChildren().remove(1);
                getPaneFromCoordinate(new IntIntPair(7, move.equals("O-O") ? 0 : 7)).getChildren().remove(1);
            }
        }
    }

    private void setMovesTableController() {
        if (GameStates.isServer()) {
            System.out.println("HIEEEER");
            mtc = ServerController.getMtc();
        } else {
            mtc = ClientController.getMtc();
        }
    }
}
