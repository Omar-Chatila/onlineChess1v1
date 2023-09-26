package chessModel;

import java.util.ArrayList;
import java.util.List;

import static util.GameHelper.copyBoard;

public class PawnMoveTracker {
    private static final List<String> possibleMovesLogicList = new ArrayList<>();
    private static String whiteEnpassant;
    private static String blackEnpassant;


    private static boolean validatePawn(String[][] board, String move, boolean white) {
        if (!move.contains("x")) {
            setEnPassantSquare(move, white);
            int file = move.charAt(0) - 'a', rank = Character.getNumericValue(move.charAt(1));
            if (file < 0 || file > 7 || rank < 0 || rank > 8 || !board[8 - rank][file].equals(".")) return false;
            if (white) {
                if (rank < 3) return false;
                return rank != 4 && board[8 - (rank - 1)][file].equals("P")
                        || rank == 4 && (board[8 - (rank - 1)][file].equals("P") || (board[8 - (rank - 2)][file].equals("P") && board[8 - (rank - 1)][file].equals(".")));
            } else {
                if (rank > 6) return false;
                return rank != 5 && board[8 - rank - 1][file].equals("p")
                        || rank == 5 && (board[8 - rank - 1][file].equals("p") || (board[8 - rank - 2][file].equals("p") && board[8 - rank - 1][file].equals(".")));
            }
        }

        if (move.contains("x") && move.length() == 4) {
            if (white && blackEnpassant != null && blackEnpassant.equals(move.substring(2))) {
                return true;
            } else if (!white && whiteEnpassant != null && whiteEnpassant.equals(move.substring(2))) {
                return true;
            }
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
            String[][] copy = copyBoard(board);
            int file = move.charAt(0) - 'a', rank = 8 - Character.getNumericValue(move.charAt(move.contains("x") ? 3 : 1));
            if (move.contains("x")) {
                String piece = Character.toString(move.charAt(move.length() - 1));
                String newPiece = white ? piece : piece.toLowerCase();
                int capFile = move.charAt(2) - 'a';
                copy[rank + (white ? 1 : -1)][file] = ".";
                copy[rank][capFile] = newPiece;
            } else {
                copy[rank + (white ? 1 : -1)][file] = ".";
                copy[rank][file] = white ? Character.toString(move.charAt(3)) : Character.toString(move.charAt(3)).toLowerCase();
            }
            if (!Game.kingChecked(white, copy)) {
                Game.board = copy;
                return true;
            }
            return false;
        }
        if (validatePawn(board, move, white) && !move.contains("x")) {
            int file = move.charAt(0) - 'a', rank = 8 - Character.getNumericValue(move.charAt(1));
            String[][] copy = copyBoard(board);
            if (rank == (white ? 4 : 3)) {
                if (copy[rank + (white ? 2 : -2)][file].equals(white ? "P" : "p") && copy[rank + (white ? 1 : -1)][file].equals(".")) {
                    copy[rank + (white ? 2 : -2)][file] = ".";
                }
            } else {
                copy[rank + (white ? 1 : -1)][file] = ".";
            }
            copy[rank][file] = white ? "P" : "p";
            if (!Game.kingChecked(white, copy)) {
                Game.board = copy;
                return true;
            }
            return false;
        } else if (validatePawn(board, move, white) && move.contains("x")) {
            int pawnFile = move.charAt(0) - 'a', capFile = move.charAt(2) - 'a';
            int capRank = 8 - Character.getNumericValue(move.charAt(3));
            int pawnRank = white ? capRank + 1 : capRank - 1;
            String[][] copy = copyBoard(board);
            if (white && blackEnpassant != null && blackEnpassant.equals(move.substring(2))) {
                copy[3][capFile] = ".";
            } else if (!white && whiteEnpassant != null && whiteEnpassant.equals(move.substring(2))) {
                copy[4][capFile] = ".";
            }
            copy[pawnRank][pawnFile] = ".";
            copy[capRank][capFile] = white ? "P" : "p";
            if (!Game.kingChecked(white, copy)) {
                Game.board = copy;
                return true;
            }
            return false;
        }
        return false;
    }

    public static void resetEnpassant() {
        if (blackEnpassant != null) {
            String move = blackEnpassant.charAt(0) + "" + 5;
            if (Game.moveList.indexOf(move) != Game.moveList.size() - 1) {
                blackEnpassant = null;
            }
        }
        if (whiteEnpassant != null) {
            String move = whiteEnpassant.charAt(0) + "" + 4;
            if (Game.moveList.indexOf(move) != Game.moveList.size() - 1) {
                whiteEnpassant = null;
            }
        }

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

    public static void setEnPassantSquare(String move, boolean white) {
        if (white && move.matches("[a-h]4") && Game.board[5][move.charAt(0) - 'a'].matches(".") && Game.board[6][move.charAt(0) - 'a'].matches("P")) {
            whiteEnpassant = move.charAt(0) + "" + 3;

        } else if (!white && move.matches("[a-h]5") && Game.board[2][move.charAt(0) - 'a'].matches(".") && Game.board[1][move.charAt(0) - 'a'].matches("p")) {
            blackEnpassant = move.charAt(0) + "" + 6;
        } else {
            blackEnpassant = null;
            whiteEnpassant = null;
        }
    }

    public static List<String> possibleMoves(String[][] board, int rank, int file, boolean white) {
        possibleMovesLogicList.clear();
        List<String> moves = new ArrayList<>();
        String[][] copy = copyBoard(board);
        int logicRank = rank;
        int logicFile = file;
        if (!white) {
            logicRank = 7 - logicRank;
            logicFile = 7 - logicFile;
        }
        int startRank = 6;
        int direction = white ? -1 : 1;
        if (isValidSquare(logicRank + direction, logicFile) && board[logicRank + direction][logicFile].equals(".")) {
            copy[logicRank + direction][logicFile] = white ? "P" : "p";
            copy[logicRank][logicFile] = ".";
            if (white) {
                if (!Game.kingChecked(true, copy)) {
                    possibleMovesLogicList.add((logicRank + direction) + "" + logicFile);
                    moves.add((rank + direction) + "" + file);
                }
            } else {
                if (!Game.kingChecked(false, copy)) {
                    possibleMovesLogicList.add((logicRank + direction) + "" + logicFile);
                    moves.add((rank - direction) + "" + file);
                }
            }
        }
        copy = copyBoard(board);
        if (rank == startRank) {
            if (board[logicRank + direction * 2][logicFile].equals(".") && board[logicRank + direction][logicFile].equals(".")) {
                copy[logicRank + direction * 2][logicFile] = white ? "P" : "p";
                copy[logicRank][logicFile] = ".";
                if (white) {
                    if (!Game.kingChecked(true, copy)) {
                        possibleMovesLogicList.add((rank + direction * 2) + "" + file);
                        moves.add((rank + direction * 2) + "" + file);
                    }
                } else {
                    if (!Game.kingChecked(false, copy)) {
                        possibleMovesLogicList.add((rank + direction * 2) + "" + file);
                        moves.add((rank - (direction * 2)) + "" + file);
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
                }
            }
            copy = copyBoard(board);
            if (isValidSquare(rank - 1, file + 1) && board[rank - 1][file + 1].matches("[pqrnb]")) {
                copy[rank - 1][file + 1] = "P";
                copy[rank][file] = ".";
                if (!Game.kingChecked(true, copy)) {
                    possibleMovesLogicList.add((rank - 1) + "" + (file + 1));
                    moves.add((rank - 1) + "" + (file + 1));
                }
            }
            copy = copyBoard(board);
            if (blackEnpassant != null) {
                int r = 8 - Integer.parseInt(Character.getNumericValue(blackEnpassant.charAt(1)) + "");
                int f = blackEnpassant.charAt(0) - 'a';
                copy[r][f] = "P";
                copy[rank][file] = ".";
                if (!Game.kingChecked(true, copy)) {
                    if (rank == r + 1 && Math.abs(file - f) == 1) {
                        moves.add(r + "" + f);
                        possibleMovesLogicList.add(r + "" + f);
                    }
                }
            }
        } else {
            if (isValidSquare(logicRank + 1, logicFile - 1) && board[logicRank + 1][logicFile - 1].matches("[PQRNB]")) {
                copy[logicRank + 1][logicFile - 1] = "p";
                copy[logicRank][logicFile] = ".";
                if (!Game.kingChecked(false, copy)) {
                    possibleMovesLogicList.add((logicRank + 1) + "" + (logicFile - 1));
                    moves.add(((rank - 1)) + "" + ((file + 1)));
                }
            }
            copy = copyBoard(board);
            if (isValidSquare(logicRank + 1, logicFile + 1) && board[logicRank + 1][logicFile + 1].matches("[PQRNB]")) {
                copy[logicRank + 1][logicFile + 1] = "p";
                copy[logicRank][logicFile] = ".";
                if (!Game.kingChecked(false, copy)) {
                    possibleMovesLogicList.add((logicRank + 1) + "" + (logicFile + 1));
                    moves.add(((rank - 1)) + "" + ((file - 1)));
                }
            }
            copy = copyBoard(board);
            if (whiteEnpassant != null) {
                int r = 2;
                int f = 7 - (whiteEnpassant.charAt(0) - 'a');
                copy[7 - r][7 - f] = "p";
                copy[logicRank][logicFile] = ".";
                if (!Game.kingChecked(false, copy)) {
                    if (rank == r + 1 && Math.abs(file - f) == 1) {
                        moves.add(r + "" + f);
                        possibleMovesLogicList.add((7 - r) + "" + (7 - f));
                    }
                }
            }

        }
        return moves;
    }

    private static boolean isValidSquare(int rank, int file) {
        return rank >= 0 && rank < 8 && file >= 0 && file < 8;
    }
}
