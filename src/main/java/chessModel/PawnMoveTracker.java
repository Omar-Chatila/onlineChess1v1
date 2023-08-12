package chessModel;

public class PawnMoveTracker {
    public static boolean validatePawn(String[][] board, String move, boolean white) {
        if (!move.contains("x")) {
            int file = move.charAt(0) - 'a';
            int rank = Character.getNumericValue(move.charAt(1));
            if (file < 0 || file > 7 || rank < 0 || rank > 7 || !board[8 - rank][file].equals(".")) {
                return false;
            }
            if (white) {
                if (rank < 3)
                    return false;
                if (rank != 4 && board[8 - (rank - 1)][file].equals("P")) {
                    return true;
                } else return rank == 4
                        && (board[8 - (rank - 1)][file].equals("P") || board[8 - (rank - 2)][file].equals("P"));
            } else {
                if (rank > 6)
                    return false;
                if (rank != 5 && board[8 - rank - 1][file].equals("p")) {
                    return true;
                } else return rank == 5
                        && (board[8 - rank - 1][file].equals("p") || board[8 - rank - 2][file].equals("p"));
            }
        }
        if (move.contains("x") && move.length() == 4) {
            int pawnFile = move.charAt(0) - 'a';
            int capFile = move.charAt(2) - 'a';
            int capRank = 8 - Character.getNumericValue(move.charAt(3));
            int pawnRank = white ? capRank + 1 : capRank - 1;
            if (pawnFile < 0 || pawnFile > 7 || pawnRank < 0 || pawnRank > 7 || capFile < 0 || capFile > 7
                    || capRank < 0 || capRank > 7) {
                return false;
            }
            if (white) {
                return board[pawnRank][pawnFile].equals("P") && board[capRank][capFile].matches("[npkqrb]");
            } else {
                return board[pawnRank][pawnFile].equals("p") && board[capRank][capFile].matches("[NPKQRB]");
            }
        }
        return false;
    }

    public static boolean movePawn(String[][] board, String move, boolean white) {
        if (validatePawn(board, move, white) && !move.contains("x")) {
            int file = move.charAt(0) - 'a';
            int rank = 8 - Character.getNumericValue(move.charAt(1));
            if (white) {
                if (rank == 4) {
                    board[rank + 1][file] = ".";
                    board[rank + 2][file] = ".";
                } else {
                    board[rank + 1][file] = ".";
                }
                board[rank][file] = "P";
            } else {
                if (rank == 3) {
                    board[rank - 1][file] = ".";
                    board[rank - 2][file] = ".";
                } else {
                    board[rank - 1][file] = ".";
                }
                board[rank][file] = "p";
            }
            return true;
        } else if (validatePawn(board, move, white) && move.contains("x")) {
            int pawnFile = move.charAt(0) - 'a';
            int capFile = move.charAt(2) - 'a';
            int capRank = 8 - Character.getNumericValue(move.charAt(3));
            int pawnRank = white ? capRank + 1 : capRank - 1;

            if (white) {
                board[pawnRank][pawnFile] = ".";
                board[capRank][capFile] = "P";
            } else {
                board[pawnRank][pawnFile] = ".";
                board[capRank][capFile] = "p";
            }
            return true;
        }
        return false;
    }
}
