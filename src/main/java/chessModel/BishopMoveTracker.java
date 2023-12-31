package chessModel;

import java.util.ArrayList;
import java.util.List;

import static util.GameHelper.copyBoard;

public class BishopMoveTracker {
    private static final int[] dx = {-1, 1, -1, 1};
    private static final int[] dy = {-1, 1, 1, -1};

    public static boolean validateBishop(String[][] board, String move, boolean white) {
        if (!move.contains("x")) {
            int file = move.charAt(1) - 'a';
            int rank = 8 - Character.getNumericValue(move.charAt(2));
            return validateBishopHelper(board, rank, file, white);
        } else {
            int file = move.charAt(2) - 'a';
            int rank = 8 - Character.getNumericValue(move.charAt(3));
            if (!white && board[rank][file].matches("[NPKBRQ]")) {
                return validateBishopHelper(board, rank, file, false);
            } else if (white && board[rank][file].matches("[npkqrb]")) {
                return validateBishopHelper(board, rank, file, true);
            }
        }
        return false;
    }

    private static boolean validateBishopHelper(String[][] board, int rank, int file, boolean white) {
        for (int d = 0; d < 4; d++) {
            int i = 1;
            while (isValidSquare(rank + i * dy[d], file + i * dx[d])) {
                String squareContent = board[rank + i * dy[d]][file + i * dx[d]];
                if (squareContent.matches("B") && white) {
                    String[][] copy = copyBoard(board);
                    copy[rank + i * dy[d]][file + i * dx[d]] = ".";
                    copy[rank][file] = "B";
                    if (!Game.kingChecked(true, copy)) {
                        Game.board = copy;
                        return true;
                    } else {
                        return false;
                    }
                } else if (squareContent.matches("b") && !white) {
                    String[][] copy = copyBoard(board);
                    copy[rank + i * dy[d]][file + i * dx[d]] = ".";
                    copy[rank][file] = "b";
                    if (!Game.kingChecked(false, copy)) {
                        Game.board = copy;
                        return true;
                    } else {
                        return false;
                    }
                } else if (!squareContent.matches("[bB.]")) {
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
                    copy[rank + i * dy[d]][file + i * dx[d]] = white ? "B" : "b";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(white, copy)) {
                        if (white) {
                            moves.add(toAdd);
                        } else {
                            moves.add((7 - (rank + i * dy[d])) + "" + (7 - (file + i * dx[d])));
                        }
                    }
                } else {
                    whitesOppPiece(rank, file, white, moves, copy, d, i, squareContent, toAdd);
                    break;
                }
                i++;
                copy = copyBoard(board);
            }
        }
        return moves;
    }

    private static void whitesOppPiece(int rank, int file, boolean white, List<String> moves, String[][] copy, int d, int i, String squareContent, String toAdd) {
        if (white && squareContent.matches("[bqrnp]")) {
            copy[rank + i * dy[d]][file + i * dx[d]] = "B";
            copy[rank][file] = ".";
            if (!Game.kingChecked(true, copy))
                moves.add(toAdd);
        } else if (!white && squareContent.matches("[BQRNP]")) {
            copy[rank + i * dy[d]][file + i * dx[d]] = "b";
            copy[rank][file] = ".";
            if (!Game.kingChecked(false, copy))
                moves.add((7 - (rank + i * dy[d])) + "" + (7 - (file + i * dx[d])));

        }
    }

    public static List<String> possibleMovesLogic(String[][] board, int rank, int file, boolean white) {
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
                    copy[rank + i * dy[d]][file + i * dx[d]] = white ? "B" : "b";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(white, copy)) {
                        if (white) {
                            moves.add(toAdd);
                        } else {
                            moves.add(((rank + i * dy[d])) + "" + ((file + i * dx[d])));
                        }
                    }
                } else whitesOppPiece(rank, file, white, moves, copy, d, i, squareContent, toAdd);
                i++;
                copy = copyBoard(board);
            }
        }
        return moves;
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

    private static boolean isValidSquare(int rank, int file) {
        return rank >= 0 && rank < 8 && file >= 0 && file < 8;
    }
}
