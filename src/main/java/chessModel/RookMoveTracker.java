package chessModel;

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
                    return Game.kingChecked(false);
                } else if (squareContent.matches("r") && !white) {
                    board[rank + i * dy[d]][file + i * dx[d]] = ".";
                    board[rank][file] = "r";
                    return Game.kingChecked(false);
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

    private static boolean isValidSquare(int rank, int file) {
        return rank >= 0 && rank < 8 && file >= 0 && file < 8;
    }
}
