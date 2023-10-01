package com.example.controller;

import chessModel.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.effect.Glow;
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
import themes.SwagTheme;
import themes.Theme;
import util.ApplicationData;
import util.IntIntPair;
import util.SoundPlayer;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChessboardController {
    @FXML
    private GridPane chessboardGrid;
    @FXML
    private Button whiteKingButton;
    @FXML
    private Button blackKingButton;
    @FXML
    private ImageView whiteKing;
    @FXML
    private ImageView blackKing;
    private Button selectedPiece;
    private int pawnFile;
    private int pawnRank;
    public static String movedPiece;
    public static String move;
    private IntIntPair startingSquare;
    private IntIntPair destinationsSquare;
    private Button lastHoveredButton;
    private String hoveredButtonStyle;
    private boolean myTurn;
    private StackPane lastStart;
    private StackPane lastEnd;
    private Theme theme;
    private List<String> possibleSquares;

    @FXML
    private void initialize() {
        this.theme = ApplicationData.getInstance().getTheme();
        this.myTurn = GameStates.iAmWhite();
        if (!GameStates.isServer())
            new SoundPlayer().playGameStartSound();
        for (Node node : chessboardGrid.getChildren()) {
            if (node instanceof StackPane current) {
                setSquareTxtNStyle(current);
                for (Node button : current.getChildren()) {
                    if (button instanceof Button currentButton) {
                        setButtonListeners(currentButton);
                        if (((Button) button).getGraphic() instanceof ImageView imv) {
                            applyPieceTheme(imv, currentButton);
                        }
                    }
                }
            }
        }
    }

    private void applyPieceTheme(ImageView imv, Button currentButton) {
        String url = imv.getImage().getUrl().substring(imv.getImage().getUrl().lastIndexOf("/images"));
        String themeUrl = url.replaceAll("standard/", theme.getPiecesPath());
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(themeUrl)));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        if (theme instanceof SwagTheme) {
            imageView.setScaleX(0.95);
            imageView.setScaleY(0.95);
        }
        currentButton.setGraphic(imageView);
        currentButton.setContentDisplay(ContentDisplay.CENTER);
        currentButton.setAlignment(Pos.CENTER);
        currentButton.setPadding(new Insets(0, 0, 2, 0));
        Pattern pattern = Pattern.compile("[wb][RQPKNB]");
        Matcher matcher = pattern.matcher(themeUrl);
        if (matcher.find()) {
            currentButton.setId(matcher.group());
        }
    }

    private void setSquareTxtNStyle(StackPane current) {
        Integer rowIndexConstraint = GridPane.getRowIndex(current);
        Integer columnIndexConstraint = GridPane.getColumnIndex(current);
        String square = (GameStates.iAmWhite()) ? Character.toString('a' + Objects.requireNonNullElse(columnIndexConstraint, 0))
                + (8 - Objects.requireNonNullElse(rowIndexConstraint, 0)) : Character.toString('h' - Objects.requireNonNullElse(columnIndexConstraint, 0))
                + (Objects.requireNonNullElse(rowIndexConstraint, 0) + 1);
        current.setAccessibleText(square);
        int r = Objects.requireNonNullElse(GridPane.getRowIndex(current), 0);
        int c = Objects.requireNonNullElse(GridPane.getColumnIndex(current), 0);
        if ((r + c) % 2 == 0) {
            current.setStyle(theme.getLightSquareStyle());
            current.getChildren().get(0).setStyle(theme.getLightSquareStyle());
        } else {
            current.setStyle(theme.getDarkSquareStyle());
            current.getChildren().get(0).setStyle(theme.getDarkSquareStyle());
        }
    }

    private void setButtonListeners(Button currentButton) {
        currentButton.setOnDragDetected(event -> setOnDragDetection(currentButton));
        currentButton.setOnDragOver(event -> {
            if (event.getGestureSource() != currentButton && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
        });
        currentButton.setOnDragEntered(event -> {
            if (GameStates.isIsMyTurn() && isMyPiece()) {
                Button hovered = (Button) event.getSource();
                String coordinate = Objects.requireNonNullElse(GridPane.getRowIndex(hovered.getParent()), 0) + "" +
                        Objects.requireNonNullElse(GridPane.getColumnIndex(hovered.getParent()), 0);
                if (this.possibleSquares.contains(coordinate)) {
                    hoveredButtonStyle = hovered.getParent().getStyle();
                    if (hovered.getParent().getChildrenUnmodifiable().size() > 1) {
                        hovered.setStyle(theme.getHoveredXStyle());
                    } else {
                        hovered.setStyle(theme.getHoveredStyle());
                    }
                    lastHoveredButton = hovered;
                }
            }
        });
        currentButton.setOnDragExited(event -> {
            if (GameStates.isIsMyTurn() && isMyPiece()) {
                Button exited = (Button) event.getSource();
                if (exited == lastHoveredButton)
                    exited.setStyle(this.hoveredButtonStyle);
            }
        });
        currentButton.setOnDragDropped(event -> setOnDragDropped(currentButton, event));
        currentButton.setOnDragDone(this::handleDragDone);
        currentButton.setOnAction(event -> handleButtonClick(event, currentButton));
    }

    private void initMove(Button currentButton) {
        String imageUrl = currentButton.getId();
        System.out.println(imageUrl);
        boolean isWhitePiece;
        if (imageUrl != null) {
            String movedP = imageUrl.charAt(1) + "";
            isWhitePiece = Character.toString(imageUrl.charAt(0)).equals("w");
            movedPiece = movedP.equals("P") ? "" : movedP;
        } else {
            System.out.println("NUUUUUUUUUL");
            movedPiece = currentButton.getAccessibleText().charAt(1) + "";
            isWhitePiece = currentButton.getAccessibleText().charAt(0) == 'w';
        }
        if (movedPiece.isEmpty()) {
            this.pawnFile = startingSquare.column();
            this.pawnRank = startingSquare.row();
        }
        if (GameStates.isIsMyTurn() && isMyPiece()) {
            if (GameStates.iAmWhite() && isWhitePiece || !GameStates.iAmWhite() && !isWhitePiece)
                highlightPossibleSquares(movedPiece, isWhitePiece);
        }
    }

    private void setOnDragDetection(Button currentButton) {
        clearHighlighting();
        if (!GameStates.isGameOver()) {
            ApplicationData.getInstance().setIllegalMove(false);
            currentButton.getGraphic().setOpacity(0.5);
            Dragboard db = currentButton.startDragAndDrop(TransferMode.MOVE);
            startingSquare = new IntIntPair(Objects.requireNonNullElse(GridPane.getRowIndex(currentButton.getParent()), 0), Objects.requireNonNullElse(GridPane.getColumnIndex(currentButton.getParent()), 0));
            ClipboardContent content = new ClipboardContent();
            content.putImage(((ImageView) currentButton.getGraphic()).getImage());
            initMove(currentButton);
            db.setContent(content);
            selectedPiece = currentButton;
        }
    }

    private void setOnDragDropped(Button currentButton, DragEvent event) {
        clearHighlighting();
        if (!GameStates.isIsMyTurn() || selectedPiece == null || !isMyPiece()) {
            return;
        }
        StackPane cell = (StackPane) currentButton.getParent();
        IntIntPair destinationSquare = new IntIntPair(
                Objects.requireNonNullElse(GridPane.getRowIndex(cell), 0),
                Objects.requireNonNullElse(GridPane.getColumnIndex(cell), 0)
        );
        if (!possibleSquares.contains(destinationSquare.toString())) {
            return;
        }
        this.lastStart = getPaneFromCoordinate(startingSquare);
        this.lastEnd = cell;
        clearHighlighting();
        this.destinationsSquare = destinationSquare;
        if (!this.possibleSquares.contains(destinationSquare.toString())) {
            return;
        }
        String move = generateMove(destinationSquare, cell);
        if (ApplicationData.getInstance().isIllegalMove() || move.equals("wrong")) {
            return;
        }
        handleMoveTransmission(destinationSquare);
        applyMoveToBoardAndUI(cell);
        selectedPiece = null;
        event.setDropCompleted(true);
    }


    private void updateCheckStatus() {
        if (Game.kingChecked(false) && !Game.checkMated(false)) {
            blackKing.setEffect(new Glow(0.7));
            blackKingButton.setStyle(theme.getKingCheckedStyle());
        } else if (Game.checkMated(false)) {
            blackKing.setEffect(new Glow(0.8));
            blackKingButton.setStyle(theme.getKingCheckedStyle());
        } else if (!Game.kingChecked(false)) {
            blackKingButton.setStyle("-fx-background-color: transparent;");
            blackKing.setEffect(null);
        }
        if (Game.kingChecked(true) && !Game.checkMated(true)) {
            whiteKing.setEffect(new Glow(0.4));
            whiteKingButton.setStyle(theme.getKingCheckedStyle());
        } else if (Game.checkMated(true)) {
            whiteKing.setEffect(new Glow(0.8));
            whiteKingButton.setStyle(theme.getKingCheckedStyle());
        } else if (!Game.kingChecked(true)) {
            whiteKingButton.setStyle("-fx-background-color: transparent;");
            whiteKing.setEffect(null);
        }
    }

    private StackPane getPaneFromCoordinate(IntIntPair rc) {
        StackPane result = null;
        for (Node node : chessboardGrid.getChildren()) {
            if (Objects.requireNonNullElse(GridPane.getRowIndex(node), 0) == rc.row() && Objects.requireNonNullElse(GridPane.getColumnIndex(node), 0) == rc.column()) {
                result = (StackPane) node;
            }
        }
        return result;
    }

    private void highlightPossibleSquares(String movedPiece, boolean isWhitePiece) {
        if (isWhitePiece && GameStates.iAmWhite() || !isWhitePiece && !GameStates.iAmWhite()) {
            List<String> list = null;
            if (movedPiece.matches("[bB]")) {
                list = BishopMoveTracker.possibleMoves(Game.board, startingSquare.row(), startingSquare.column(), isWhitePiece);
            } else if (movedPiece.matches("[nN]")) {
                list = KnightMoveTracker.possibleMoves(Game.board, startingSquare.row(), startingSquare.column(), isWhitePiece);
            } else if (movedPiece.matches("[qQ]")) {
                list = QueenMoveTracker.possibleMoves(Game.board, startingSquare.row(), startingSquare.column(), isWhitePiece);
            } else if (movedPiece.matches("[rR]")) {
                list = RookMoveTracker.possibleMoves(Game.board, startingSquare.row(), startingSquare.column(), isWhitePiece);
            } else if (movedPiece.matches("[kK]")) {
                list = KingMoveTracker.possibleMoves(Game.board, startingSquare.row(), startingSquare.column(), isWhitePiece);
            } else if (movedPiece.isEmpty() || movedPiece.matches("[pP]")) {
                list = PawnMoveTracker.possibleMoves(Game.board, startingSquare.row(), startingSquare.column(), isWhitePiece);
            }
            assert list != null;
            this.possibleSquares = list;
            for (String coordinate : list) {
                IntIntPair c = new IntIntPair(Character.getNumericValue(coordinate.charAt(0)), Character.getNumericValue(coordinate.charAt(1)));
                StackPane square = getPaneFromCoordinate(c);
                Button b = (Button) square.getChildren().get(0);
                Image highlight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/transparent.png")));
                ImageView h = new ImageView(highlight);
                h.setOpacity(0.7);
                boolean found = false;
                for (Node n : b.getChildrenUnmodifiable()) {
                    if (n instanceof StackPane k) {
                        found = true;
                        k.getChildren().add(h);
                    }
                }
                if (!found) b.setGraphic(h);
            }
        }
    }

    public void clearHighlighting() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane square = getPaneFromCoordinate(new IntIntPair(i, j));
                Button button = (Button) square.getChildren().get(0);
                if ((i + j) % 2 == 0) {
                    button.setStyle(theme.getLightSquareStyle());
                } else {
                    button.setStyle(theme.getDarkSquareStyle());
                }
                if (square.getChildren().size() > 1) {
                    Button movedPiece = (Button) square.getChildren().get(1);
                    movedPiece.getGraphic().setOpacity(1);
                }

                boolean found = false;
                for (Node n : button.getChildrenUnmodifiable()) {
                    if (n instanceof StackPane sp) {
                        sp.getChildren().removeIf(im -> im instanceof ImageView);
                        found = true;
                    }
                }
                if (!found) button.setGraphic(null);
            }
        }
        highlightLastMove(this.lastStart, this.lastEnd);
    }

    private void highlightLastMove(StackPane startCell, StackPane endCell) {
        if (startCell == null || endCell == null) return;
        int scRi = Objects.requireNonNullElse(GridPane.getRowIndex(startCell), 0);
        int scCi = Objects.requireNonNullElse(GridPane.getColumnIndex(startCell), 0);
        int ecRi = Objects.requireNonNullElse(GridPane.getRowIndex(endCell), 0);
        int ecCi = Objects.requireNonNullElse(GridPane.getColumnIndex(endCell), 0);
        String light = theme.getLastMoveLight(); /* Green color */
        String dark = theme.getLastMoveDark();
        if ((scCi + scRi) % 2 == 0) { // white square
            startCell.getChildren().get(0).setStyle(startCell.getChildren().get(0).getStyle() + light);
        } else {
            startCell.getChildren().get(0).setStyle(startCell.getChildren().get(0).getStyle() + dark);
        }
        if ((ecRi + ecCi) % 2 == 0) { // white square
            endCell.getChildren().get(0).setStyle(endCell.getChildren().get(0).getStyle() + light);
        } else {
            endCell.getChildren().get(0).setStyle(endCell.getChildren().get(0).getStyle() + dark);
        }
        this.lastStart = startCell;
        this.lastEnd = endCell;
    }

    public void updateBoard(String opponentMove) {
        if (GameStates.isServer()) {
            ApplicationData.getInstance().getServerController().showLastBoard();
        } else {
            ApplicationData.getInstance().getClientController().showLastBoard();
        }
        this.destinationsSquare = null;
        this.myTurn = true;
        updateCheckStatus();
        int startRow = 7 - Character.getNumericValue(opponentMove.charAt(0));
        int startCol = 7 - Character.getNumericValue(opponentMove.charAt(1));
        int destRow = 7 - Character.getNumericValue(opponentMove.charAt(3));
        int destCol = 7 - Character.getNumericValue(opponentMove.charAt(4));
        IntIntPair startCoordinates = new IntIntPair(startRow, startCol);
        IntIntPair endCoordinates = new IntIntPair(destRow, destCol);
        StackPane startCell = getPaneFromCoordinate(startCoordinates);
        StackPane endCell = getPaneFromCoordinate(endCoordinates);
        this.lastStart = startCell;
        this.lastEnd = endCell;
        boolean enpassant = Game.moveList.get(Game.moveList.size() - 1).contains("x") && endCell.getChildren().size() == 1;
        Button movingPiece = (Button) startCell.getChildren().remove(1);
        if (opponentMove.matches("[0-9]{2}\\.[0-9]{2}[A-R]")) {
            String piece = (GameStates.iAmWhite() ? "b" : "w") + opponentMove.charAt(opponentMove.length() - 1);
            System.out.println(piece);
            setPromotedPiece(piece, movingPiece);
        }
        endCell.getChildren().add(movingPiece);
        if (endCell.getChildren().size() == 3) {
            endCell.getChildren().remove(1);
        }
        if (enpassant) {
            StackPane removablePawn = getPaneFromCoordinate(new IntIntPair(GridPane.getRowIndex(endCell) - 1, GridPane.getColumnIndex(endCell)));
            removablePawn.getChildren().remove(1);
        }
        clearHighlighting();
    }

    private String generateMove(IntIntPair destinationSquare, StackPane cell) {
        String file = "";
        boolean isWhite = GameStates.iAmWhite();
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
                if (isWhite) {
                    rank = 8 - Objects.requireNonNullElse(GridPane.getRowIndex(selectedPiece.getParent()), 0);
                } else {
                    rank = Objects.requireNonNullElse(GridPane.getRowIndex(selectedPiece.getParent()), 0) + 1;
                }
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
        if (movedPiece.isEmpty() && !move.contains("x") && (destinationSquare.column() != pawnFile || destinationSquare.row() >= pawnRank)) {
            move = "wrong";
        }
        if (movedPiece.isEmpty() && move.contains("x") && (Math.abs(destinationSquare.column() - pawnFile) != 1 || destinationSquare.row() + 1 != pawnRank)) {
            move = "wrong";
        }
        if (movedPiece.isEmpty() && !move.contains("x")) { // en passant
            if (startingSquare.row() == 3 && destinationSquare.row() == 2 && Math.abs(destinationSquare.column() - startingSquare.column()) == 1) {
                try {
                    if (isWhite && (Game.board[startingSquare.row()][startingSquare.column() - 1].equals("p"))) {
                        move = file + "x" + cell.getAccessibleText();
                    } else if (!isWhite && (Game.board[7 - startingSquare.row()][7 - startingSquare.column() - 1].equals("P"))) {
                        move = file + "x" + cell.getAccessibleText();
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }
                try {
                    if (isWhite && Game.board[startingSquare.row()][startingSquare.column() + 1].equals("p")) {
                        move = movedPiece + file + "x" + cell.getAccessibleText();
                    } else if (!isWhite && (Game.board[7 - startingSquare.row()][7 - startingSquare.column() + 1].equals("P"))) {
                        move = file + "x" + cell.getAccessibleText();
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }
        }
        if (Game.board[7][4].equals("K") && (move.equals("Kg1") || move.equals("Kxh1"))
                || Game.board[0][4].equals("k") && (move.equals("Kg8") || move.equals("Kxh8"))) {
            move = "O-O";
        } else if (Game.board[7][4].equals("K") && (move.equals("Kc1") || move.equals("Kb1") || move.equals("Kxa1"))
                || Game.board[0][4].equals("k") && (move.equals("Kb8") || move.equals("Kc8") || move.equals("Kxa8"))) {
            move = "O-O-O";
        }
        if (move.matches("([a-h]x)?[a-h]8") || move.matches("([a-h]x)?[a-h]1")) {
            Button movingButton = (Button) getPaneFromCoordinate(startingSquare).getChildren().get(1);
            FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("PawnPromotion.fxml"));
            try {
                Stage mainStage = (Stage) this.chessboardGrid.getScene().getWindow();
                Scene scene = new Scene(fxmlLoader.load());
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.getDialogPane().setContent(scene.getRoot());
                dialog.initOwner(mainStage);
                dialog.initModality(Modality.WINDOW_MODAL);
                dialog.initStyle(StageStyle.UNDECORATED);
                movingButton.getGraphic().setOpacity(0.5);
                dialog.showAndWait();
                move += "=" + ApplicationData.getInstance().getPromotedPiece();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return move;
    }

    private void sendMessageToClientOrServer(String message) {
        myTurn = false;
        if (GameStates.isServer()) {
            ApplicationData.getInstance().getServer().sendMessageToClient(message);
        } else {
            ApplicationData.getInstance().getClient().sendMessageToServer(message);
        }
        if (message.matches("[0-9]{2}\\.[0-9]{2}[A-Q]?")) {
            String coordinate = message.replaceAll("[^0-9]", "");
            System.out.println("Message: " + message + "coordinate: " + coordinate);
            IntIntPair startPair = new IntIntPair(Character.getNumericValue(coordinate.charAt(0)), Character.getNumericValue(coordinate.charAt(1)));
            System.out.println("start " + startPair);
            IntIntPair endPair = new IntIntPair(Character.getNumericValue(coordinate.charAt(2)), Character.getNumericValue(coordinate.charAt(3)));
            System.out.println("endpair " + endPair);
            StackPane start = getPaneFromCoordinate(startPair);
            StackPane end = getPaneFromCoordinate(endPair);
            this.lastStart = start;
            this.lastEnd = end;
            highlightLastMove(start, end);
        }
    }

    private void handleMoveTransmission(IntIntPair destinationSquare) {
        sendMessageToClientOrServer(move);
        String message = startingSquare.toString() + "." + destinationSquare;
        if (move.contains("=")) {
            message += move.charAt(move.length() - 1);
        }
        sendMessageToClientOrServer(message);
        if (move.equals("O-O")) {
            String castlingMove = GameStates.isServer() && GameStates.isServerWhite() || !GameStates.isServer() && !GameStates.isServerWhite()
                    ? "77.75" : "70.72";
            sendMessageToClientOrServer(castlingMove);
        } else if (move.equals("O-O-O")) {
            String castlingMove = GameStates.isServer() && GameStates.isServerWhite() || !GameStates.isServer() && !GameStates.isServerWhite()
                    ? "70.73" : "77.74";
            sendMessageToClientOrServer(castlingMove);
        }
    }

    private void applyMoveToBoardAndUI(StackPane cell) {
        if (!ApplicationData.getInstance().isIllegalMove() && !move.equals("O-O") && !move.equals("O-O-O")) {
            boolean enpassant = move.contains("x") && cell.getChildren().size() == 1;
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
                setPromotedPiece(piece, selectedPiece);
                ((StackPane) selectedPiece.getParent()).getChildren().remove(selectedPiece);
                cell.getChildren().add(selectedPiece);
                setButtonListeners(selectedPiece);
            } else {
                ((StackPane) selectedPiece.getParent()).getChildren().remove(selectedPiece);
                if (enpassant) {
                    StackPane removablePawn = getPaneFromCoordinate(new IntIntPair(GridPane.getRowIndex(cell) + 1, Objects.requireNonNullElse(GridPane.getColumnIndex(cell), 0)));
                    removablePawn.getChildren().remove(1);
                }
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
                StackPane startPane = getPaneFromCoordinate(new IntIntPair(7, 4));
                if (startPane.getChildren().size() > 1) {
                    startPane.getChildren().remove(1);
                }
                StackPane endPane = getPaneFromCoordinate(new IntIntPair(7, move.equals("O-O") ? 7 : 0));
                if (endPane.getChildren().size() > 1) {
                    endPane.getChildren().remove(1);
                }
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
                StackPane startPane = getPaneFromCoordinate(new IntIntPair(7, 3));
                if (startPane.getChildren().size() > 1) {
                    startPane.getChildren().remove(1);
                }
                StackPane endPane = getPaneFromCoordinate(new IntIntPair(7, move.equals("O-O") ? 0 : 7));
                if (endPane.getChildren().size() > 1) {
                    endPane.getChildren().remove(1);
                }
            }
        }
    }

    private void setPromotedPiece(String piece, Button selectedPiece) {
        Image promotion = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + theme.getPiecesPath() + piece + ".png")));
        ImageView h = new ImageView(promotion);
        h.setFitHeight(50);
        h.setFitWidth(50);
        selectedPiece.setGraphic(h);
        selectedPiece.setAccessibleText(piece);
        selectedPiece.setId(piece);
    }

    private boolean isLegalDragDrop() {
        return !ApplicationData.getInstance().isIllegalMove() && this.destinationsSquare != null && !this.destinationsSquare.equals(this.startingSquare) && !move.equals("wrong");
    }

    private boolean isMyPiece() {
        int rank = GameStates.iAmWhite() ? startingSquare.row() : 7 - startingSquare.row();
        int file = GameStates.iAmWhite() ? startingSquare.column() : 7 - startingSquare.column();
        if (GameStates.iAmWhite()) {
            return Game.board[rank][file].matches("[BQRKNP]");
        } else {
            return Game.board[rank][file].matches("[bqrknp]");
        }
    }

    private void handleDragDone(DragEvent event) {
        if (!GameStates.isGameOver()) {
            if (GameStates.isIsMyTurn() || !isLegalDragDrop()) {
                new SoundPlayer().playIllegalMoveSound();
                clearHighlighting();
            }
        }
        updateCheckStatus();
    }

    private void handleButtonClick(ActionEvent event, Button currentButton) {
        if (!GameStates.isGameOver()) {
            clearHighlighting();
            Node button = (Node) event.getSource();
            StackPane square = (StackPane) button.getParent();
            int rank = Objects.requireNonNullElse(GridPane.getRowIndex(square), 0);
            int file = Objects.requireNonNullElse(GridPane.getColumnIndex(square), 0);
            boolean isWhite = GameStates.iAmWhite();
            if (!isWhite) {
                rank = 7 - rank;
                file = 7 - file;
            }
            boolean myPiece = GameStates.iAmWhite() && Character.isUpperCase(Game.board[rank][file].charAt(0))
                    || !GameStates.iAmWhite() && Character.isLowerCase(Game.board[rank][file].charAt(0));
            if (myPiece) {
                // this.startingSquare = new IntIntPair(rank, file);
                this.selectedPiece = (Button) button;
                square.getChildren().get(0).setStyle((rank + file) % 2 == 0 ? theme.getLastMoveLight() : theme.getLastMoveDark());
                startingSquare = new IntIntPair(Objects.requireNonNullElse(GridPane.getRowIndex(currentButton.getParent()), 0), Objects.requireNonNullElse(GridPane.getColumnIndex(currentButton.getParent()), 0));
                initMove(currentButton);
            } else {
                clearHighlighting();
                IntIntPair destinationSquare = new IntIntPair(Objects.requireNonNullElse(GridPane.getRowIndex(square), 0), Objects.requireNonNullElse(GridPane.getColumnIndex(square), 0));
                if (possibleSquares != null && possibleSquares.contains(destinationSquare.toString())) {
                    this.destinationsSquare = destinationSquare;
                    String move = generateMove(destinationSquare, square);
                    if (ApplicationData.getInstance().isIllegalMove()) return;
                    if (move.equals("wrong")) return;
                    handleMoveTransmission(destinationSquare);
                    applyMoveToBoardAndUI(square);
                    selectedPiece = null;
                }
                updateCheckStatus();
            }
        }
    }
}
