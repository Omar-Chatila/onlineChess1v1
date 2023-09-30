package chessModel;

import Exceptions.IllegalMoveException;
import com.example.controller.*;
import javafx.application.Platform;
import util.ApplicationData;
import util.GameHelper;
import util.IntIntPair;
import util.SoundPlayer;

import java.util.ArrayList;
import java.util.List;

import static chessModel.PawnMoveTracker.*;
import static util.GameHelper.copyBoard;
import static util.GameHelper.print;

@SuppressWarnings("CallToPrintStackTrace")
public class Game {
    private static final Graveyard whiteGraveyard = new Graveyard(true);
    private static final Graveyard blackGraveyard = new Graveyard(false);
    public static List<String> moveList = new ArrayList<>();
    public static List<String[][]> playedPositions = new ArrayList<>();
    public static String[][] board = new String[8][8];
    public static boolean drawClaimable;
    private static int fiftyMoveRule;
    private static MovesTableController movesTableController;
    private static int materialDifference;

    private static String[][] movePieces(String move, boolean white) throws IllegalMoveException {
        boolean legal = true;
        resetEnpassant();
        if (move.equals("O-O") || move.equals("O-O-O")) {
            legal = KingMoveTracker.validateKing(board, move, white);
        } else {
            String currentPiece = Character.toString(move.charAt(0));
            if (!currentPiece.matches("[NKBRQ]")) {
                legal = movePawn(board, move, white);
            } else if (currentPiece.matches("N")) {
                legal = KnightMoveTracker.validateKnight(board, move, white);
            } else if (currentPiece.matches("R")) {
                legal = RookMoveTracker.validateRook(board, move, white);
            } else if (currentPiece.matches("B")) {
                legal = BishopMoveTracker.validateBishop(board, move, white);
            } else if (currentPiece.matches("Q")) {
                legal = QueenMoveTracker.validateQueen(board, move, white);
            } else if (currentPiece.matches("K")) {
                legal = KingMoveTracker.validateKing(board, move, white);
            }
        }
        if (!legal) {
            ApplicationData.getInstance().setIllegalMove(true);
            new SoundPlayer().playIllegalMoveSound();
            print(board);
            Platform.runLater(() -> ApplicationData.getInstance().getChessboardController().clearHighlighting());
            throw new IllegalMoveException(move);
        } else {
            playMoveSound(move, white);
            updateGraveyard(move, white);
        }
        return board;
    }

    private static void updateGraveyard(String move, boolean white) {
        if (move.contains("x")) {
            int index = move.indexOf('x');
            int file = move.charAt(index + 1) - 'a';
            int rank = 8 - Character.getNumericValue(move.charAt(index + 2));
            String piece = playedPositions.get(playedPositions.size() - 1)[rank][file].toUpperCase();
            switch (piece) {
                case "Q" -> materialDifference += white ? 9 : -9;
                case "R" -> materialDifference += white ? 5 : -5;
                case "B", "N" -> materialDifference += white ? 3 : -3;
                case "P" -> materialDifference += white ? 1 : -1;
            }
            Platform.runLater(() -> ApplicationData.getInstance().getWgc().updateDiffLabel());
            Platform.runLater(() -> ApplicationData.getInstance().getBgc().updateDiffLabel());
            if (white) {
                blackGraveyard.addPiece(piece);
            } else {
                whiteGraveyard.addPiece(piece);
            }
        }
    }

    public static boolean kingChecked(boolean white, String[][] board) {
        boolean checked = false;
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                if (white) {
                    switch (board[rank][file]) {
                        case "q" -> checked |= QueenMoveTracker.checksKing(board, rank, file, true);
                        case "b" -> checked |= BishopMoveTracker.checksKing(board, rank, file, true);
                        case "n" -> checked |= KnightMoveTracker.checksKing(board, rank, file, true);
                        case "r" -> checked |= RookMoveTracker.checksKing(board, rank, file, true);
                        case "p" -> checked |= checksKing(board, rank, file, true);
                    }
                } else {
                    switch (board[rank][file]) {
                        case "Q" -> checked |= QueenMoveTracker.checksKing(board, rank, file, false);
                        case "B" -> checked |= BishopMoveTracker.checksKing(board, rank, file, false);
                        case "N" -> checked |= KnightMoveTracker.checksKing(board, rank, file, false);
                        case "R" -> checked |= RookMoveTracker.checksKing(board, rank, file, false);
                        case "P" -> checked |= checksKing(board, rank, file, false);
                    }
                }
            }
            if (checked) break;
        }
        return checked;
    }

    public static boolean kingChecked(boolean white) {
        return kingChecked(white, Game.board);
    }

    public static boolean checkMated(boolean white) {
        boolean checkMate = false;
        if (kingChecked(white)) {
            checkMate = hasNoMoves(white);
        }
        GameStates.setGameOver(checkMate);
        return checkMate;
    }

    public static boolean stalemated(boolean white) {
        boolean stalemate = false;
        if (!kingChecked(white)) {
            stalemate = hasNoMoves(white);
        }
        GameStates.setGameOver(stalemate);
        return stalemate;
    }

    private static boolean hasNoMoves(boolean white) {
        boolean hasMoves = false;
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                if (white) {
                    switch (board[rank][file]) {
                        case "Q" -> hasMoves |= !QueenMoveTracker.possibleMovesLogic(board, rank, file, true).isEmpty();
                        case "B" ->
                                hasMoves |= !BishopMoveTracker.possibleMovesLogic(board, rank, file, true).isEmpty();
                        case "N" ->
                                hasMoves |= !KnightMoveTracker.possibleMovesLogic(board, rank, file, true).isEmpty();
                        case "R" -> hasMoves |= !RookMoveTracker.possibleMovesLogic(board, rank, file, true).isEmpty();
                        case "K" -> hasMoves |= !KingMoveTracker.possibleMovesLogic(board, rank, file, true).isEmpty();
                        case "P" -> hasMoves |= !PawnMoveTracker.possibleMovesLogic(board, rank, file, true).isEmpty();
                    }
                }
            }
        }
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                if (!white) {
                    switch (board[rank][file]) {
                        case "q" ->
                                hasMoves |= !QueenMoveTracker.possibleMovesLogic(board, rank, file, false).isEmpty();
                        case "b" ->
                                hasMoves |= !BishopMoveTracker.possibleMovesLogic(board, rank, file, false).isEmpty();
                        case "n" ->
                                hasMoves |= !KnightMoveTracker.possibleMovesLogic(board, rank, file, false).isEmpty();
                        case "r" -> hasMoves |= !RookMoveTracker.possibleMovesLogic(board, rank, file, false).isEmpty();
                        case "k" -> hasMoves |= !KingMoveTracker.possibleMovesLogic(board, rank, file, false).isEmpty();
                        case "p" ->
                                hasMoves |= !PawnMoveTracker.possibleMovesLogic(board, 7 - rank, 7 - file, false).isEmpty();
                    }
                }
            }
        }
        return !hasMoves;
    }

    public static boolean isAmbiguousMove(String move, boolean white, IntIntPair destinationSquare) {
        int file = destinationSquare.column();
        int row = destinationSquare.row();
        if (!white) {
            file = 7 - file;
            row = 7 - row;
        }
        int count = 0;
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                switch (move.charAt(0)) {
                    case 'R' -> {
                        List<List<String>> allRookMoves = new ArrayList<>();
                        if (board[r][f].equals((white) ? "R" : "r")) {
                            allRookMoves.add(RookMoveTracker.possibleMovesLogic(board, r, f, white));
                        }
                        for (List<String> rookMoves : allRookMoves) {
                            if (rookMoves.contains(row + "" + file)) {
                                count++;
                            }
                        }
                    }
                    case 'N' -> {
                        List<List<String>> allKnightMoves = new ArrayList<>();
                        if (board[r][f].equals((white) ? "N" : "n")) {
                            allKnightMoves.add(KnightMoveTracker.possibleMovesLogic(board, r, f, white));
                        }
                        for (List<String> knightMoves : allKnightMoves) {
                            if (knightMoves.contains(row + "" + file)) {
                                count++;
                            }
                        }
                    }
                    case 'Q' -> {
                        List<List<String>> allQueenMoves = new ArrayList<>();
                        if (board[r][f].equals((white) ? "Q" : "q")) {
                            allQueenMoves.add(QueenMoveTracker.possibleMovesLogic(board, r, f, white));
                        }
                        for (List<String> queenMoves : allQueenMoves) {
                            if (queenMoves.contains(row + "" + file)) {
                                count++;
                            }
                        }
                    }
                }
            }
        }
        return count > 1;
    }

    public static boolean pieceOnSameFile(String move, boolean white, IntIntPair destinationSquare, IntIntPair startingSquare) {
        int file = destinationSquare.column();
        int row = destinationSquare.row();
        int startFile = startingSquare.column();
        if (!white) {
            row = 7 - row;
            file = 7 - file;
            startFile = 7 - startFile;
        }
        int found = 0;
        char piece = move.charAt(0);
        List<List<String>> allMoves = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            switch (piece) {
                case 'R' -> {
                    if (board[r][startFile].equals((white) ? "R" : "r")) {
                        allMoves.add(RookMoveTracker.possibleMovesLogic(board, r, startFile, white));
                    }
                }
                case 'N' -> {
                    if (board[r][startFile].equals((white) ? "N" : "n")) {
                        allMoves.add(KnightMoveTracker.possibleMovesLogic(board, r, startFile, white));
                    }
                }
                case 'Q' -> {
                    if (board[r][startFile].equals((white) ? "Q" : "q")) {
                        allMoves.add(QueenMoveTracker.possibleMovesLogic(board, r, startFile, white));
                    }
                }
            }
        }
        for (List<String> moveList : allMoves) {
            if (moveList.contains(row + "" + file)) {
                found++;
            }
        }
        return found > 1;
    }

    private static boolean isThreefoldRepetition() {
        int tally = 1;
        for (String[][] b : playedPositions) {
            if (GameHelper.boardEquals(board, b)) {
                tally++;
            }
        }
        return tally >= 3;
    }

    private static boolean reset50moveRule(String move) {
        return Character.toString(move.charAt(0)).matches("[a-h]") || move.contains("x");
    }

    private static void playMoveSound(String lastMove, boolean white) {
        if (!white && Game.kingChecked(true) || white && Game.kingChecked(false)) {
            new SoundPlayer().playCheckSound();
        } else if (lastMove.startsWith("O")) {
            new SoundPlayer().playCastleSound();
        } else if (lastMove.contains("x")) {
            new SoundPlayer().playCaptureSound();
        } else if (lastMove.contains("=")) {
            new SoundPlayer().playPromotionSound();
        } else {
            if (GameStates.isIsMyTurn()) {
                new SoundPlayer().playMoveSelfSound();
            } else {
                new SoundPlayer().playOpponentMoveSound();
            }
        }
    }

    public static void executeMove(String move, boolean white) {
        InfoViewController.doubleClicked = false;
        System.out.println("Current Move:  " + move);
        ServerController.currentPositionNr++;
        ClientController.currentPositionNr++;
        if (movesTableController == null) {
            if (GameStates.isServer()) {
                movesTableController = ServerController.getMtc();
            } else {
                movesTableController = ClientController.getMtc();
            }
        }
        try {
            print(movePieces(move, white));
            updateMovesTable(move, white);
            if (stalemated(!white)) {
                System.out.println("Game over! - Draw by stalemate");
                ApplicationData.getInstance().getIvc().updateInfoText("Game over! - Draw by stalemate");
                ApplicationData.getInstance().getIvc().showWinner(white);
                ApplicationData.getInstance().getIvc().showWinner(!white);
            }
            if (isThreefoldRepetition()) {
                System.out.println("3-Fold repetition: One player can claim draw");
                ApplicationData.getInstance().getIvc().updateInfoText("3-Fold repetition: One player can claim draw");
                drawClaimable = true;
                // ApplicationData.getInstance().getIvc().getOfferDraw().fire();
            }
            if (kingChecked(!white) && checkMated(!white)) {
                ApplicationData.getInstance().getIvc().updateInfoText("Game over! - " + (!white ? "Black won!" : "White won!"));
                ApplicationData.getInstance().getIvc().showWinner(white);
                System.out.println("Game over! - " + (!white ? "Black won!" : "White won!"));
            }
            if (reset50moveRule(move)) {
                fiftyMoveRule = 0;
            } else {
                fiftyMoveRule++;
            }
            if (fiftyMoveRule >= 100) {
                ApplicationData.getInstance().getIvc().updateInfoText("50 move rule applied: One player can claim draw!");
                //ApplicationData.getInstance().getIvc().getOfferDraw().fire();
                System.out.println("50 move rule applied: One player can claim draw!");
                drawClaimable = true;
            }
            moveList.add(move);
            playedPositions.add(copyBoard(board));
            if (!GameStates.isGameOver()) {
                ApplicationData.getInstance().getIvc().updateInfoText((white ? "White " : "Black ") + "played: " + move);
            } else {
                new SoundPlayer().playGameEndSound();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateMovesTable(String move, boolean white) {
        if (white) {
            movesTableController.addMove(movesTableController.getCurrentMove(), move, null);
        } else {
            movesTableController.addMove(movesTableController.getCurrentMove(), null, move);
        }
    }

    public static Graveyard getBlackGraveyard() {
        return blackGraveyard;
    }

    public static Graveyard getWhiteGraveyard() {
        return whiteGraveyard;
    }

    public static int getMaterialDifference() {
        return materialDifference;
    }
}
