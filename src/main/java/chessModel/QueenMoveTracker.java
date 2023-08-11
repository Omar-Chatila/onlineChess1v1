package chessModel;

public class QueenMoveTracker {
	public static boolean validateQueen(String[][] board, String move, boolean white) {
		if (!move.contains("x")) {
			int file = move.charAt(1) - 'a';
			int rank = 8 - Character.getNumericValue(move.charAt(2));
			return validateQueenHelper(board, rank, file, white);
		} else {
			int file = move.charAt(2) - 'a';
			int rank = 8 - Character.getNumericValue(move.charAt(3));
			if (!white && board[rank][file].matches("[NPKBRQ]")) {
				return validateQueenHelper(board, rank, file, white);
			} else if (white && board[rank][file].matches("[npkqrb]")) {
				return validateQueenHelper(board, rank, file, white);
			}
		}
		return false;
	}

	private static boolean validateQueenHelper(String[][] board, int rank, int file, boolean white) {
		int[] dx = { -1, 1, -1, 1, 0, 0, 1, -1 };
		int[] dy = { -1, 1, 1, -1, 1, -1, 0, 0 };
		for (int d = 0; d < 8; d++) {
			int i = 1;
			while (isValidSquare(rank + i * dy[d], file + i * dx[d])) {
				String squareContent = board[rank + i * dy[d]][file + i * dx[d]];
				if (squareContent.matches("[Q]") && white) {
					board[rank + i * dy[d]][file + i * dx[d]] = ".";
					board[rank][file] = "Q";
					return true;
				} else if (squareContent.matches("[q]") && !white) {
					board[rank + i * dy[d]][file + i * dx[d]] = ".";
					board[rank][file] = "q";
					return true;
				} else if (!squareContent.matches("[qQ.]")) {
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
