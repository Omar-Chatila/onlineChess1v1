package chessModel;

import java.util.ArrayList;
import java.util.List;

import static util.GameHelper.copyBoard;

public class KingMoveTracker { // TODO check if king is in other king's space
    private static final int[] dx = {-1, 1, -1, 1, 0, 0, 1, -1};
    private static final int[] dy = {-1, 1, 1, -1, 1, -1, 0, 0};
    private static boolean whiteKingHasMoved;
    private static boolean blackKingHasMoved;


    public static boolean validateKing(String[][] board, String move, boolean white) {
        if (move.equals("O-O")) {
            return castleShort(white);
        }
        if (move.equals("O-O-O")) {
            return castleLong(white);
        }
        if (!move.contains("x")) {
            int file = move.charAt(1) - 'a';
            int rank = 8 - Character.getNumericValue(move.charAt(2));
            return validateKingHelper(board, rank, file, white);
        } else {
            int file = move.charAt(2) - 'a';
            int rank = 8 - Character.getNumericValue(move.charAt(3));
            if (!white && board[rank][file].matches("[NPKBRQ]")) {
                return validateKingHelper(board, rank, file, false);
            } else if (white && board[rank][file].matches("[npkqrb]")) {
                return validateKingHelper(board, rank, file, true);
            }
        }
        return false;
    }

    private static boolean validateKingHelper(String[][] board, int rank, int file, boolean white) {
        for (int d = 0; d < 8; d++) {
            int i = 1;
            if (isValidSquare(rank + i * dy[d], file + i * dx[d])) {
                String squareContent = board[rank + i * dy[d]][file + i * dx[d]];
                if (squareContent.matches("K") && white) {
                    board[rank + i * dy[d]][file + i * dx[d]] = ".";
                    board[rank][file] = "K";
                    whiteKingHasMoved = true;
                    return !Game.kingChecked(true);
                } else if (squareContent.matches("k") && !white) {
                    board[rank + i * dy[d]][file + i * dx[d]] = ".";
                    board[rank][file] = "k";
                    blackKingHasMoved = true;
                    return !Game.kingChecked(false);
                }
            }
        }
        return false;
    }

    private static boolean hasShortCastlingRight(boolean white) {
        boolean freeSpace = white ? Game.board[7][4].equals("K") && Game.board[7][5].equals(".") && Game.board[7][6].equals(".") && Game.board[7][7].equals("R")
                : Game.board[0][4].equals("k") && Game.board[0][5].equals(".") && Game.board[0][6].equals(".") && Game.board[0][7].equals("r");
        if (white)
            return !whiteKingHasMoved && !Game.kingChecked(true) && freeSpace;
        else
            return !blackKingHasMoved && !Game.kingChecked(false) && freeSpace;
    }

    private static boolean hasLongCastlingRight(boolean white) {
        boolean freeSpace = white ? Game.board[7][4].equals("K") && Game.board[7][3].equals(".") && Game.board[7][2].equals(".") && Game.board[7][1].equals(".") && Game.board[7][0].equals("R")
                : Game.board[0][4].equals("k") && Game.board[0][3].equals(".") && Game.board[0][2].equals(".") && Game.board[0][1].equals(".") && Game.board[0][0].equals("r");
        if (white)
            return !whiteKingHasMoved && !Game.kingChecked(true) && freeSpace;
        else
            return !blackKingHasMoved && !Game.kingChecked(false) && freeSpace;
    }

    public static List<String> possibleMoves(String[][] board, int rank, int file, boolean white) {
        if (!white) {
            rank = 7 - rank;
            file = 7 - file;
        }
        List<String> moves = new ArrayList<>();
        String[][] copy = copyBoard(board);
        for (int d = 0; d < 8; d++) {
            if (isValidSquare(rank + dy[d], file + dx[d])) {
                String squareContent = copy[rank + dy[d]][file + dx[d]];
                String toAdd = (rank + dy[d]) + "" + (file + dx[d]);
                if (white && squareContent.matches("[PQRBNK]")) continue;
                if (!white && squareContent.matches("[pqrbnk]")) continue;
                if ((white && squareContent.matches("[.bqrnp]")) || (!white && squareContent.matches("[.BQRNP]"))) {
                    copy[rank + dy[d]][file + dx[d]] = white ? "K" : "k";
                    copy[rank][file] = ".";
                    if (white && !Game.kingChecked(true, copy) || !white && !Game.kingChecked(false, copy)) {
                        if (white) {
                            moves.add(toAdd);
                        } else {
                            moves.add((7 - (rank + dy[d])) + "" + (7 - (file + dx[d])));
                        }
                    }
                }
                copy = copyBoard(board);
            }
        }
        if (white && hasLongCastlingRight(true)) {
            moves.add("72");
            moves.add("71");
            moves.add("70");
        }
        if (white && hasShortCastlingRight(true)) {
            moves.add("76");
            moves.add("77");
        }
        if (!white && hasLongCastlingRight(false)) {
            moves.add("75");
            moves.add("76");
            moves.add("77");
        }
        if (!white && hasShortCastlingRight(false)) {
            moves.add("71");
            moves.add("70");
        }
        return moves;
    }

    public static List<String> possibleMovesLogic(String[][] board, int rank, int file, boolean white) {
        List<String> moves = new ArrayList<>();
        String[][] copy = copyBoard(board);
        for (int d = 0; d < 8; d++) {
            if (isValidSquare(rank + dy[d], file + dx[d])) {
                String squareContent = copy[rank + dy[d]][file + dx[d]];
                String toAdd = (rank + dy[d]) + "" + (file + dx[d]);
                if (white && squareContent.matches("[PQRBNK]")) continue;
                if (!white && squareContent.matches("[pqrbnk]")) continue;
                if ((white && squareContent.matches("[.bqrnp]")) || (!white && squareContent.matches("[.BQRNP]"))) {
                    copy[rank + dy[d]][file + dx[d]] = white ? "K" : "k";
                    copy[rank][file] = ".";
                    if (white && !Game.kingChecked(true, copy) || !white && !Game.kingChecked(false, copy)) {
                        if (white) {
                            moves.add(toAdd);
                        } else {
                            moves.add(((rank + dy[d])) + "" + ((file + dx[d])));
                        }
                    }
                }
                copy = copyBoard(board);
            }
        }
        if (white && hasLongCastlingRight(true)) {
            moves.add("72");
            moves.add("71");
            moves.add("70");
        }
        if (white && hasShortCastlingRight(true)) {
            moves.add("76");
            moves.add("77");
        }
        if (!white && hasLongCastlingRight(false)) {
            moves.add("75");
            moves.add("76");
            moves.add("77");
        }
        if (!white && hasShortCastlingRight(false)) {
            moves.add("71");
            moves.add("70");
        }
        return moves;
    }

    private static boolean castleShort(boolean white) {
        if (hasShortCastlingRight(white)) {
            if (white) {
                Game.board[7][6] = "K";
                Game.board[7][5] = "R";
                Game.board[7][4] = ".";
                Game.board[7][7] = ".";
                String[][] copy = copyBoard(Game.board);
                copy[7][5] = "K";
                copy[7][4] = ".";
                return !Game.kingChecked(true) && !Game.kingChecked(true, copy);
            } else {
                Game.board[0][6] = "k";
                Game.board[0][5] = "r";
                Game.board[0][4] = ".";
                Game.board[0][7] = ".";
                String[][] copy = copyBoard(Game.board);
                copy[0][5] = "K";
                copy[0][4] = ".";
                return !Game.kingChecked(false) && !Game.kingChecked(false, copy);
            }
        }
        return false;
    }

    private static boolean castleLong(boolean white) {
        if (hasLongCastlingRight(white)) {
            if (white) {
                Game.board[7][2] = "K";
                Game.board[7][3] = "R";
                Game.board[7][4] = ".";
                Game.board[7][0] = ".";
                String[][] copy = copyBoard(Game.board);
                copy[7][3] = "K";
                copy[7][4] = ".";
                return !Game.kingChecked(true) && !Game.kingChecked(true, copy);
            } else {
                Game.board[0][2] = "k";
                Game.board[0][3] = "r";
                Game.board[0][4] = ".";
                Game.board[0][0] = ".";
                String[][] copy = copyBoard(Game.board);
                copy[0][3] = "K";
                copy[0][4] = ".";
                return !Game.kingChecked(false) && !Game.kingChecked(false, copy);
            }
        }
        return false;
    }

    private static boolean isValidSquare(int rank, int file) {
        return rank >= 0 && rank < 8 && file >= 0 && file < 8;
    }

}
