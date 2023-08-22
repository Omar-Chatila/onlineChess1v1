package chessModel;

import java.util.ArrayList;
import java.util.List;

import static util.GameHelper.copyBoard;

public class RookMoveTracker {
    private static final int[] dx = {0, 0, -1, 1};
    private static final int[] dy = {-1, 1, 0, 0};

    public static boolean validateRook(String[][] board, String move, boolean white) {
        if (!move.contains("x")) {
            int file = move.charAt(1) - 'a';
            int rank = 8 - Character.getNumericValue(move.charAt(2));
            return validateRookHelper(board, rank, file, white);
        } else {
            int file = move.charAt(2) - 'a';
            int rank = 8 - Character.getNumericValue(move.charAt(3));
            if (!white && board[rank][file].matches("[NPKBRQ]")) {
                return validateRookHelper(board, rank, file, false);
            } else if (white && board[rank][file].matches("[npkqrb]")) {
                return validateRookHelper(board, rank, file, true);
            }
        }
        return false;
    }

    private static boolean validateRookHelper(String[][] board, int rank, int file, boolean white) {
        for (int d = 0; d < 4; d++) {
            int i = 1;
            while (isValidSquare(rank + i * dy[d], file + i * dx[d])) {
                String squareContent = board[rank + i * dy[d]][file + i * dx[d]];
                if (squareContent.matches("R") && white) {
                    board[rank + i * dy[d]][file + i * dx[d]] = ".";
                    board[rank][file] = "R";
                    return !Game.kingChecked(true);
                } else if (squareContent.matches("r") && !white) {
                    board[rank + i * dy[d]][file + i * dx[d]] = ".";
                    board[rank][file] = "r";
                    return !Game.kingChecked(false);
                } else if (!squareContent.matches("[rR.]")) {
                    break;
                }
                i++;
            }
        }
        return false;
    }

    public static boolean checksKing(String[][] board, int rank, int file, boolean white) {
        for (int d = 0; d < 4; d++) {
            int i = 1;
            while (isValidSquare(rank + i * dy[d], file + i * dx[d])) {
                String squareContent = board[rank + i * dy[d]][file + i * dx[d]];
                if (!white && squareContent.equals("k")) {
                    return true;
                } else if (white && squareContent.equals("K")) {
                    return true;
                } else if (!squareContent.equals(".")) {
                    break;
                }
                i++;
            }
        }
        return false;
    }

    public static List<String> possibleMoves(String[][] board, int rank, int file, boolean white) {
        if (!white) {
            rank = 7 - rank;
            file = 7 - file;
        }
        List<String> moves = new ArrayList<>();
        String[][] copy = copyBoard(board);
        for (int d = 0; d < 4; d++) {
            int i = 1;
            while (isValidSquare(rank + i * dy[d], file + i * dx[d])) {
                String squareContent = copy[rank + i * dy[d]][file + i * dx[d]];
                String toAdd = (rank + i * dy[d]) + "" + (file + i * dx[d]);
                if (white && squareContent.matches("[PQRBNK]")) break;
                if (!white && squareContent.matches("[pqrbnk]")) break;
                if (squareContent.equals(".")) {
                    copy[rank + i * dy[d]][file + i * dx[d]] = white ? "R" : "r";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(white, copy)) {
                        if (white) {
                            moves.add(toAdd);
                        } else {
                            moves.add((7 - (rank + i * dy[d])) + "" + (7 - (file + i * dx[d])));
                        }
                    }
                } else if (white && squareContent.matches("[bqrnp]")) {
                    if (!Game.kingChecked(true, copy))
                        moves.add(toAdd);
                    break;
                } else if (!white && squareContent.matches("[BQRNP]")) {
                    if (!Game.kingChecked(false, copy))
                        moves.add((7 - (rank + i * dy[d])) + "" + (7 - (file + i * dx[d])));
                    break;
                } else {
                    break;
                }
                i++;
                copy = copyBoard(board);
            }
        }
        return moves;
    }

    private static boolean isValidSquare(int rank, int file) {
        return rank >= 0 && rank < 8 && file >= 0 && file < 8;
    }
}
