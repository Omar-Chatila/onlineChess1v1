package chessModel;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("CallToPrintStackTrace")
public class Game {
    public static List<String> moveList = new ArrayList<>();
    public static String[][] board = new String[8][8];

    private static String[][] movePieces(String move, boolean white) throws Exception { // Fehlermeldung bei falschen Zug fehlt , check fehlt
        boolean legal = true;
        String currentPiece = Character.toString(move.charAt(0));
        if (!currentPiece.matches("[NKBRQ]")) {
            legal = PawnMoveTracker.movePawn(board, move, white);
        } else if (currentPiece.matches("[N]")) {
            legal = KnightMoveTracker.validateKnight(board, move, white);
        } else if (currentPiece.matches("[R]")) {
            legal = RookMoveTracker.validateRook(board, move, white);
        } else if (currentPiece.matches("[B]")) {
            legal = BishopMoveTracker.validateBishop(board, move, white);
        } else if (currentPiece.matches("[Q]")) {
            legal = QueenMoveTracker.validateQueen(board, move, white);
        } else if (currentPiece.matches("[K]")) {
            legal = KingMoveTracker.validateKing(board, move, white);
        }
        if (!legal) {
            throw new Exception("Illegal Move: " + move);
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

    public static void map(String[][] board) {
        String[][] chessBoard = new String[8][8];
        boolean white = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                white = !white;
                switch (board[i][j]) {
                    case "p" -> chessBoard[i][j] = "♟";
                    case "k" -> chessBoard[i][j] = "♚";
                    case "b" -> chessBoard[i][j] = "♝";
                    case "q" -> chessBoard[i][j] = "♛";
                    case "r" -> chessBoard[i][j] = "♜";
                    case "n" -> chessBoard[i][j] = "♞";
                    case "P" -> chessBoard[i][j] = "♙";
                    case "K" -> chessBoard[i][j] = "♔";
                    case "B" -> chessBoard[i][j] = "♗";
                    case "Q" -> chessBoard[i][j] = "♕";
                    case "R" -> chessBoard[i][j] = "♖";
                    case "N" -> chessBoard[i][j] = "♘";
                    default -> {
                        if (white) {
                            chessBoard[i][j] = "□";
                        } else {
                            chessBoard[i][j] = "■";
                        }
                    }
                }
            }
        }
        print(chessBoard);
    }

    public static void executeMove(String move, boolean white) {
        try {
            moveList.add(move);
            map(movePieces(move, white));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
