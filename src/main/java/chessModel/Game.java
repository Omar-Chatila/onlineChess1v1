package chessModel;

import Exceptions.IllegalMoveException;
import util.ApplicationData;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("CallToPrintStackTrace")
public class Game {
    public static List<String> moveList = new ArrayList<>();
    public static String[][] board = new String[8][8];

    private static String[][] movePieces(String move, boolean white) throws IllegalMoveException {
        boolean legal = true;
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
        if (!legal) {
            ApplicationData.getInstance().setIllegalMove(true);
            throw new IllegalMoveException(move);
        }
        return board;
    }

    public static void initialize(String[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (i != 1 && i != 6) {
                    board[i][j] = ".";
                } else if (i == 1) {
                    board[i][j] = "p";
                } else {
                    board[i][j] = "P";
                }
            }
        }
        board[0][0] = "r";
        board[0][1] = "n";
        board[0][2] = "b";
        board[0][3] = "q";
        board[0][4] = "k";
        board[0][5] = "b";
        board[0][6] = "n";
        board[0][7] = "r";

        board[7][0] = "R";
        board[7][1] = "N";
        board[7][2] = "B";
        board[7][3] = "Q";
        board[7][4] = "K";
        board[7][5] = "B";
        board[7][6] = "N";
        board[7][7] = "R";
    }

    public static void print(String[][] board) {
        System.out.println("-----------------------------");
        for (String[] a : board) {
            for (String s : a) {
                System.out.print(s + " ");
            }
            System.out.println();
        }
        System.out.println("-----------------------------");
        System.out.println();
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

    public static void executeMove(String move, boolean white) {
        try {
            print(movePieces(move, white));
            moveList.add(move);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
