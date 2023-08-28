package chessModel;

import Exceptions.IllegalMoveException;
import com.example.controller.GameStates;
import util.ApplicationData;
import util.IntIntPair;

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
            checkMate = hasNoMoves(white);
        }
        System.out.println("CheckMate?" + checkMate);
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
                        case "p" -> hasMoves |= !PawnMoveTracker.possibleMovesLogic(board, rank, file, false).isEmpty();
                    }
                }
            }
        }
        System.out.println("has moves?: " + hasMoves);
        return !hasMoves;
    }

    public static boolean isAmbiguousMove(String move, boolean white, IntIntPair destinationSquare) {
        int file = destinationSquare.getColumn();
        int row = destinationSquare.getRow();
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

    public static boolean pieceOnSameRank(String move, boolean white, IntIntPair destinationSquare) {
        int file = destinationSquare.getColumn();
        int row = destinationSquare.getRow();
        if (!white) {
            row = 7 - row;
            file = 7 - file;
        }
        int found = 0;
        char piece = move.charAt(0);
        List<List<String>> allMoves = new ArrayList<>();
        for (int c = 0; c < 8; c++) {
            switch (piece) {
                case 'R' -> {
                    if (board[row][c].equals((white) ? "R" : "r")) {
                        allMoves.add(RookMoveTracker.possibleMovesLogic(board, row, c, white));
                    }
                }
                case 'N' -> {
                    if (board[row][c].equals((white) ? "N" : "n")) {
                        allMoves.add(KnightMoveTracker.possibleMovesLogic(board, row, c, white));
                    }
                }
                case 'Q' -> {
                    if (board[row][c].equals((white) ? "Q" : "q")) {
                        allMoves.add(QueenMoveTracker.possibleMovesLogic(board, row, c, white));
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
            System.out.println(moveList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
