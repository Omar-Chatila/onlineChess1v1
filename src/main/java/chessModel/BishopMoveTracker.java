package chessModel;

public class BishopMoveTracker {
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
        int[] dx = {-1, 1, -1, 1};
        int[] dy = {-1, 1, 1, -1};
        for (int d = 0; d < 4; d++) {
            int i = 1;
            while (isValidSquare(rank + i * dy[d], file + i * dx[d])) {
                String squareContent = board[rank + i * dy[d]][file + i * dx[d]];
                if (squareContent.matches("B") && white) {
                    board[rank + i * dy[d]][file + i * dx[d]] = ".";
                    board[rank][file] = "B";
                    return true;
                } else if (squareContent.matches("b") && !white) {
                    board[rank + i * dy[d]][file + i * dx[d]] = ".";
                    board[rank][file] = "b";
                    return true;
                } else if (!squareContent.matches("[bB.]")) {
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
