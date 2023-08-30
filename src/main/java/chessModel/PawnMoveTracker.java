package chessModel;

import java.util.ArrayList;
import java.util.List;

import static util.GameHelper.copyBoard;

public class PawnMoveTracker {
    private static final List<String> possibleMovesLogicList = new ArrayList<>();

    private static boolean validatePawn(String[][] board, String move, boolean white) {
        if (!move.contains("x")) {
            int file = move.charAt(0) - 'a', rank = Character.getNumericValue(move.charAt(1));
            if (file < 0 || file > 7 || rank < 0 || rank > 8 || !board[8 - rank][file].equals(".")) return false;
            if (white) {
                if (rank < 3) return false;
                return rank != 4 && board[8 - (rank - 1)][file].equals("P")
                        || rank == 4 && (board[8 - (rank - 1)][file].equals("P") || board[8 - (rank - 2)][file].equals("P"));
            } else {
                if (rank > 6) return false;
                return rank != 5 && board[8 - rank - 1][file].equals("p")
                        || rank == 5 && (board[8 - rank - 1][file].equals("p") || board[8 - rank - 2][file].equals("p"));
            }
        }
        if (move.contains("x") && move.length() == 4) {
            int pawnFile = move.charAt(0) - 'a', capFile = move.charAt(2) - 'a';
            int capRank = 8 - Character.getNumericValue(move.charAt(3));
            int pawnRank = white ? capRank + 1 : capRank - 1;
            if (pawnFile < 0 || pawnFile > 7 || pawnRank < 0 || pawnRank > 7 || capFile < 0 || capFile > 7 || capRank < 0 || capRank > 7)
                return false;
            return (white && board[pawnRank][pawnFile].equals("P") && board[capRank][capFile].matches("[npkqrb]"))
                    || (!white && board[pawnRank][pawnFile].equals("p") && board[capRank][capFile].matches("[NPKQRB]"));
        }

        return false;
    }

    public static boolean movePawn(String[][] board, String move, boolean white) {
        if (move.contains("=")) {
            int file = move.charAt(0) - 'a', rank = 8 - Character.getNumericValue(move.charAt(move.contains("x") ? 3 : 1));
            if (move.contains("x")) {
                String piece = Character.toString(move.charAt(move.length() - 1));
                String newPiece = white ? piece : piece.toLowerCase();
                int capFile = move.charAt(2) - 'a';
                board[rank + (white ? 1 : -1)][file] = ".";
                board[rank][capFile] = newPiece;
            } else {
                board[rank + (white ? 1 : -1)][file] = ".";
                board[rank][file] = white ? Character.toString(move.charAt(3)) : Character.toString(move.charAt(3)).toLowerCase();
            }
            return !Game.kingChecked(white);
        }
        if (validatePawn(board, move, white) && !move.contains("x")) {
            int file = move.charAt(0) - 'a', rank = 8 - Character.getNumericValue(move.charAt(1));
            if (rank == (white ? 4 : 3)) {
                board[rank + (white ? 1 : -1)][file] = ".";
                board[rank + (white ? 2 : -2)][file] = ".";
            } else {
                board[rank + (white ? 1 : -1)][file] = ".";
            }
            board[rank][file] = white ? "P" : "p";
            return !Game.kingChecked(white);
        } else if (validatePawn(board, move, white) && move.contains("x")) {
            int pawnFile = move.charAt(0) - 'a', capFile = move.charAt(2) - 'a';
            int capRank = 8 - Character.getNumericValue(move.charAt(3));
            int pawnRank = white ? capRank + 1 : capRank - 1;
            board[pawnRank][pawnFile] = ".";
            board[capRank][capFile] = white ? "P" : "p";
            return !Game.kingChecked(white);
        }
        return false;
    }

    public static boolean checksKing(String[][] board, int rank, int file, boolean white) {
        if (!white) {
            if (isValidSquare(rank - 1, file - 1) && board[rank - 1][file - 1].equals("k")) {
                return true;
            } else return isValidSquare(rank - 1, file + 1) && board[rank - 1][file + 1].equals("k");
        } else {
            if (isValidSquare(rank + 1, file - 1) && board[rank + 1][file - 1].equals("K")) {
                return true;
            } else return isValidSquare(rank + 1, file + 1) && board[rank + 1][file + 1].equals("K");
        }
    }

    public static List<String> possibleMovesLogic(String[][] board, int rank, int file, boolean white) {
        possibleMoves(board, rank, file, white);
        return possibleMovesLogicList;
    }

    public static List<String> possibleMoves(String[][] board, int rank, int file, boolean white) {
        possibleMovesLogicList.clear();
        if (!white) {
            rank = 7 - rank;
            file = 7 - file;
        }
        List<String> moves = new ArrayList<>();
        String[][] copy = copyBoard(board);
        int startRank = white ? 6 : 1;
        int direction = white ? -1 : 1;
        if (isValidSquare(rank + direction, file) && board[rank + direction][file].equals(".")) {
            copy[rank + direction][file] = white ? "P" : "p";
            copy[rank][file] = ".";
            if (white) {
                if (!Game.kingChecked(true, copy)) {
                    possibleMovesLogicList.add((rank + direction) + "" + file);
                    moves.add((rank + direction) + "" + file);
                }
            } else {
                if (!Game.kingChecked(false, copy)) {
                    possibleMovesLogicList.add((rank + direction) + "" + file);
                    moves.add(7 - (rank + direction) + "" + (7 - file));
                }
            }
        }
        copy = copyBoard(board);
        if (rank == startRank) {
            if (board[rank + direction * 2][file].equals(".")) {
                copy[rank + direction * 2][file] = white ? "P" : "p";
                copy[rank][file] = ".";
                if (white) {
                    if (!Game.kingChecked(true, copy)) {
                        possibleMovesLogicList.add((rank + direction * 2) + "" + file);
                        moves.add((rank + direction * 2) + "" + file);
                    }
                } else {
                    if (!Game.kingChecked(false, copy)) {
                        possibleMovesLogicList.add((rank + direction * 2) + "" + file);
                        moves.add(7 - (rank + direction * 2) + "" + (7 - file));
                    }
                }
            }
        }
        copy = copyBoard(board);
        if (white) {
            if (isValidSquare(rank - 1, file - 1) && board[rank - 1][file - 1].matches("[pqrnb]")) {
                copy[rank - 1][file - 1] = "P";
                copy[rank][file] = ".";
                if (!Game.kingChecked(true, copy)) {
                    possibleMovesLogicList.add((rank - 1) + "" + (file - 1));
                    moves.add((rank - 1) + "" + (file - 1));
                    copy = copyBoard(board);
                }
            }
            if (isValidSquare(rank - 1, file + 1) && board[rank - 1][file + 1].matches("[pqrnb]")) {
                copy[rank - 1][file + 1] = "P";
                copy[rank][file] = ".";
                if (!Game.kingChecked(true, copy)) {
                    possibleMovesLogicList.add((rank - 1) + "" + (file + 1));
                    moves.add((rank - 1) + "" + (file + 1));
                }
            }
        } else {
            if (isValidSquare(rank + 1, file - 1) && board[rank + 1][file - 1].matches("[PQRNB]")) {
                copy[rank + 1][file - 1] = "p";
                copy[rank][file] = ".";
                if (!Game.kingChecked(false, copy)) {
                    possibleMovesLogicList.add((rank + 1) + "" + (file - 1));
                    moves.add((7 - (rank + 1)) + "" + (7 - (file - 1)));
                    copy = copyBoard(board);
                }
            }
            if (isValidSquare(rank + 1, file + 1) && board[rank + 1][file + 1].matches("[PQRNB]")) {
                copy[rank + 1][file + 1] = "p";
                copy[rank][file] = ".";
                if (!Game.kingChecked(false, copy)) {
                    possibleMovesLogicList.add((rank + 1) + "" + (file + 1));
                    moves.add((7 - (rank + 1)) + "" + (7 - (file + 1)));
                }
            }
        }
        return moves;
    }

    private static boolean isValidSquare(int rank, int file) {
        return rank >= 0 && rank < 8 && file >= 0 && file < 8;
    }
}
