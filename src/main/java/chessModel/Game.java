package chessModel;

import Exceptions.IllegalMoveException;
import com.example.controller.GameStates;
import util.ApplicationData;

import java.util.ArrayList;
import java.util.List;

import static util.GameHelper.print;

@SuppressWarnings("CallToPrintStackTrace")
public class Game {
    public static List<String> moveList = new ArrayList<>();
    public static String[][] board = new String[8][8];

    private static String[][] movePieces(String move, boolean white) throws IllegalMoveException {
        boolean legal = true;
        if (move.equals("O-O") || move.equals("O-O-O")) {
            legal = KingMoveTracker.validateKing(board, move, white);
        } else {
            String currentPiece = Character.toString(move.charAt(0));
            if (!currentPiece.matches("[NKBRQ]")) {
                legal = PawnMoveTracker.movePawn(board, move, white);
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
            throw new IllegalMoveException(move);
        }
        return board;
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
                        case "p" -> checked |= PawnMoveTracker.checksKing(board, rank, file, true);
                    }
                } else {
                    switch (board[rank][file]) {
                        case "Q" -> checked |= QueenMoveTracker.checksKing(board, rank, file, false);
                        case "B" -> checked |= BishopMoveTracker.checksKing(board, rank, file, false);
                        case "N" -> checked |= KnightMoveTracker.checksKing(board, rank, file, false);
                        case "R" -> checked |= RookMoveTracker.checksKing(board, rank, file, false);
                        case "P" -> checked |= PawnMoveTracker.checksKing(board, rank, file, false);
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
            checkMate = !hasMoves(white);
        }
        System.out.println("CheckMate?" + checkMate);
        GameStates.setGameOver(checkMate);
        return checkMate;
    }

    public static boolean stalemated(boolean white) {
        boolean stalemate = false;
        if (!kingChecked(white)) {
            stalemate = !hasMoves(white);
        }
        GameStates.setGameOver(stalemate);
        return stalemate;
    }

    private static boolean hasMoves(boolean white) {
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
                    }
                }
            }
        }
        System.out.println("has moves?: " + hasMoves);
        return hasMoves;
    }

    public static void executeMove(String move, boolean white) {
        try {
            print(movePieces(move, white));
            if (stalemated(!white)) {
                System.out.println("Game over! - Draw");
            }
            if (kingChecked(!white) && checkMated(!white)) {
                System.out.println("Game over! - " + (!white ? "Black won!" : "White won!"));
            }
            moveList.add(move);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
