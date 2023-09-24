package chessModel;

import java.util.ArrayList;
import java.util.List;

import static util.GameHelper.copyBoard;

public class KnightMoveTracker {
    private static final int[] offsetY = {-2, -1, 1, 2, -2, -1, 1, 2};
    private static final int[] offsetX = {-1, -2, -2, -1, 1, 2, 2, 1};

    public static boolean validateKnight(String[][] board, String move, boolean white) {
        int file = move.charAt(move.contains("x") ? 2 : 1) - 'a';
        char x = move.charAt(move.contains("x") ? 3 : 2);
        int rank = 8 - Character.getNumericValue(x);
        int startFile = -1;
        int startRank = -1;
        if (move.matches("N[a-h][a-h][1-8]")) {
            startFile = move.charAt(1) - 'a';
            rank = 8 - Character.getNumericValue(move.charAt(move.contains("x") ? 4 : 3));
            file = x - 'a';
        } else if (move.matches("N[1-8][a-h][1-8]")) {
            startRank = 8 - Character.getNumericValue(move.charAt(1));
            rank = 8 - Character.getNumericValue(move.charAt(move.contains("x") ? 4 : 3));
            file = x - 'a';
        }
        for (int i = 0; i < 8; i++) {
            int rankY = rank + offsetY[i];
            int fileX = file + offsetX[i];
            if (isValidSquare(rankY, fileX)) {
                boolean isWhiteKnight = board[rankY][fileX].equals("N");
                boolean isBlackKnight = board[rankY][fileX].equals("n");
                if ((isWhiteKnight && white || isBlackKnight && !white)
                        && ((move.contains("x") && white && board[rank][file].matches("[npkqrb]")
                        || move.contains("x") && !white && board[rank][file].matches("[NPKBRQ]"))
                        || !move.contains("x"))) {
                    if ((move.matches("N[a-h][a-h][1-8]") && fileX == startFile) ||
                            (move.matches("N[1-8][a-h][1-8]") && rankY == startRank) ||
                            (!move.matches("N[a-h][a-h][1-8]") && !move.matches("N[1-8][a-h][1-8]"))) {
                        String[][] copy = copyBoard(board);
                        copy[rankY][fileX] = ".";
                        copy[rank][file] = white ? "N" : "n";
                        if (!Game.kingChecked(white, copy)) {
                            Game.board = copy;
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean checksKing(String[][] board, int rank, int file, boolean white) {
        for (int d = 0; d < 8; d++) {
            if (isValidSquare(rank + offsetY[d], file + offsetX[d])) {
                String squareContent = board[rank + offsetY[d]][file + offsetX[d]];
                if (!white && squareContent.equals("k")) {
                    return true;
                } else if (white && squareContent.equals("K")) {
                    return true;
                }
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
        for (int d = 0; d < 8; d++) {
            if (isValidSquare(rank + offsetY[d], file + offsetX[d])) {
                String squareContent = copy[rank + offsetY[d]][file + offsetX[d]];
                String toAdd = (rank + offsetY[d]) + "" + (file + offsetX[d]);
                if (white && squareContent.matches("[PQRBNK]")) continue;
                if (!white && squareContent.matches("[pqrbnk]")) continue;
                if (squareContent.equals(".")) {
                    copy[rank + offsetY[d]][file + offsetX[d]] = white ? "N" : "n";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(white, copy)) {
                        if (white) {
                            moves.add(toAdd);
                        } else {
                            moves.add((7 - (rank + offsetY[d])) + "" + (7 - (file + offsetX[d])));
                        }
                    }
                } else if (white && squareContent.matches("[bqrnp]")) {
                    copy[rank + offsetY[d]][file + offsetX[d]] = "N";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(true, copy))
                        moves.add(toAdd);
                } else if (!white && squareContent.matches("[BQRNP]")) {
                    copy[rank + offsetY[d]][file + offsetX[d]] = "n";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(false, copy))
                        moves.add((7 - (rank + offsetY[d])) + "" + (7 - (file + offsetX[d])));
                }
                copy = copyBoard(board);
            }
        }
        return moves;
    }

    public static List<String> possibleMovesLogic(String[][] board, int rank, int file, boolean white) {
        List<String> moves = new ArrayList<>();
        String[][] copy = copyBoard(board);
        for (int d = 0; d < 8; d++) {
            if (isValidSquare(rank + offsetY[d], file + offsetX[d])) {
                String squareContent = copy[rank + offsetY[d]][file + offsetX[d]];
                String toAdd = (rank + offsetY[d]) + "" + (file + offsetX[d]);
                if (white && squareContent.matches("[PQRBNK]")) continue;
                if (!white && squareContent.matches("[pqrbnk]")) continue;
                if (squareContent.equals(".")) {
                    copy[rank + offsetY[d]][file + offsetX[d]] = white ? "N" : "n";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(white, copy)) {
                        if (white) {
                            moves.add(toAdd);
                        } else {
                            moves.add(((rank + offsetY[d])) + "" + ((file + offsetX[d])));
                        }
                    }
                } else if (white && squareContent.matches("[bqrnp]")) {
                    copy[rank + offsetY[d]][file + offsetX[d]] = "N";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(true, copy))
                        moves.add(toAdd);
                } else if (!white && squareContent.matches("[BQRNP]")) {
                    copy[rank + offsetY[d]][file + offsetX[d]] = "n";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(false, copy))
                        moves.add(((rank + offsetY[d])) + "" + ((file + offsetX[d])));
                }
            }
            copy = copyBoard(board);
        }
        return moves;
    }

    private static boolean isValidSquare(int rank, int file) {
        return rank >= 0 && rank < 8 && file >= 0 && file < 8;
    }
}
