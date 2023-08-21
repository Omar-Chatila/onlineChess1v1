package chessModel;

import java.util.ArrayList;
import java.util.List;

import static util.GameHelper.copyBoard;
import static util.GameHelper.print;

public class KingMoveTracker { // TODO check if king is in other king's space
    private static final int[] dx = {-1, 1, -1, 1, 0, 0, 1, -1};
    private static final int[] dy = {-1, 1, 1, -1, 1, -1, 0, 0};
    private static boolean kingHasMoved;

    public static boolean validateKing(String[][] board, String move, boolean white) {
        if (move.equals("O-O")) {
            if (hasShortCastlingRight(white)) {
                if (white) {
                    Game.board[7][6] = "K";
                    Game.board[7][5] = "R";
                    Game.board[7][4] = ".";
                    Game.board[7][7] = ".";
                    return !Game.kingChecked(true);
                } else {
                    Game.board[0][6] = "k";
                    Game.board[0][5] = "r";
                    Game.board[0][4] = ".";
                    Game.board[0][7] = ".";
                    return !Game.kingChecked(false);
                }
            }
            return false;
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
                    kingHasMoved = true;
                    return !Game.kingChecked(true);
                } else if (squareContent.matches("k") && !white) {
                    board[rank + i * dy[d]][file + i * dx[d]] = ".";
                    board[rank][file] = "k";
                    kingHasMoved = true;
                    return !Game.kingChecked(false);
                }
            }
        }
        return false;
    }

    private static boolean hasShortCastlingRight(boolean white) {
        boolean freeSpace = white ? Game.board[7][4].equals("K") && Game.board[7][5].equals(".") && Game.board[7][6].equals(".") && Game.board[7][7].equals("R")
                : Game.board[0][4].equals("k") && Game.board[0][5].equals(".") && Game.board[0][6].equals(".") && Game.board[0][7].equals("r");
        return !kingHasMoved && !Game.kingChecked(white) && freeSpace;
    }

    public static List<String> possibleMoves(String[][] board, int rank, int file, boolean white) {
        if (!white) {
            rank = 7 - rank;
            file = 7 - file;
        }
        List<String> moves = new ArrayList<>();
        String[][] copy = copyBoard(board);
        print(copy);
        for (int d = 0; d < 8; d++) {
            int i = 1;
            if (isValidSquare(rank + i * dy[d], file + i * dx[d])) {
                String squareContent = copy[rank + i * dy[d]][file + i * dx[d]];
                String toAdd = (rank + i * dy[d]) + "" + (file + i * dx[d]);
                if (squareContent.equals(".")) {
                    copy[rank + i * dy[d]][file + i * dx[d]] = white ? "K" : "k";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(white, copy)) {
                        if (white) {
                            moves.add(toAdd);
                        } else {
                            moves.add((7 - (rank + i * dy[d])) + "" + (7 - (file + i * dx[d])));
                        }
                    }
                } else if (white && squareContent.matches("[bqrnp]")) {
                    moves.add(toAdd);
                    break;
                } else if (!white && squareContent.matches("[BQRNP]")) {
                    moves.add((7 - (rank + i * dy[d])) + "" + (7 - (file + i * dx[d])));
                    break;
                } else {
                    break;
                }
                copy = copyBoard(board);
            }
        }
        return moves;
    }

    private static boolean isValidSquare(int rank, int file) {
        return rank >= 0 && rank < 8 && file >= 0 && file < 8;
    }
}
