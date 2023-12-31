package chessModel;

import java.util.ArrayList;
import java.util.List;

import static util.GameHelper.copyBoard;

public class RookMoveTracker {
    private static final int[] dx = {0, 0, -1, 1};
    private static final int[] dy = {-1, 1, 0, 0};

    public static boolean validateRook(String[][] board, String move, boolean white) {
        if (!move.contains("x") && move.length() == 4) {
            if (Character.isDigit(move.charAt(1))) {
                int movingRank = 8 - Character.getNumericValue(move.charAt(1));
                int file = move.charAt(2) - 'a';
                int rank = 8 - Character.getNumericValue(move.charAt(3));
                return validateRookHelper(board, rank, file, white, 100, movingRank);
            } else {
                int movingFile = move.charAt(1) - 'a';
                int file = move.charAt(2) - 'a';
                int rank = 8 - Character.getNumericValue(move.charAt(3));
                return validateRookHelper(board, rank, file, white, movingFile, 100);
            }
        } else if (move.contains("x") && move.length() == 5) {
            if (Character.isDigit(move.charAt(1))) {
                int movingRank = 8 - Character.getNumericValue(move.charAt(1));
                int file = move.charAt(3) - 'a';
                int rank = 8 - Character.getNumericValue(move.charAt(4));
                if (!white && board[rank][file].matches("[NPKBRQ]"))
                    return validateRookHelper(board, rank, file, false, 100, movingRank);
                else if (white && board[rank][file].matches("[npkqrb]")) {
                    return validateRookHelper(board, rank, file, true, 100, movingRank);
                }
            } else {
                int movingFile = move.charAt(1) - 'a';
                int file = move.charAt(3) - 'a';
                int rank = 8 - Character.getNumericValue(move.charAt(4));
                if (!white && board[rank][file].matches("[NPKBRQ]"))
                    return validateRookHelper(board, rank, file, false, movingFile, 10);
                else if (white && board[rank][file].matches("[npkqrb]"))
                    return validateRookHelper(board, rank, file, true, movingFile, 10);
            }
        }
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
        return validateRookHelper(board, rank, file, white, 100, 100);
    }

    private static boolean validateRookHelper(String[][] board, int rank, int file, boolean white, int myFile, int myRank) {
        for (int d = 0; d < 4; d++) {
            int i = 1;
            while (isValidSquare(rank + i * dy[d], file + i * dx[d])) {
                String squareContent = board[rank + i * dy[d]][file + i * dx[d]];
                if (squareContent.matches("R") && white) {
                    if ((myFile > 10 && myRank > 10) || (myFile < 10 && (file + i * dx[d]) == myFile) || (myRank < 10 && (rank + i * dy[d]) == myRank)) {
                        String[][] copy = copyBoard(board);
                        copy[rank + i * dy[d]][file + i * dx[d]] = ".";
                        copy[rank][file] = "R";
                        if(!Game.kingChecked(true, copy)) {
                            Game.board = copy;
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else if (squareContent.matches("r") && !white) {
                    String[][] copy = copyBoard(board);
                    copy[rank + i * dy[d]][file + i * dx[d]] = ".";
                    copy[rank][file] = "r";
                    if(!Game.kingChecked(false, copy)) {
                        Game.board = copy;
                        return true;
                    } else {
                        return false;
                    }
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
                    copy[rank + i * dy[d]][file + i * dx[d]] = "R";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(true, copy))
                        moves.add(toAdd);
                    break;
                } else if (!white && squareContent.matches("[BQRNP]")) {
                    copy[rank + i * dy[d]][file + i * dx[d]] = "r";
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
                            moves.add(((rank + i * dy[d])) + "" + ((file + i * dx[d])));
                        }
                    }
                } else if (white && squareContent.matches("[bqrnp]")) {
                    copy[rank + i * dy[d]][file + i * dx[d]] = "R";
                    copy[rank][file] = ".";
                    if (!Game.kingChecked(true, copy))
                        moves.add(toAdd);
                    break;
                } else if (!white && squareContent.matches("[BQRNP]")) {
                    copy[rank + i * dy[d]][file + i * dx[d]] = "r";
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
