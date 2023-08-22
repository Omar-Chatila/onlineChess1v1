package chessModel;

import java.util.ArrayList;
import java.util.List;

import static util.GameHelper.copyBoard;

public class QueenMoveTracker {
    private static final int[] dx = {-1, 1, -1, 1, 0, 0, 1, -1};
    private static final int[] dy = {-1, 1, 1, -1, 1, -1, 0, 0};

    public static boolean validateQueen(String[][] board, String move, boolean white) {
        if (!move.contains("x")) {
            int file = move.charAt(1) - 'a';
            int rank = 8 - Character.getNumericValue(move.charAt(2));
            return validateQueenHelper(board, rank, file, white);
        } else {
            int file = move.charAt(2) - 'a';
            int rank = 8 - Character.getNumericValue(move.charAt(3));
            if (!white && board[rank][file].matches("[NPKBRQ]")) {
                return validateQueenHelper(board, rank, file, false);
            } else if (white && board[rank][file].matches("[npkqrb]")) {
                return validateQueenHelper(board, rank, file, true);
            }
        }
        return false;
    }

    private static boolean validateQueenHelper(String[][] board, int rank, int file, boolean white) {
        for (int d = 0; d < 8; d++) {
            int i = 1;
            while (isValidSquare(rank + i * dy[d], file + i * dx[d])) {
                String squareContent = board[rank + i * dy[d]][file + i * dx[d]];
                if (squareContent.matches("Q") && white) {
                    board[rank + i * dy[d]][file + i * dx[d]] = ".";
                    board[rank][file] = "Q";
                    return !Game.kingChecked(true);
                } else if (squareContent.matches("q") && !white) {
                    board[rank + i * dy[d]][file + i * dx[d]] = ".";
                    board[rank][file] = "q";
                    return !Game.kingChecked(false);
                } else if (!squareContent.matches("[qQ.]")) {
                    break;
                }
                i++;
            }
        }
        return false;
    }

    public static boolean checksKing(String[][] board, int rank, int file, boolean white) {
        for (int d = 0; d < 8; d++) {
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
        for (int d = 0; d < 8; d++) {
            int i = 1;
            while (isValidSquare(rank + i * dy[d], file + i * dx[d])) {
                String squareContent = copy[rank + i * dy[d]][file + i * dx[d]];
                String toAdd = (rank + i * dy[d]) + "" + (file + i * dx[d]);
                if (white && squareContent.matches("[PQRBNK]")) break;
                if (!white && squareContent.matches("[pqrbnk]")) break;
                if (squareContent.equals(".")) {
                    copy[rank + i * dy[d]][file + i * dx[d]] = white ? "Q" : "q";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(white, copy)) {
                        if (white) {
                            moves.add(toAdd);
                        } else {
                            moves.add((7 - (rank + i * dy[d])) + "" + (7 - (file + i * dx[d])));
                        }
                    }
                } else if (white && squareContent.matches("[bqrnp]")) {
                    copy[rank + i * dy[d]][file + i * dx[d]] = "Q";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(true, copy))
                        moves.add(toAdd);
                    break;
                } else if (!white && squareContent.matches("[BQRNP]")) {
                    copy[rank + i * dy[d]][file + i * dx[d]] = "q";
                    copy[rank][file] = ".";
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

    public static List<String> possibleMovesLogic(String[][] board, int rank, int file, boolean white) {
        List<String> moves = new ArrayList<>();
        String[][] copy = copyBoard(board);
        for (int d = 0; d < 8; d++) {
            int i = 1;
            while (isValidSquare(rank + i * dy[d], file + i * dx[d])) {
                String squareContent = copy[rank + i * dy[d]][file + i * dx[d]];
                String toAdd = (rank + i * dy[d]) + "" + (file + i * dx[d]);
                if (white && squareContent.matches("[PQRBNK]")) break;
                if (!white && squareContent.matches("[pqrbnk]")) break;
                if (squareContent.equals(".")) {
                    copy[rank + i * dy[d]][file + i * dx[d]] = white ? "Q" : "q";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(white, copy)) {
                        if (white) {
                            moves.add(toAdd);
                        } else {
                            moves.add(((rank + i * dy[d])) + "" + ((file + i * dx[d])));
                        }
                    }
                } else if (white && squareContent.matches("[bqrnp]")) {
                    copy[rank + i * dy[d]][file + i * dx[d]] = "Q";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(true, copy))
                        moves.add(toAdd);
                    break;
                } else if (!white && squareContent.matches("[BQRNP]")) {
                    copy[rank + i * dy[d]][file + i * dx[d]] = "q";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(false, copy))
                        moves.add(((rank + i * dy[d])) + "" + ((file + i * dx[d])));
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
