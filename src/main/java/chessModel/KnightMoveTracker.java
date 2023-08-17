package chessModel;

public class KnightMoveTracker {
    private static final int[] offsetY = {-2, -1, 1, 2, -2, -1, 1, 2};
    private static final int[] offsetX = {-1, -2, -2, -1, 1, 2, 2, 1};

    public static boolean validateKnight(String[][] board, String move, boolean white) { // TODO: zuege wie NEd5 oder N5e4 //																					// ber�cksichtigen fehlt
        if (!move.contains("x")) {
            int file = move.charAt(1) - 'a';
            int rank = 8 - Character.getNumericValue(move.charAt(2));
            for (int i = 0; i < 8; i++) {
                if (rank + offsetY[i] >= 0 && rank + offsetY[i] < 8 && file + offsetX[i] >= 0
                        && file + offsetX[i] < 8) {
                    int rankY = rank + offsetY[i];
                    int fileX = file + offsetX[i];
                    if (board[rankY][fileX].equals("N") && white) {
                        board[rankY][fileX] = ".";
                        board[rank][file] = "N";
                        return !Game.kingChecked(true);
                    } else if (board[rankY][fileX].equals("n") && !white) {
                        board[rankY][fileX] = ".";
                        board[rank][file] = "n";
                        return !Game.kingChecked(false);
                    }
                }
            }
        } else {
            int file = move.charAt(2) - 'a';
            int rank = 8 - Character.getNumericValue(move.charAt(3));
            for (int i = 0; i < 8; i++) {
                if (rank + offsetY[i] >= 0 && rank + offsetY[i] < 8 && file + offsetX[i] >= 0
                        && file + offsetX[i] < 8) {
                    int rankY = rank + offsetY[i];
                    int fileX = file + offsetX[i];
                    if (board[rankY][fileX].equals("N") && white) {
                        if (board[rank][file].matches("[npkqrb]")) {
                            board[rankY][fileX] = ".";
                            board[rank][file] = "N";
                        }
                        return !Game.kingChecked(true);
                    } else if (board[rankY][fileX].equals("n") && !white) {
                        if (board[rank][file].matches("[NPKBRQ]")) {
                            board[rankY][fileX] = ".";
                            board[rank][file] = "n";
                        }
                        return !Game.kingChecked(false);
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

    private static boolean isValidSquare(int rank, int file) {
        return rank >= 0 && rank < 8 && file >= 0 && file < 8;
    }
}
